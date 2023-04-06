package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import lombok.val;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class StaffListCommand implements CommandClass {

    @Command(name = "stafflist", aliases = {"staff", "sl", "slist"}, groupsToUse = {Groups.PRIME})
    public void stafflist(BungeeCommandSender commandSender, String label, String[] args) {
        commandSender.sendMessage("");
        commandSender.sendMessage(BungeeMessages.STAFFLIST_PREFIX);
        commandSender.sendMessage("");

        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            if (!BungeeMain.isValid(proxiedPlayer)) continue;

            if (proxiedPlayer.hasPermission(CommonsConst.PERMISSION_PREFIX + ".staff")) {
                val bungeePlayer = BungeeMain.getBungeePlayer(proxiedPlayer.getName());
                val tag = bungeePlayer.getGroup().getTag();

                val components = TextComponent.fromLegacyText(BungeeMessages.STAFFLIST_PLAYER.replace("%nick%", tag.getColor() + "§l" + tag.getPrefix() + "§f " + tag.getColor() +
                        proxiedPlayer.getName()).replace("%servidor%", proxiedPlayer.getServer().getInfo().getName()));

                for (BaseComponent message : components) {
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/connect " + proxiedPlayer.getServer().getInfo().getName().toLowerCase()));
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent("§aClique para conectar")}));
                }

                commandSender.sendMessage(components);
            }
        }

        commandSender.sendMessage("");
    }
}