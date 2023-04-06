package com.br.guilhermematthew.nowly.commons.custompackets;

import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketAction;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketCustomAction;
import com.br.guilhermematthew.servercommunication.ServerCommunication;
import com.br.guilhermematthew.servercommunication.common.packet.CommonPacket;

import java.util.HashMap;
import java.util.Map;

public class PacketsManager {

    private static final Map<String, Class<?>> MAP_CLASS = new HashMap<String, Class<?>>();

    static {
        register(CPacketAction.class);
        register(CPacketCustomAction.class);
    }

    public static void register(Class<? extends CommonPacket> packetClass) {
        MAP_CLASS.put(packetClass.getSimpleName(), packetClass);
    }

    public static CommonPacket getPacket(final String packetName) {
        try {
            return (CommonPacket) MAP_CLASS.get(packetName).newInstance();
        } catch (Exception ex) {
            ServerCommunication.debug("PACKET MANAGER", "An error ocurred on trying get packet with the name -> " + packetName);
            return null;
        }
    }
}