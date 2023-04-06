package com.br.guilhermematthew.nowly.commons.bukkit.api.hologram;

import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.reflection.Reflection;
import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.reflection.minecraft.Minecraft;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class HologramAPI {

    protected static final List<Hologram> holograms = new ArrayList<>();
    public static boolean packetsEnabled = false;
    protected static final boolean is1_8/*Or 1.9*/ = Minecraft.VERSION.newerThan(Minecraft.Version.v1_8_R1);
    protected static final boolean useProtocolSupport = true;

    //NEW

    public static Hologram createHologramAndSpawn(final String name, final Location loc, String text) {
        Hologram hologram = new DefaultHologram(name, loc, text);
        hologram.spawn();
        holograms.add(hologram);
        return hologram;
    }


    /**
     * Creates a new {@link Hologram}
     *
     * @param loc  {@link Location} to spawn the hologram at
     * @param text Initial text content of the hologram
     * @return a new {@link Hologram}
     */

    public static boolean spawnSingle(@Nonnull final Hologram hologram, final Player p) throws Exception {
        if (hologram == null) {
            throw new IllegalArgumentException("hologram cannot be null");
        }

        if (!hologram.isShown(p)) {
            hologram.setShown(p, true);

            spawn(hologram, new LinkedList<>(Collections.singletonList(p)));
        }
        return true;
    }

    public static boolean despawnSingle(@Nonnull Hologram hologram, Player p) throws Exception {
        if (hologram == null) {
            throw new IllegalArgumentException("hologram cannot be null");
        }

        if (hologram.isShown(p)) {
            hologram.setShown(p, false);

            ((CraftHologram) hologram).sendDestroyPackets(new LinkedList<>(Collections.singletonList(p)));
        }
        return true;
    }

    public static Hologram createHologram(String name, Location loc, String text) {
        Hologram hologram = new DefaultHologram(name, loc, text);
        holograms.add(hologram);
        return hologram;
    }

    /**
     * Removes a {@link Hologram}
     *
     * @param loc  {@link Location} of the hologram
     * @param text content of the hologram
     * @return <code>true</code> if a hologram was found and has been removed, <code>false</code> otherwise
     */
    public static boolean removeHologram(Location loc, String text) {
        Hologram toRemove = null;
        for (Hologram h : holograms) {
            if (h.getLocation().equals(loc) && h.getText().equals(text)) {
                toRemove = h;
                break;
            }
        }
        if (toRemove != null) {
            return removeHologram(toRemove);
        }
        return false;
    }

    /**
     * Removes a {@link Hologram}
     *
     * @param hologram {@link Hologram} to remove
     * @return <code>true</code> if the hologram has been removed
     */
    public static boolean removeHologram(@Nonnull Hologram hologram) {
        if (hologram.isSpawned()) {
            hologram.despawn();
            hologram.clean();
        }
        return holograms.remove(hologram);
    }

    /**
     * @return {@link Collection} of all registered {@link Hologram}s
     */
    public static Collection<Hologram> getHolograms() {
        return new ArrayList<>(holograms);
    }

    protected static boolean spawn(@Nonnull final Hologram hologram, final Collection<? extends Player> receivers) throws Exception {
        if (hologram == null) {
            throw new IllegalArgumentException("hologram cannot be null");
        }

        checkReceiverWorld(hologram, receivers);

        if (!receivers.isEmpty()) {
            ((CraftHologram) hologram).sendSpawnPackets(receivers, true, true);
            ((CraftHologram) hologram).sendTeleportPackets(receivers, true, true);
            ((CraftHologram) hologram).sendNamePackets(receivers);
            ((CraftHologram) hologram).sendAttachPacket(receivers);
        }

        return true;
    }

    protected static boolean despawn(@Nonnull Hologram hologram, Collection<? extends Player> receivers) throws Exception {
        if (hologram == null) {
            throw new IllegalArgumentException("hologram cannot be null");
        }
        if (receivers.isEmpty()) {
            return false;
        }

        ((CraftHologram) hologram).sendDestroyPackets(receivers);
        return true;
    }

    protected static void sendPacket(Player p, Object packet) {
        if (p == null || packet == null) {
            throw new IllegalArgumentException("player and packet cannot be null");
        }
        try {
            Object handle = Reflection.getHandle(p);
            Object connection = Reflection.getFieldWithException(handle.getClass(), "playerConnection").get(handle);
            Reflection.getMethod(connection.getClass(), "sendPacket", Reflection.getNMSClass("Packet")).invoke(connection, packet);
        } catch (Exception e) {
        }
    }

    protected static Collection<? extends Player> checkReceiverWorld(final Hologram hologram, final Collection<? extends Player> receivers) {
        for (Iterator<? extends Player> iterator = receivers.iterator(); iterator.hasNext(); ) {
            Player next = iterator.next();
            if (!next.getWorld().equals(hologram.getLocation().getWorld())) {
                iterator.remove();
            }
        }
        return receivers;
    }

    public static boolean is1_8() {
        return is1_8;
    }

    public static boolean packetsEnabled() {
        return packetsEnabled;
    }
}