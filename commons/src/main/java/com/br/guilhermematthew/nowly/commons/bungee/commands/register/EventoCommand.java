package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class EventoCommand implements CommandClass {

    public static boolean hasEvento = false;

    @SuppressWarnings("deprecation")
    @Command(name = "evento")
    public void evento(BungeeCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        ProxiedPlayer proxiedPlayer = commandSender.getPlayer();

        if (!hasEvento && !BungeeMain.getBungeePlayer(proxiedPlayer.getName()).isStaffer()) {
            commandSender.sendMessage(BungeeMessages.EVENTO_OFFLINE);
            return;
        }

        if (proxiedPlayer.getServer().getInfo().getName().equalsIgnoreCase("evento")) {
            commandSender.sendMessage("§cVocê já está no evento.");
            return;
        }

        if (ProxyServer.getInstance().getServers().containsKey("Evento")) {
            proxiedPlayer.sendMessage(BungeeMessages.CONNECTING);
            proxiedPlayer.connect(ProxyServer.getInstance().getServerInfo("Evento"));
        } else {
            proxiedPlayer.sendMessage("§cO servidor não existe.");
        }
    }

    @Command(name = "eventomanager", groupsToUse = {Groups.MOD_PLUS})
    public void eventomanager(BungeeCommandSender commandSender, String label, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(BungeeMessages.EVENTO_MANAGER_USAGE);
            return;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
                if (hasEvento) {
                    commandSender.sendMessage(BungeeMessages.EVENTO_MANAGER_ONLINE);
                    return;
                }
                hasEvento = true;

                commandSender.sendMessage(BungeeMessages.VOCE_LIBEROU_A_ENTRADA_NO_EVENTO);

                TextComponent message = new TextComponent(BungeeMessages.EVENTO_LIBERADO);
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/evento"));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent("§aClique para se conectar")}));

                BungeeCord.getInstance().broadcast(TextComponent.fromLegacyText(""));
                BungeeCord.getInstance().broadcast(message);
                BungeeCord.getInstance().broadcast(TextComponent.fromLegacyText(""));
            } else if (args[0].equalsIgnoreCase("off")) {
                if (!hasEvento) {
                    commandSender.sendMessage(BungeeMessages.EVENTO_MANAGER_OFFLINE);
                    return;
                }
                hasEvento = false;
                commandSender.sendMessage(BungeeMessages.VOCE_LIBEROU_A_ENTRADA_APENAS_PARA_STAFFERS);
            } else {
                commandSender.sendMessage(BungeeMessages.EVENTO_MANAGER_USAGE);
            }
        }
    }
}