package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.TimeUnit;

public class SendCommand implements CommandClass {

    @Command(name = "send", aliases = {"enviar"}, groupsToUse = {Groups.ADMIN}, runAsync = true)
    public void send(BungeeCommandSender commandSender, String label, String[] args) {
        if (args.length != 2) {
            commandSender.sendMessage(BungeeMessages.COMMAND_SEND_USAGE);
            return;
        }
        ServerInfo target = ProxyServer.getInstance().getServerInfo(args[1]);
        if (target == null) {
            commandSender.sendMessage(BungeeMessages.SERVER_NOT_EXIST);
            return;
        }
        if (target.getName().equalsIgnoreCase("Login")) {
            commandSender.sendMessage(BungeeMessages.VOCE_NAO_PODE_ENVIAR_PARA_ESTE_SERVIDOR);
            return;
        }
        if (ProxyServer.getInstance().getServerInfo(args[0]) != null) {
            if ((args[0].equalsIgnoreCase("Login")) || (args[0].equalsIgnoreCase("ScreenShare"))) {
                commandSender.sendMessage(BungeeMessages.VOCE_NAO_PODE_PUXAR_DESTE_SERVIDOR);
                return;
            }
            ServerInfo from = ProxyServer.getInstance().getServerInfo(args[0]);

            commandSender.sendMessage(BungeeMessages.TRYING_SEND_SERVER_TO_SERVER.replace("%servidor%", from.getName())
                    .replace("%destino%", target.getName()));

            int delay = 0;

            for (ProxiedPlayer inServer : from.getPlayers()) {
                BungeeMain.runLater(() -> {
                    enviar(inServer, target);
                }, delay, TimeUnit.MILLISECONDS);
                delay += 333;
            }
            return;
        }
        if (args[0].equalsIgnoreCase("local")) {
            if (commandSender.getNick().equalsIgnoreCase("CONSOLE")) {
                commandSender.sendMessage("§cVocê não está em um servidor.");
                return;
            }
            ProxiedPlayer p = (ProxiedPlayer) commandSender;

            commandSender
                    .sendMessage(BungeeMessages.TRING_SEND_LOCAL_FOR_SERVER.replace("%destino%", target.getName()));

            int delay = 0;

            for (ProxiedPlayer inLocal : p.getServer().getInfo().getPlayers()) {
                BungeeMain.runLater(() -> {
                    enviar(inLocal, target);
                }, delay, TimeUnit.MILLISECONDS);
                delay += 333;
            }
        } else if (args[0].equalsIgnoreCase("todos") || (args[0].equalsIgnoreCase("*"))) {
            commandSender.sendMessage(BungeeMessages.TRYING_SEND_ALL_FOR_SERVER.replace("%destino%", target.getName()));

            int delay = 0;

            for (ProxiedPlayer p1 : ProxyServer.getInstance().getPlayers()) {
                if (p1.getServer().getInfo().getName().equalsIgnoreCase("Login")) {
                    continue;
                }
                if (p1.getServer().getInfo().getName().equalsIgnoreCase("ScreenShare")) {
                    continue;
                }

                BungeeMain.runLater(() -> {
                    enviar(p1, target);
                }, delay, TimeUnit.MILLISECONDS);
                delay += 333;
            }
        } else {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
            if (player == null) {
                commandSender.sendMessage(BungeeMessages.JOGADOR_OFFLINE);
                return;
            }

            commandSender.sendMessage(BungeeMessages.TRYING_SEND_PLAYER_FOR_SERVER.replace("%nick%", player.getName())
                    .replace("%destino%", target.getName()));

            BungeeMain.runLater(() -> {
                enviar(player, target);
            }, 333, TimeUnit.MILLISECONDS);
        }
    }

    @SuppressWarnings("deprecation")
    private void enviar(ProxiedPlayer player, ServerInfo target) {
        if ((player.getServer() != null) && (!player.getServer().getInfo().getName().equals("login"))
                && (!player.getServer().getInfo().getName().equalsIgnoreCase("screenshare"))
                && (!player.getServer().getInfo().equals(target))) {
            player.connect(target);
            player.sendMessage(BungeeMessages.SENDED_FOR_OTHER_SERVER);
        }
    }

    /**
     *
     *
     * BungeeClient.sendPacketToServer(player, new
     * PacketRequirePlayerData(player.getName(), player.getUniqueId().toString(),
     * Core.getUuidFetcher().getOfflineUUID(player.getName()).toString(), false));
     *
     *
     */
}