package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.types.GameServer;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.types.NetworkServer;
import com.br.guilhermematthew.nowly.commons.common.utility.system.DateUtils;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

public class ServersInfoCommand implements CommandClass {

    @SuppressWarnings("deprecation")
    @Command(name = "serverInfo", aliases = {"si", "svinfo", "server"}, groupsToUse = {Groups.ADMIN})
    public void serverInfo(BungeeCommandSender commandSender, String label, String[] args) {
        if (args.length != 1) {
            commandSender.sendMessage("§cUtilize: /serverinfo list");
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (!commandSender.isPlayer())
                return;

            ProxiedPlayer proxiedPlayer = commandSender.getPlayer();

            proxiedPlayer.sendMessage("");

            List<NetworkServer> networks = new ArrayList<>(CommonsGeneral.getServersManager().getServers());

            for (NetworkServer networkServer : networks) {
                if (networkServer.getServerId() != 1)
                    continue;

                TextComponent component = new TextComponent("§e["
                        + networkServer.getServerType().getName().toUpperCase() + networkServer.getServerId() + "]");

                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        (new ComponentBuilder("Online: " + (networkServer.isOnline() ? "§aSim" : "§cNão") + "\n"
                                + "Onlines: §7" + networkServer.getOnlines() + "/" + networkServer.getMaxPlayers()))
                                .create()));

                proxiedPlayer.sendMessage(component);
            }

            for (NetworkServer networkServer : networks) {
                if (!(networkServer instanceof GameServer))
                    continue;

                if (networkServer.getServerType() != ServerType.HARDCORE_GAMES)
                    continue;

                GameServer hardcoreGamesServer = (GameServer) networkServer;

                TextComponent component = new TextComponent("§e[HG" + hardcoreGamesServer.getServerId() + "]");

                component
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                (new ComponentBuilder("Online: " + (hardcoreGamesServer.isOnline() ? "§aSim" : "§cNão")
                                        + "\n" + "Onlines: §7" + hardcoreGamesServer.getOnlines() + "/"
                                        + hardcoreGamesServer.getMaxPlayers() + "\n" + "Jogadores: §7"
                                        + hardcoreGamesServer.getPlayersGaming() + "\n" + "Tempo: §7"
                                        + DateUtils.formatSeconds(hardcoreGamesServer.getTempo()) + "\n" + "Estágio: §7"
                                        + hardcoreGamesServer.getStage().getNome())).create()));

                proxiedPlayer.sendMessage(component);
            }

            proxiedPlayer.sendMessage("");
        }
    }
}