package com.br.guilhermematthew.nowly.commons.bukkit.commands.register;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.MenuListener;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.bukkit.menu.AccountInventory;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.connections.mysql.MySQLManager;
import org.bukkit.entity.Player;

public class AccountCommand implements CommandClass {

    @Command(name = "account", aliases = {"acc", "perfil", "conta", "stats", "info"}, runAsync = true)
    public void account(BukkitCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        if (!MenuListener.isOpenMenus()) {
            commandSender.sendMessage("§cVocê não pode abrir menus agora.");
            return;
        }

        Player player = commandSender.getPlayer();

        final String nickViewer = BukkitServerAPI.getRealNick(player);

        if (args.length == 1) {
            if (!commandSender.hasPermission("account")) {
                return;
            }

            String nick = MySQLManager.getString("accounts", "nick", args[0], "nick");
            if (nick.equalsIgnoreCase("N/A")) {
                commandSender.sendMessage(BukkitMessages.NAO_TEM_CONTA);
                return;
            }
            new AccountInventory(nickViewer, nick).open(player);
        } else {
            new AccountInventory(nickViewer, nickViewer).open(player);
        }
    }

}