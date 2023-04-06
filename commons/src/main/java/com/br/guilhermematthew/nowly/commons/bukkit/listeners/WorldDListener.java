package com.br.guilhermematthew.nowly.commons.bukkit.listeners;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class WorldDListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] msg) {

        if (channel.equals("WDL|INIT")) {

            p.kickPlayer(
                    "§cSaudações!\n\n§cDetectamos o uso indevido de\n§cWorldDownloader via canal alternativo.\n§cPedimos que, para iniciar sua jogatina\n§cretire o client atual para isso.\n\n§cAtenciosamente, Equipe LeagueMC.");

        }

    }
}
