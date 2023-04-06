package com.br.guilhermematthew.nowly.commons.bukkit.custom.injector.listener;

import java.util.ArrayList;

public class PacketListenerAPI {

    private static final ArrayList<PacketListener> listeners = new ArrayList<>();

    public static ArrayList<PacketListener> getListeners() {
        return listeners;
    }

    public static void addListener(PacketListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(PacketListener listener) {
        listeners.remove(listener);
    }
}
