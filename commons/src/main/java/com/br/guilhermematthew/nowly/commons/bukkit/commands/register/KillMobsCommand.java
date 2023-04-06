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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class KillMobsCommand implements CommandClass {

    @Command(name = "killmobs", aliases = {"km"}, groupsToUse = {Groups.MOD})
    public void killMobs(BukkitCommandSender commandSender, String label, String[] args) {
        int removidos = 0;

        for (World world : Bukkit.getWorlds()) {
            List<Entity> entitys = world.getEntities();

            for (Entity entity : entitys) {
                if ((entity instanceof LivingEntity) && (!(entity instanceof Player))) {
                    entity.remove();
                    removidos++;
                }
            }

            entitys.clear();
            entitys = null;
        }

        commandSender.sendMessage(BukkitMessages.KILL_MOBS.replace("%quantia%", "" + removidos));

        if (!commandSender.getNick().equalsIgnoreCase("CONSOLE")) {
            BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(commandSender.getPlayer()) + " limpou os mobs!]",
                    Groups.ADMIN);
        }
    }
}