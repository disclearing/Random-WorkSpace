package com.br.guilhermematthew.nowly.commons.bukkit.commands.register;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitSettings;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatCommand implements CommandClass {

    @Command(name = "chat", groupsToUse = {Groups.MOD})
    public void chat(BukkitCommandSender commandSender, String label, String[] args) {
        if (BukkitSettings.CHAT_OPTION) {
            BukkitSettings.CHAT_OPTION = false;

            Bukkit.broadcastMessage(BukkitMessages.CHAT_DESATIVADO);

            if (!commandSender.getNick().equalsIgnoreCase("CONSOLE")) {
                BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(commandSender.getPlayer()) + " desativou o Chat!]", Groups.ADMIN);
            }
        } else {
            BukkitSettings.CHAT_OPTION = true;
            Bukkit.broadcastMessage(BukkitMessages.CHAT_ATIVADO);

            if (!commandSender.getNick().equalsIgnoreCase("CONSOLE")) {
                BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(commandSender.getPlayer()) + " ativou o Chat!]", Groups.ADMIN);
            }
        }
    }

    @Command(name = "clearchat", aliases = {"cc"}, groupsToUse = {Groups.MOD})
    public void clearChat(BukkitCommandSender commandSender, String label, String[] args) {
        for (Player on : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i <= 100; i++) {
                on.sendMessage("");
            }
        }
        Bukkit.broadcastMessage(BukkitMessages.CHAT_LIMPO);
    }
}