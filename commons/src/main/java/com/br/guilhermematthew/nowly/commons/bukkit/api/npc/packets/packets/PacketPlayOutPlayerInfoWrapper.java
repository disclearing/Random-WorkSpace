package com.br.guilhermematthew.nowly.commons.bukkit.api.npc.packets.packets;

import com.br.guilhermematthew.nowly.commons.bukkit.api.npc.api.wrapper.GameProfileWrapper;
import com.br.guilhermematthew.nowly.commons.bukkit.utility.Reflection;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.WorldSettings;

import java.util.List;

public class PacketPlayOutPlayerInfoWrapper {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public PacketPlayOutPlayerInfo create(PacketPlayOutPlayerInfo.EnumPlayerInfoAction action, GameProfileWrapper gameProfileWrapper, String name) {
        GameProfile gameProfile = (GameProfile) gameProfileWrapper.getGameProfile();

        PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo();
        Reflection.getField(packetPlayOutPlayerInfo.getClass(), "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.class)
                .set(packetPlayOutPlayerInfo, action);

        PacketPlayOutPlayerInfo.PlayerInfoData playerInfoData = packetPlayOutPlayerInfo.new PlayerInfoData(gameProfile, 1,
                WorldSettings.EnumGamemode.NOT_SET, IChatBaseComponent.ChatSerializer.a(name));

        Reflection.FieldAccessor<List> fieldAccessor = Reflection.getField(packetPlayOutPlayerInfo.getClass(),
                "b", List.class);

        List<PacketPlayOutPlayerInfo.PlayerInfoData> list = fieldAccessor.get(packetPlayOutPlayerInfo);
        list.add(playerInfoData);
        fieldAccessor.set(packetPlayOutPlayerInfo, list);

        return packetPlayOutPlayerInfo;
    }
}