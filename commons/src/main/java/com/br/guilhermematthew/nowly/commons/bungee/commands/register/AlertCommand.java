package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;

public class AlertCommand implements CommandClass {

    @SuppressWarnings("deprecation")
    @Command(name = "alert", aliases = {"aviso"}, groupsToUse = {Groups.ADMIN})
    public void alert(BungeeCommandSender commandSender, String label, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(BungeeMessages.ALERT);
            return;
        }
        StringBuilder builder = new StringBuilder();

        builder.append(BungeeMessages.ALERT_PREFIX);

        for (String s : args) {
            builder.append(ChatColor.translateAlternateColorCodes('&', s));
            builder.append(" ");
        }

        String message = builder.substring(0, builder.length() - 1);
        ProxyServer.getInstance().broadcast(message);
    }
}