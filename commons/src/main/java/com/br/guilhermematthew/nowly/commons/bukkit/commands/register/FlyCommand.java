package com.br.guilhermematthew.nowly.commons.bukkit.commands.register;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandClass {

    @Command(name = "fly", groupsToUse = {Groups.MOD})
    public void fly(BukkitCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        Player player = commandSender.getPlayer();

        if (args.length == 0) {
            changeFly(player);
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                commandSender.sendMessage(BukkitMessages.JOGADOR_OFFLINE);
                return;
            }

            if (target.getAllowFlight()) {
                player.sendMessage(BukkitMessages.FLY_DESATIVADO_PARA.replace("%nick%", target.getName()));
            } else {
                player.sendMessage(BukkitMessages.FLY_ATIVADO_PARA.replace("%nick%", target.getName()));
            }

            changeFly(target);

            target = null;
        }
    }

    public void changeFly(Player player) {
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.sendMessage(BukkitMessages.FLY_DESATIVADO);
        } else {
            player.setAllowFlight(true);
            player.sendMessage(BukkitMessages.FLY_ATIVADO);
        }
    }
}