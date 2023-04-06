package com.br.guilhermematthew.nowly.commons.bukkit.commands.register;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerRequestEvent;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.connections.mysql.MySQLManager;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.custompackets.PacketType;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketCustomAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SkinCommand implements CommandClass {

    @Command(name = "skin", groupsToUse = {Groups.PRIME}, runAsync = true)
    public void skin(BukkitCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        Player player = commandSender.getPlayer();

        if (args.length == 0) {
            player.sendMessage(BukkitMessages.SKIN_USAGE);
            return;
        }

        if (requestChangeSkin(player)) {
            BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());

            if (!bukkitPlayer.canChangeSkin()) {
                player.sendMessage(BukkitMessages.SKIN_AGUARDE_PARA_TROCAR);
                return;
            }

            bukkitPlayer.setLastChangeSkin(System.currentTimeMillis());

            if (args[0].equalsIgnoreCase("atualizar")) {
                player.sendMessage(BukkitMessages.SKIN_DOWNLOADING);

                bukkitPlayer.sendPacket(new CPacketCustomAction(bukkitPlayer.getNick())
                        .type(PacketType.BUNGEE_UPDATE_SKIN).field(args[0]));

            } else if (args[0].equalsIgnoreCase("random")) {
                player.sendMessage(BukkitMessages.SKIN_DOWNLOADING);

                bukkitPlayer.sendPacket(new CPacketCustomAction(bukkitPlayer.getNick())
                        .type(PacketType.BUNGEE_SET_RANDOM_SKIN));
            } else {
                if (!MySQLManager.contains("skins", "nick", args[0])) {
                    player.sendMessage(BukkitMessages.SKIN_DOWNLOADING);
                }

                bukkitPlayer.sendPacket(new CPacketCustomAction(bukkitPlayer.getNick())
                        .type(PacketType.BUNGEE_SET_SKIN).field(args[0]));
            }
        } else {
            player.sendMessage(BukkitMessages.VOCE_NAO_PODE_TROCAR_SUA_SKIN_AGORA);
        }
    }

    private boolean requestChangeSkin(Player player) {
        PlayerRequestEvent event = new PlayerRequestEvent(player, "skin");
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            player.sendMessage("§cVocê não pode trocar sua skin agora!");
            return false;
        }

        return true;
    }
}