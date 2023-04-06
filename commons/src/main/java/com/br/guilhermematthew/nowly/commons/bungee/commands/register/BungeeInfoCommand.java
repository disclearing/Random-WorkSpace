package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;

public class BungeeInfoCommand implements CommandClass {

    @Command(name = "bungeeinfo", groupsToUse = {Groups.ADMIN})
    public void bungeeinfo(BungeeCommandSender commandSender, String label, String[] args) {
        long total = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long used = total - free;
        double usedPercentage = (used * 100) / total;

        commandSender.sendMessage("");
        commandSender.sendMessage("§e§lBUNGEECORD - INFOS");
        commandSender.sendMessage("");
        commandSender.sendMessage("§7UUIDS no Cache -> §e" + CommonsGeneral.getUUIDFetcher().getCacheSize());
        commandSender.sendMessage("");
        commandSender.sendMessage("§e§lRAM");
        commandSender.sendMessage(" Memória total: §e" + total / 1048576L + " MB");
        commandSender.sendMessage(" Memória usada: §c" + (used / 1048576L) + " MB");
        commandSender.sendMessage(" Memória livre: §a" + (free / 1048576L) + " MB");
        commandSender.sendMessage(" Memória em uso: " + StringUtility.ramQuality(usedPercentage) + "%");
        commandSender.sendMessage("");
    }
}