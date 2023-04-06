package com.br.guilhermematthew.nowly.commons.bukkit.commands.register;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TpallCommand implements CommandClass {

    @Command(name = "tpall", groupsToUse = {Groups.ADMIN})
    public void tpall(BukkitCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        Player player = commandSender.getPlayer();

        Location loc = player.getLocation();

        for (Player ons : Bukkit.getOnlinePlayers()) {
            if (ons != player) {
                ons.setFallDistance(-5);
                ons.teleport(loc);
            }
        }

        player.sendMessage(BukkitMessages.TPALL);

        BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(player) + " puxou todos os jogadores]", Groups.ADMIN);

        loc = null;
        player = null;
    }
}