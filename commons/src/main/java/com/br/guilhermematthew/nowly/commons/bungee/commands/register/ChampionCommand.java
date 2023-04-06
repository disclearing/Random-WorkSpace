package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.types.GameServer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ChampionCommand implements CommandClass {

    @SuppressWarnings("deprecation")
    @Command(name = "champions")
    public void handlecommand(BungeeCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) return;

        ProxiedPlayer proxiedPlayer = commandSender.getPlayer();

        if (!ProxyServer.getInstance().getServers().containsKey("Champions")) {
            proxiedPlayer.sendMessage("§cO servidor não existe.");
            return;
        }

        if (proxiedPlayer.getServer().getInfo().getName().equalsIgnoreCase("Champions")) {
            commandSender.sendMessage("§cVocê já está no servidor da Champions.");
            return;
        }

        GameServer server = CommonsGeneral.getServersManager().getHardcoreGamesServer("Champions", 1);

        if (!server.isOnline()) {
            proxiedPlayer.sendMessage("§cO Quartel ainda não está liberado.");
        } else {
            proxiedPlayer.sendMessage(BungeeMessages.CONNECTING);
            proxiedPlayer.connect(ProxyServer.getInstance().getServerInfo("Champions"));
        }
    }
}