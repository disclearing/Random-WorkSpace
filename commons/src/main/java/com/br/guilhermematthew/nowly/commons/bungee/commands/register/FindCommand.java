package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class FindCommand implements CommandClass {

    @Command(name = "find", aliases = {"procurar"}, groupsToUse = {Groups.PRIME})
    public void find(BungeeCommandSender commandSender, String label, String[] args) {
        if (args.length != 1) {
            commandSender.sendMessage("§cUse: /find <Nick>");
            return;
        }

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);

        if ((player == null) || (player.getServer() == null)) {
            commandSender.sendMessage(BungeeMessages.JOGADOR_OFFLINE);
        } else {
            commandSender.sendMessage(BungeeMessages.PLAYER_FINDED.replace("%servidor%", player.getServer().getInfo().getName()));
        }
    }
}