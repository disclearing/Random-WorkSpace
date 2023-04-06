package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.bungee.account.BungeePlayer;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class StaffChatCommand implements CommandClass {

    @SuppressWarnings("deprecation")
    @Command(name = "staffchat", aliases = {"sc"}, groupsToUse = {Groups.PRIME})
    public void staffchat(BungeeCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        ProxiedPlayer proxiedPlayer = commandSender.getPlayer();

        BungeePlayer proxyPlayer = BungeeMain.getBungeePlayer(proxiedPlayer.getName());

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
                if (proxyPlayer.getBoolean(DataType.RECEIVE_STAFFCHAT_MESSAGES)) {
                    proxiedPlayer.sendMessage(BungeeMessages.STAFFCHAT_JA_ESTA_ATIVADO);
                    return;
                }
                proxyPlayer.set(DataType.RECEIVE_STAFFCHAT_MESSAGES, true);

                BungeeMain.runAsync(() -> {
                    proxyPlayer.getDataHandler().saveCategory(DataCategory.PREFERENCES);
                });

                proxiedPlayer.sendMessage(BungeeMessages.VOCE_ATIVOU_O_STAFFCHAT);
            } else if (args[0].equalsIgnoreCase("off")) {
                if (!proxyPlayer.getBoolean(DataType.RECEIVE_STAFFCHAT_MESSAGES)) {
                    proxiedPlayer.sendMessage(BungeeMessages.STAFFCHAT_JA_ESTA_DESATIVADO);
                    return;
                }

                proxyPlayer.set(DataType.RECEIVE_STAFFCHAT_MESSAGES, false);

                BungeeMain.runAsync(() -> {
                    proxyPlayer.getDataHandler().saveCategory(DataCategory.PREFERENCES);
                });

                proxiedPlayer.sendMessage(BungeeMessages.VOCE_DESATIVOU_O_STAFFCHAT);
            } else {
                proxiedPlayer.sendMessage("");
                proxiedPlayer.sendMessage("§cUse: /staffchat");
                proxiedPlayer.sendMessage("§cUse: /staffchat <On/Off>");
                proxiedPlayer.sendMessage("");
            }
            return;
        }

        if (proxyPlayer.isInStaffChat()) {
            proxyPlayer.setInStaffChat(false);
            proxiedPlayer.sendMessage(BungeeMessages.VOCE_SAIU_DO_STAFFCHAT);
        } else {
            proxyPlayer.setInStaffChat(true);
            proxiedPlayer.sendMessage(BungeeMessages.VOCE_ENTROU_NO_STAFFCHAT);
        }
    }
}