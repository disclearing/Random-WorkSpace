package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.TimeUnit;

public class ConnectCommand implements CommandClass {

    @SuppressWarnings("deprecation")
    @Command(name = "connect", aliases = {"server", "play", "conectar"})
    public void server(BungeeCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        ProxiedPlayer proxiedPlayer = commandSender.getPlayer();

        if (args.length == 0) {
            proxiedPlayer.sendMessage("§cUse: /connect <Servidor>");
            return;
        }

        if (!proxiedPlayer.getServer().getInfo().getName().equalsIgnoreCase(args[0])) {
            if (ProxyServer.getInstance().getServers().containsKey(args[0])) {
                if (args[0].toLowerCase().equalsIgnoreCase("Login")) {
                    proxiedPlayer.sendMessage("§cVocê não pode se conectar no servidor de Login.");
                    return;
                }
                if (args[0].toLowerCase().equalsIgnoreCase("ScreenShare")) {
                    proxiedPlayer.sendMessage("§cVocê não pode se conectar no servidor de ScreenShare.");
                    return;
                }
                if (args[0].equalsIgnoreCase("evento")) {
                    proxiedPlayer.sendMessage("§cUtilize: /evento");
                    return;
                }

                proxiedPlayer.sendMessage(BungeeMessages.CONNECTING);

                BungeeMain.runLater(() -> {
                    proxiedPlayer.connect(ProxyServer.getInstance().getServerInfo(args[0]));
                }, 333, TimeUnit.MILLISECONDS);
            } else {
                proxiedPlayer.sendMessage("§cO servidor não existe.");
            }
        } else {
            proxiedPlayer.sendMessage(BungeeMessages.VOCE_JA_ESTA_NESTE_SERVIDOR);
        }
    }
}