package com.br.guilhermematthew.nowly.commons.bukkit.commands.register;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitSettings;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.server.ServerStopEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.queue.PlayerBukkitQueue;
import com.br.guilhermematthew.nowly.commons.bukkit.queue.QueueType;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StopCommand implements CommandClass {

    @Command(name = "stop", aliases = {"rl", "reload", "parar", "reiniciar", "stop"}, groupsToUse = {Groups.ADMIN})
    public void stop(BukkitCommandSender commandSender, String label, String[] args) {
        commandSender.sendMessage("");
        commandSender.sendMessage(BukkitMessages.STOP_PREFIX);
        commandSender.sendMessage("");

        Bukkit.getServer().getPluginManager().callEvent(new ServerStopEvent());

        if (Bukkit.getOnlinePlayers().size() == 0) {
            Bukkit.shutdown();
            return;
        }

        BukkitSettings.DANO_OPTION = false;
        BukkitSettings.PVP_OPTION = false;
        BukkitSettings.CHAT_OPTION = false;
        BukkitSettings.LOGIN_OPTION = false;

        commandSender.sendMessage(BukkitMessages.STOP_SENDING_PLAYERS_TO_LOBBY);

        ServerType serverToConnect = ServerType.LOBBY;

        if (BukkitMain.getServerType().isPvP(false)) {
            serverToConnect = ServerType.LOBBY_PVP;
        } else if (BukkitMain.getServerType().isHardcoreGames(false)) {
            serverToConnect = ServerType.LOBBY_HARDCOREGAMES;
        }

        if (BukkitMain.getServerType() != ServerType.LOBBY && BukkitMain.getServerType() != ServerType.LOGIN) {
            PlayerBukkitQueue queue = new PlayerBukkitQueue(15, true, QueueType.CONNECT);
            queue.setStopOnFinish(true);
            queue.setDestroyOnFinish(true);

            for (Player onlines : Bukkit.getOnlinePlayers()) {
                queue.addToQueue(onlines, serverToConnect.getName());

                onlines.sendMessage(BukkitMessages.PLAYER_SERVER_RESTARTING);
            }

            queue.start();
        } else {
            PlayerBukkitQueue queue = new PlayerBukkitQueue(10, true, QueueType.KICK);
            queue.setStopOnFinish(true);
            queue.setDestroyOnFinish(true);

            for (Player onlines : Bukkit.getOnlinePlayers()) {
                queue.addToQueue(onlines);
                onlines.sendMessage(BukkitMessages.PLAYER_SERVER_RESTARTING);
            }

            queue.start();
        }
        commandSender.sendMessage("");

        if (!commandSender.getNick().equalsIgnoreCase("CONSOLE")) {
            BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(commandSender.getPlayer()) + " executou o comando para parar o servidor!]", Groups.ADMIN);
        }
    }
}