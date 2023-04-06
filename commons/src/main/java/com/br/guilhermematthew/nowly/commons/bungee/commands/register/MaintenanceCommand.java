package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.connections.mysql.MySQLManager;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;

public class MaintenanceCommand implements CommandClass {

    @Command(name = "maintenance", aliases = {"wg", "manu", "manutenção"}, groupsToUse = {Groups.ADMIN}, runAsync = true)
    public void globalWhitelist(BungeeCommandSender commandSender, String label, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage("");
            commandSender.sendMessage("§cUtilize: /" + label + " <On/Off>");
            commandSender.sendMessage("§cUtilize: /" + label + " <Lista>");
            commandSender.sendMessage("§cUtilize: /" + label + " <add/remove> <Nick>");
            commandSender.sendMessage("");
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
                changeGlobalWhite(commandSender, true);
            } else if (args[0].equalsIgnoreCase("off")) {
                changeGlobalWhite(commandSender, false);
            } else if (args[0].equalsIgnoreCase("lista")) {
                commandSender.sendMessage("");

                String formated = StringUtility.formatArrayToString(BungeeMain.getManager().getWhitelistPlayers(), false);

                commandSender.sendMessage("§aJogadores na WhiteList: " + formated);
            } else {
                commandSender.sendMessage("");
                commandSender.sendMessage("§cUtilize: /" + label + " <Lista>");
                commandSender.sendMessage("§cUtilize: /" + label + " <add/remove> <Nick>");
                commandSender.sendMessage("");
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                String nick = args[1];

                if (BungeeMain.getManager().getWhitelistPlayers().contains(nick.toLowerCase())) {
                    commandSender.sendMessage("§cEste jogador ja está na WhiteList!");
                    return;
                }

                BungeeMain.getManager().getWhitelistPlayers().add(nick.toLowerCase());
                commandSender.sendMessage("§7" + nick + " §afoi adicionado na WhiteList!");

                MySQLManager.updateStats("global_whitelist", "nicks", "identify", "default",
                        StringUtility.formatArrayToStringWithoutSpace(BungeeMain.getManager().getWhitelistPlayers(), true));
            } else if (args[0].equalsIgnoreCase("remove")) {
                String nick = args[1];

                if (!BungeeMain.getManager().getWhitelistPlayers().contains(nick.toLowerCase())) {
                    commandSender.sendMessage("§cEste jogador não está na WhiteList!");
                    return;
                }
                BungeeMain.getManager().getWhitelistPlayers().remove(nick.toLowerCase());

                commandSender.sendMessage("§7" + nick + " §afoi removido da WhiteList!");

                MySQLManager.updateStats("global_whitelist", "nicks", "identify", "default",
                        StringUtility.formatArrayToStringWithoutSpace(BungeeMain.getManager().getWhitelistPlayers(), true));
            } else {
                commandSender.sendMessage("");
                commandSender.sendMessage("§cUtilize: /" + label + " <On/Off>");
                commandSender.sendMessage("§cUtilize: /" + label + " <Lista>");
                commandSender.sendMessage("§cUtilize: /" + label + " <add/remove> <Nick>");
                commandSender.sendMessage("");
            }
        }
    }

    private void changeGlobalWhite(BungeeCommandSender commandSender, boolean ativar) {
        boolean ativada = BungeeMain.getManager().isGlobalWhitelist();

        if (ativada) {
            if (ativar) {
                commandSender.sendMessage("§cA WhiteList global ja está ativada!");
                return;
            }

            MySQLManager.updateStats("global_whitelist", "actived", "identify", "default", "false");

            commandSender.sendMessage("§aWhiteList global desativada!");

            BungeeMain.getManager().setGlobalWhitelist(false);

            BungeeMain.getManager().warnStaff("§aA WhiteList global foi desativada!", Groups.ADMIN);
        } else {
            if (!ativar) {
                commandSender.sendMessage("§cA WhiteList global ja está desativada!");
                return;
            }

            MySQLManager.updateStats("global_whitelist", "actived", "identify", "default", "true");

            BungeeMain.getManager().setGlobalWhitelist(true);

            commandSender.sendMessage("§aWhiteList global ativada!");
            BungeeMain.getManager().warnStaff("§aA WhiteList global foi ativada!", Groups.ADMIN);
        }
    }
}