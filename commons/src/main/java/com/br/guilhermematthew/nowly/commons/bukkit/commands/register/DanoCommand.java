package com.br.guilhermematthew.nowly.commons.bukkit.commands.register;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitSettings;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import org.bukkit.Bukkit;

public class DanoCommand implements CommandClass {

    @Command(name = "dano", groupsToUse = {Groups.MOD})
    public void dano(BukkitCommandSender commandSender, String label, String[] args) {
        if (BukkitSettings.DANO_OPTION) {
            BukkitSettings.DANO_OPTION = false;
            Bukkit.broadcastMessage(BukkitMessages.DANO_DESATIVADO);
        } else {
            BukkitSettings.DANO_OPTION = true;
            Bukkit.broadcastMessage(BukkitMessages.DANO_ATIVADO);
        }

        if (!commandSender.getNick().equalsIgnoreCase("CONSOLE")) {
            BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(commandSender.getPlayer()) + " "
                    + (BukkitSettings.DANO_OPTION ? "ativou" : "desativou") + " o Dano!]", Groups.ADMIN);
        }
    }

    @Command(name = "pvp", groupsToUse = {Groups.MOD})
    public void pvp(BukkitCommandSender commandSender, String label, String[] args) {
        if (BukkitSettings.PVP_OPTION) {
            BukkitSettings.PVP_OPTION = false;
            Bukkit.broadcastMessage(BukkitMessages.PVP_DESATIVADO);
        } else {
            BukkitSettings.PVP_OPTION = true;
            Bukkit.broadcastMessage(BukkitMessages.PVP_ATIVADO);
        }

        if (!commandSender.getNick().equalsIgnoreCase("CONSOLE")) {
            BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(commandSender.getPlayer()) + " "
                    + (BukkitSettings.PVP_OPTION ? "ativou" : "desativou") + " o PvP!]", Groups.ADMIN);
        }
    }
}