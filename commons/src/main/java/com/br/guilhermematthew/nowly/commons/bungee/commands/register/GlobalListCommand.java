package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.account.BungeePlayer;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.bungee.manager.premium.PremiumMapManager;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GlobalListCommand implements CommandClass {

    @SuppressWarnings("deprecation")
    @Command(name = "globallist", aliases = {"glist", "gl"}, groupsToUse = {Groups.ADMIN}, runAsync = true)
    public void glist(BungeeCommandSender commandSender, String label, String[] args) {
        int crackeds = 0,
                premiums = 0;

        commandSender.sendMessage("");

        for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
            List<String> players = new ArrayList<>();

            for (ProxiedPlayer player : server.getPlayers()) {
                boolean premium = PremiumMapManager.getPremiumMap(player.getName()).isPremium();

                if (premium) {
                    premiums++;
                } else {
                    crackeds++;
                }

                if (CommonsGeneral.getProfileManager().containsProfile(player.getName())) {
                    final BungeePlayer proxyPlayer = BungeeMain.getBungeePlayer(player.getName());

                    if (premium) {
                        players.add(proxyPlayer.getGroup().getColor() + player.getDisplayName());
                    } else {
                        players.add(proxyPlayer.getGroup().getColor() + player.getDisplayName());
                    }

                } else {
                    players.add("§7" + player.getDisplayName());
                }
            }

            Collections.sort(players, String.CASE_INSENSITIVE_ORDER);

            commandSender.sendMessage("§6" + server.getName() + " [" + server.getPlayers().size() + "] " +
                    Util.format(players, ChatColor.RESET + ", "));
        }

        commandSender.sendMessage("");
        commandSender.sendMessage("§fNo momento há §a" + ProxyServer.getInstance().getPlayers().size() + " §fjogadores em toda rede!");
        commandSender.sendMessage("");
        commandSender.sendMessage("§fNo momento há §6" + premiums + " §fjogadores §6§lORIGINAIS §fonline.");
        commandSender.sendMessage("§fNo momento há §c" + crackeds + " §fjogadores §c§lPIRATAS §fonline.");
        commandSender.sendMessage("");
    }
}