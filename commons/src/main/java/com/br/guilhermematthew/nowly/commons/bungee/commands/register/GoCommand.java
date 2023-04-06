package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.custompackets.PacketType;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketCustomAction;
import com.br.guilhermematthew.servercommunication.server.Server;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.TimeUnit;

public class GoCommand implements CommandClass {

    @SuppressWarnings("deprecation")
    @Command(name = "go", aliases = {"ir"}, groupsToUse = {Groups.PRIME})
    public void go(BungeeCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        ProxiedPlayer proxiedPlayer = commandSender.getPlayer();
        if (args.length != 1) {
            proxiedPlayer.sendMessage(BungeeMessages.COMMAND_GO_USAGE);
            return;
        }

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
        if ((player == null) || (player.getServer() == null)) {
            proxiedPlayer.sendMessage(BungeeMessages.JOGADOR_OFFLINE);
        } else {
            proxiedPlayer.sendMessage(BungeeMessages.CONNECTING);

            if(proxiedPlayer.getServer().getInfo().getName().equals(player.getServer().getInfo().getName())) {
                Server.getInstance().sendPacket(player.getServer().getInfo().getName(), new CPacketCustomAction(player.getName(), player.getUniqueId())
                        .type(PacketType.BUKKIT_GO)
                        .field(proxiedPlayer.getName()));
            } else {
                proxiedPlayer.connect(ProxyServer.getInstance().getServerInfo(player.getServer().getInfo().getName()), (ignored, throwable) -> {
                    BungeeMain.runLater(() -> Server.getInstance().sendPacket(player.getServer().getInfo().getName(), new CPacketCustomAction(player.getName(), player.getUniqueId())
                            .type(PacketType.BUKKIT_GO)
                            .field(proxiedPlayer.getName())), 333, TimeUnit.MILLISECONDS);
                });
            }
        }
    }
}