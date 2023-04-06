package com.br.guilhermematthew.nowly.commons.bukkit.commands.register;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportCommand implements CommandClass {

    @Command(name = "tp", aliases = {"teleport"}, groupsToUse = {Groups.PRIME})
    public void tp(BukkitCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        if (args.length == 0) {
            commandSender.sendMessage(BukkitMessages.TELEPORT_USAGE);
        } else if (args.length == 1) {
            Player target = Bukkit.getServer().getPlayer(args[0]);

            if (target == null) {
                commandSender.sendMessage(BukkitMessages.JOGADOR_OFFLINE);
                return;
            }

            Player player = commandSender.getPlayer();

            player.setFallDistance(-5);
            player.teleport(target.getLocation());

            commandSender.sendMessage(BukkitMessages.TELEPORTED_TO_PLAYER.replace("%nick%", target.getName()));
            BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(player) + " se teleportou para o "
                    + BukkitServerAPI.getRealNick(target) + "]", Groups.ADMIN);

            target = null;
            player = null;
        } else if (args.length == 2) {
            Player player1 = Bukkit.getServer().getPlayer(args[0]), player2 = Bukkit.getServer().getPlayer(args[1]);

            if (player1 == null || player2 == null) {
                commandSender.sendMessage(BukkitMessages.JOGADOR_OFFLINE);
                return;
            }

            player1.setFallDistance(-5);
            player1.teleport(player2.getLocation());

            commandSender.sendMessage(BukkitMessages.TELEPORTED_PLAYER_TO_PLAYER.replace("%nick%", player1.getName())
                    .replace("%nick1%", player2.getName()));

            BukkitServerAPI.warnStaff(
                    "§7[" + BukkitServerAPI.getRealNick(player1) + " foi teleportado para o "
                            + BukkitServerAPI.getRealNick(player2) + " pelo " + commandSender.getNick() + "]",
                    Groups.ADMIN);

            player1 = null;
            player2 = null;
        } else if (args.length == 3) {
            if (!StringUtility.isInteger(args[0]) || !StringUtility.isInteger(args[1])
                    || !StringUtility.isInteger(args[2])) {
                commandSender.sendMessage(BukkitMessages.TELEPORT_USAGE);
                return;
            }

            int x = Integer.parseInt(args[0]), y = Integer.parseInt(args[1]), z = Integer.parseInt(args[2]);

            Player player = commandSender.getPlayer();
            Location loc = new Location(player.getWorld(), x + 0.500, y, z + 0.500);

            player.setFallDistance(-5);
            player.teleport(loc);

            commandSender.sendMessage(
                    BukkitMessages.TELEPORTED_TO_LOCATION.replace("%coords%", "x " + x + ", y " + y + ", z " + z));

            BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(player)
                    + " se teleportou para as coordenadas: " + x + ", " + y + ", " + z + "]", Groups.ADMIN);

            loc = null;
            player = null;
        } else if (args.length == 4) {
            Player target = Bukkit.getServer().getPlayer(args[0]);

            if (target == null) {
                commandSender.sendMessage(BukkitMessages.JOGADOR_OFFLINE);
                return;
            }

            if (!StringUtility.isInteger(args[1]) || !StringUtility.isInteger(args[2])
                    || !StringUtility.isInteger(args[3])) {
                commandSender.sendMessage(BukkitMessages.TELEPORT_USAGE);
                return;
            }

            int x = Integer.parseInt(args[1]), y = Integer.parseInt(args[2]), z = Integer.parseInt(args[3]);

            Location loc = new Location(target.getWorld(), x + 0.500, y, z + 0.500);
            target.setFallDistance(-5);
            target.teleport(loc);

            commandSender.sendMessage(BukkitMessages.TELEPORTED_PLAYER_TO_LOCATION.replace("%nick%", target.getName())
                    .replace("%coords%", "x " + x + ", y " + y + ", z " + z));
            BukkitServerAPI
                    .warnStaff("§7[" + BukkitServerAPI.getRealNick(target) + " foi teleportado para as coordenadas: "
                            + x + ", " + y + ", " + z + " pelo " + commandSender.getNick() + "]", Groups.ADMIN);

            target = null;
            loc = null;
        } else {
            commandSender.sendMessage(BukkitMessages.TELEPORT_USAGE);
        }
    }
}