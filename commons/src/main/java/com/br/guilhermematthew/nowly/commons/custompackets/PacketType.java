package com.br.guilhermematthew.nowly.commons.custompackets;

public enum PacketType {

    UNKNOWN,
    BUKKIT_RECEIVE_SKIN_DATA,
    BUKKIT_RECEIVE_ACCOUNT_FROM_BUNGEECORD,
    BUKKIT_REQUEST_ACCOUNT_TO_BUNGEECORD,
    BUKKIT_SEND_UPDATED_DATA,
    BUKKIT_PLAYER_RESPAWN,
    BUKKIT_SEND_SERVER_DATA,
    BUKKIT_SEND_INFO,

    BUNGEE_SET_FAST_SKIN,
    BUNGEE_SET_RANDOM_SKIN,
    BUNGEE_SET_SKIN,
    BUNGEE_UPDATE_SKIN,
    BUKKIT_GO,
    BUNGEE_SEND_UPDATED_STATS,
    BUNGEE_SEND_KICK,
    BUNGEE_SEND_PLAYER_ACTION,
    BUNGEE_SEND_INFO;

    public static PacketType getType(String name) {
        PacketType type = PacketType.UNKNOWN;

        for (PacketType types : values()) {
            if (types.name().equalsIgnoreCase(name)) {
                type = types;
                break;
            }
        }

        return type;
    }
}