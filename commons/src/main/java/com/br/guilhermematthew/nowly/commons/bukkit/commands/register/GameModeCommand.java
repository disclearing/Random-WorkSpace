package com.br.guilhermematthew.nowly.commons.bukkit.commands.register;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GameModeCommand implements CommandClass {

    @Command(name = "gamemode", aliases = {"gm"}, groupsToUse = {Groups.MOD})
    public void gm(BukkitCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        Player player = commandSender.getPlayer();

        if (args.length == 0) {
            changeGameMode(player);
            BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(player) + " alterou o seu GameMode para "
                    + player.getGameMode().name() + "]", Groups.ADMIN);
            return;
        }

        if (args.length == 1) {
            Player target = BukkitServerAPI.getExactPlayerByNick(args[0]);

            if (target == null) {
                if (args[0].equalsIgnoreCase("0")) {
                    changeGameMode(player, GameMode.SURVIVAL);
                } else if (args[0].equalsIgnoreCase("1")) {
                    changeGameMode(player, GameMode.CREATIVE);
                } else if (args[0].equalsIgnoreCase("2")) {
                    changeGameMode(player, GameMode.ADVENTURE);
                } else if (args[0].equalsIgnoreCase("3")) {
                    changeGameMode(player, GameMode.SPECTATOR);
                } else {
                    changeGameMode(player);
                }

                BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(player) + " alterou o seu GameMode para "
                        + player.getGameMode().name() + "]", Groups.ADMIN);
                return;
            }

            changeGameMode(target);

            player.sendMessage(BukkitMessages.GAMEMODE_CHANGE_FOR_SENDER.replace("%nick%", target.getName()).replace("%gamemode%",
                    target.getGameMode().name()));

            BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(player) + " alterou o GameMode para de " + BukkitServerAPI.getRealNick(target) + " para "
                    + target.getGameMode().name() + "]", Groups.ADMIN);
        }
    }

    private void changeGameMode(Player player) {
        changeGameMode(player, null);
    }

    private void changeGameMode(Player player, GameMode preference) {
        if (preference != null) {
            if (preference == player.getGameMode()) {
                player.sendMessage(BukkitMessages.VOCE_JA_ESTA_NESSE_GAMEMODE);
                return;
            }
        }

        if(preference == null) {
            if(player.getGameMode() == GameMode.CREATIVE) preference = GameMode.SURVIVAL;
            else preference = GameMode.CREATIVE;
        }

        player.setGameMode(preference);
        player.sendMessage(BukkitMessages.GAMEMODE_CHANGED.replace("%gamemode%", preference.name()));
    }
}