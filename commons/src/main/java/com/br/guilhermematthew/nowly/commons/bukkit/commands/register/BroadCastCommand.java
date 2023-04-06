package com.br.guilhermematthew.nowly.commons.bukkit.commands.register;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import org.bukkit.Bukkit;

public class BroadCastCommand implements CommandClass {

    @Command(name = "broadcast", aliases = {"bc"}, groupsToUse = {Groups.MOD})
    public void broadcast(BukkitCommandSender commandSender, String label, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(BukkitMessages.BROADCAST_USAGE);
            return;
        }

        Bukkit.broadcastMessage(BukkitMessages.BROADCAST_PREFIX + StringUtility.createArgs(0, args).replaceAll("&", "§"));
        if (!commandSender.getNick().equalsIgnoreCase("CONSOLE")) {
            BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(commandSender.getPlayer()) + " utilizou o BroadCast!]", Groups.ADMIN);
        }
    }
}