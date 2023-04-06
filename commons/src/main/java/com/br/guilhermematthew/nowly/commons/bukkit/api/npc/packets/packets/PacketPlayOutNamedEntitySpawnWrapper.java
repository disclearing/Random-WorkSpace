package com.br.guilhermematthew.nowly.commons.bukkit.api.npc.packets.packets;

import com.br.guilhermematthew.nowly.commons.bukkit.utility.Reflection;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import org.bukkit.Location;

import java.util.UUID;

public class PacketPlayOutNamedEntitySpawnWrapper {

    public PacketPlayOutNamedEntitySpawn create(UUID uuid, Location location, int entityId, int itemStackID) {
        PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn();

        Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "a", int.class)
                .set(packetPlayOutNamedEntitySpawn, entityId);

        Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "b", UUID.class)
                .set(packetPlayOutNamedEntitySpawn, uuid);

        Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "c", int.class)
                .set(packetPlayOutNamedEntitySpawn, (int) Math.floor(location.getX() * 32.0D));

        Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "d", int.class)
                .set(packetPlayOutNamedEntitySpawn, (int) Math.floor(location.getY() * 32.0D));

        Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "e", int.class)
                .set(packetPlayOutNamedEntitySpawn, (int) Math.floor(location.getZ() * 32.0D));

        Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "f", byte.class)
                .set(packetPlayOutNamedEntitySpawn, (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));

        Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "g", byte.class)
                .set(packetPlayOutNamedEntitySpawn, (byte) ((int) (location.getPitch() * 256.0F / 360.0F)));

        Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "h", int.class)
                .set(packetPlayOutNamedEntitySpawn, itemStackID);

        DataWatcher dataWatcher = new DataWatcher(null);
        dataWatcher.a(10, (byte) 127);

        Reflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "i", DataWatcher.class)
                .set(packetPlayOutNamedEntitySpawn, dataWatcher);

        return packetPlayOutNamedEntitySpawn;
    }
}