package com.br.guilhermematthew.servercommunication.common.packet;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PacketListenerManager {

    private final List<PacketEvent> packetMap;

    public PacketListenerManager() {
        packetMap = new ArrayList<>();
    }

    public void register(PacketEvent packetEvent) {
        this.packetMap.add(packetEvent);
    }

    public void unregister(PacketEvent packetEvent) {
        this.packetMap.remove(packetEvent);
    }

    public void call(final String packetString, final Socket socket) {
        this.packetMap.forEach(packetEvent -> packetEvent.onPacketReceive(packetString, socket));
    }

    public interface PacketEvent {

        void onPacketReceive(final String packetString, final Socket socket);

    }
}