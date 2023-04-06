package com.br.guilhermematthew.nowly.commons.bukkit.api.npc.api;

import org.bukkit.entity.Player;

interface PacketHandler {

    void createPackets();

    void sendShowPackets(Player player);

    void sendHidePackets(Player player, boolean scheduler);
}