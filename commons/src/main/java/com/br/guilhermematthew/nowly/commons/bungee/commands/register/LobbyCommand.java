package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class LobbyCommand implements CommandClass {

    @SuppressWarnings("deprecation")
    @Command(name = "lobby", aliases = {"L", "hub"})
    public void lobby(BungeeCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        ProxiedPlayer proxiedPlayer = commandSender.getPlayer();

        String serverToConnect = "Lobby";

        ServerType serverConnected = ServerType.getServer(proxiedPlayer.getServer().getInfo().getName().toLowerCase());

        BungeeMain.console(proxiedPlayer.getName() + " executou o /lobby. ele esta no servidor: " + serverConnected.getName());

        if (serverConnected != ServerType.UNKNOWN) {
            if (serverConnected.isHardcoreGames()) {
                BungeeMain.console(proxiedPlayer.getName() + " executou o /lobby. ele esta no HG!!!!");
                serverToConnect = "LobbyHardcoreGames";
            }

            if (serverConnected.isPvP()) {
                serverToConnect = "LobbyPvP";
            }
        }

        if (!CommonsGeneral.getServersManager().getNetworkServer(serverToConnect).isOnline()) {
            BungeeMain.console(proxiedPlayer.getName() + " executou o /lobby. e o servidor em que ele ia se conectar esta offline. (" + serverToConnect + ")");
            serverToConnect = "Lobby";
        }

        if (serverConnected.getName().equalsIgnoreCase(serverToConnect)) {
            proxiedPlayer.sendMessage(BungeeMessages.VOCE_JA_ESTA_NESTE_SERVIDOR);
            return;
        }

        proxiedPlayer.sendMessage(BungeeMessages.CONNECTING);

        proxiedPlayer.connect(ProxyServer.getInstance().getServerInfo(serverToConnect));
    }
}