package com.br.guilhermematthew.nowly.commons.bukkit.commands.register;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import java.util.List;

public class ClearDropsCommand implements CommandClass {

    @Command(name = "cleardrops", aliases = {"cd"}, groupsToUse = {Groups.MOD})
    public void clearDrops(BukkitCommandSender commandSender, String label, String[] args) {
        int removidos = 0;

        for (World world : Bukkit.getWorlds()) {
            List<Entity> items = world.getEntities();

            for (Entity item : items) {
                if (item instanceof Item) {
                    item.remove();
                    removidos++;
                }
            }
        }

        commandSender.sendMessage(BukkitMessages.CLEAR_DROPS.replace("%quantia%", "" + removidos));

        if (!commandSender.getNick().equalsIgnoreCase("CONSOLE")) {
            BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(commandSender.getPlayer()) + " limpou o chão!]", Groups.ADMIN);
        }
    }
}