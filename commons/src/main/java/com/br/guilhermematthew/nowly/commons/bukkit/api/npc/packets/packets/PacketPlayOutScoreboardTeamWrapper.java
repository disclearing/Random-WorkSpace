package com.br.guilhermematthew.nowly.commons.bukkit.api.npc.packets.packets;

import com.br.guilhermematthew.nowly.commons.bukkit.utility.Reflection;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;

import java.util.Collection;

public class PacketPlayOutScoreboardTeamWrapper {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public PacketPlayOutScoreboardTeam createRegisterTeam(String name, String color) {
        PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeam = new PacketPlayOutScoreboardTeam();

        Reflection.getField(packetPlayOutScoreboardTeam.getClass(), "h", int.class)
                .set(packetPlayOutScoreboardTeam, 0);

        Reflection.getField(packetPlayOutScoreboardTeam.getClass(), "b", String.class)
                .set(packetPlayOutScoreboardTeam, color + name);

        Reflection.getField(packetPlayOutScoreboardTeam.getClass(), "a", String.class)
                .set(packetPlayOutScoreboardTeam, color + name);

        Reflection.getField(packetPlayOutScoreboardTeam.getClass(), "e", String.class)
                .set(packetPlayOutScoreboardTeam, "never");

        Reflection.getField(packetPlayOutScoreboardTeam.getClass(), "i", int.class)
                .set(packetPlayOutScoreboardTeam, 1);
        // Could not get this working in the PacketPlayOutPlayerInfoWrapper class.

        Reflection.getField(packetPlayOutScoreboardTeam.getClass(), "c", String.class)
                .set(packetPlayOutScoreboardTeam, "" + color);

        Reflection.FieldAccessor<Collection> collectionFieldAccessor = Reflection.getField(
                packetPlayOutScoreboardTeam.getClass(), "g", Collection.class);

        Collection collection = collectionFieldAccessor.get(packetPlayOutScoreboardTeam);
        collection.add(name);
        collectionFieldAccessor.set(packetPlayOutScoreboardTeam, collection);

        return packetPlayOutScoreboardTeam;
    }

    public PacketPlayOutScoreboardTeam createUnregisterTeam(String name, String color) {
        PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeam = new PacketPlayOutScoreboardTeam();

        Reflection.getField(packetPlayOutScoreboardTeam.getClass(), "h", int.class)
                .set(packetPlayOutScoreboardTeam, 1);
        Reflection.getField(packetPlayOutScoreboardTeam.getClass(), "a", String.class)
                .set(packetPlayOutScoreboardTeam, color + name);

        return packetPlayOutScoreboardTeam;
    }
}