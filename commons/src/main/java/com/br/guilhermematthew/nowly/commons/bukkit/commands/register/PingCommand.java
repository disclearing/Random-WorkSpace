package com.br.guilhermematthew.nowly.commons.bukkit.commands.register;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.api.player.PlayerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PingCommand implements CommandClass {

    @Command(name = "ping", aliases = {"p", "ms", "latencia"})
    public void ping(BukkitCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        Player player = commandSender.getPlayer();

        if (args.length == 0) {
            player.sendMessage(BukkitMessages.SEU_PING.replace("%quantia%", "" + PlayerAPI.getPing(player)));
        } else if (args.length == 1) {
            if (!commandSender.hasPermission("ping"))
                return;

            final Player p1 = Bukkit.getPlayer(args[0]);
            if (p1 == null) {
                player.sendMessage(BukkitMessages.JOGADOR_OFFLINE);
                return;
            }
            player.sendMessage(BukkitMessages.PING_OUTRO.replace("%nick%", p1.getName()).replace("%quantia%", "" + PlayerAPI.getPing(p1)));
        }
    }
}