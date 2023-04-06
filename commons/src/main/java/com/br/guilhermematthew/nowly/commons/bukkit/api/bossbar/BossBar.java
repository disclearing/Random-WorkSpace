package com.br.guilhermematthew.nowly.commons.bukkit.api.bossbar;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@Getter
@Setter
public class BossBar {

    private Player player;
    private EntityWither wither;
    private boolean cancelar, tempo;
    private int segundos;

    public BossBar(final Player player, final String message, final boolean tempo) {
        setPlayer(player);
        setCancelar(false);
        setTempo(tempo);
        setSegundos(20);

        setWither(new EntityWither(((CraftWorld) player.getWorld()).getHandle()));

        getWither().setInvisible(true);
        getWither().setCustomName(message);
        getWither().getEffects().clear();
        getWither().setLocation(getViableLocation().getX(), getViableLocation().getY(), getViableLocation().getZ(), 0, 0);

        ((CraftPlayer) getPlayer()).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(getWither()));
    }

    public void onSecond() {
        if (isCancelar()) {
            destroy();
            return;
        }

        if (!getPlayer().isOnline()) {
            setCancelar(true);
            return;
        }

        if (!isTempo()) {
            update();
            return;
        }

        if (getSegundos() == 0) {
            destroy();
            setCancelar(true);
            return;
        }

        segundos--;
        update();
    }

    public void update() {
        if (wither == null || player == null) return;

        wither.setLocation(getViableLocation().getX(), getViableLocation().getY(), getViableLocation().getZ(), 0, 0);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityTeleport(wither));
    }

    public Location getViableLocation() {
        if (player == null)
            return new Location(Bukkit.getWorld("world"), 0, 0, 0);
        return player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(28));
    }

    public void destroy() {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(wither.getId()));
    }
}