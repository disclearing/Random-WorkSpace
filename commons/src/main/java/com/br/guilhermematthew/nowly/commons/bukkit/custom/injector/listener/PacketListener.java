package com.br.guilhermematthew.nowly.commons.bukkit.custom.injector.listener;

import com.br.guilhermematthew.nowly.commons.bukkit.custom.injector.PacketObject;

public interface PacketListener {

    void onPacketReceiving(PacketObject packetObject);

    void onPacketSending(PacketObject packetObject);
}
