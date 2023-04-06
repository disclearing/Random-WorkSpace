package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.bungee.account.BungeePlayer;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class KickCommand implements CommandClass {

    @SuppressWarnings("deprecation")
    @Command(name = "kick", aliases = {"expulsar"}, groupsToUse = {Groups.PRIME})
    public void kick(BungeeCommandSender commandSender, String label, String[] args) {
        String expulsou = commandSender.getNick();

        if (args.length == 0) {
            commandSender.sendMessage(BungeeMessages.KICK_USAGE);
            return;
        }

        String nick = args[0];
        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(nick);

        if (target == null) {
            commandSender.sendMessage(BungeeMessages.JOGADOR_OFFLINE);
            return;
        }

        boolean kick = true;
        if (!expulsou.equalsIgnoreCase("CONSOLE")) {
            if (BungeeMain.isValid(target)) {
                BungeePlayer proxyPlayer = BungeeMain.getBungeePlayer(target.getName()),
                        proxyExpulsou = BungeeMain.getBungeePlayer(commandSender.getPlayer().getName());

                if (proxyPlayer.getGroup().getLevel() > proxyExpulsou.getGroup().getLevel()) {
                    commandSender.sendMessage(BungeeMessages.DONT_KICK_PLAYER_ABOVE_YOU);
                    kick = false;
                }
            }
        }

        if (!kick) {
            return;
        }
        String motivo = "Não especificado";
        if (args.length >= 2) {
            motivo = StringUtility.createArgs(1, args);
        }

        commandSender.sendMessage(BungeeMessages.PLAYER_KICKED);
        target.disconnect(BungeeMessages.VOCE_FOI_EXPULSO.replace("%expulsou%", expulsou).replace("%motivo%", motivo));
    }
}