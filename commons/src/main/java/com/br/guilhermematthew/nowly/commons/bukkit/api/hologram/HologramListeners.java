package com.br.guilhermematthew.nowly.commons.bukkit.api.hologram;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.api.protocol.ProtocolGetter;
import com.br.guilhermematthew.nowly.commons.bukkit.utility.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class HologramListeners {

    private final static String LOCKED_TAG = "LOCKED.HOLOGRAMS.TIME";
    private static Listener listener;

    public static void registerListeners() {

        listener = new Listener() {

            @EventHandler(priority = EventPriority.MONITOR)
            public void onJoin(PlayerJoinEvent event) {
                handleHolograms(event.getPlayer(), event.getPlayer().getLocation());
            }

            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                for (Hologram hologram : HologramAPI.getHolograms()) {
                    if (hologram.isSpawned()) {
                        hologram.clean(event.getPlayer());
                    }
                }
            }

            @EventHandler
            public void onRealMovement(PlayerMoveEvent event) {
                if(!LocationUtil.isRealMovement(event.getFrom(), event.getTo())) return;

                Player player = event.getPlayer();
                if (isLocked(player)) return;

                handleHolograms(player, event.getTo());
            }

            @EventHandler
            public void onTeleport(PlayerTeleportEvent e) {
                Player player = e.getPlayer();
                if (isLocked(player)) return;

                handleHolograms(player, e.getTo());
            }
        };

        Bukkit.getServer().getPluginManager().registerEvents(listener, BukkitMain.getInstance());
    }

    public static void unregisterListeners() {
        HandlerList.unregisterAll(listener);
        listener = null;
    }

    public static void handleHolograms(Player player, Location location) {
        lock(player);

        for (Hologram hologram : HologramAPI.getHolograms()) {
            if (!hologram.isSpawned()) continue;
            if (!hologram.getLocation().getWorld().getName().equals(location.getWorld().getName())) continue;

            if (hologram.getLocation().distance(location) <= 80) {

                boolean spawn = true;

                if (hologram.getName().equalsIgnoreCase("name")) {
                    if (ProtocolGetter.getVersion(player) < 6) spawn = false;
                }

                if (spawn) {
                    try {
                        HologramAPI.spawnSingle(hologram, player);
                    } catch (Exception ex) {
                    }
                }
            } else {
                try {
                    HologramAPI.despawnSingle(hologram, player);
                } catch (Exception ex) {
                }
            }
        }

        removeLock(player);
    }

    public static void lock(Player player) {
        lock(player, 1500L);
    }

    public static void removeLock(Player player) {
        player.removeMetadata(LOCKED_TAG, BukkitMain.getInstance());
    }

    public static void lock(Player player, Long time) {
        player.setMetadata(LOCKED_TAG, new FixedMetadataValue(BukkitMain.getInstance(), System.currentTimeMillis() + time));
    }

    private static boolean isLocked(final Player player) {
        if (!player.hasMetadata(LOCKED_TAG)) return false;
        return player.getMetadata(LOCKED_TAG).get(0).asLong() > System.currentTimeMillis();
    }
}