package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.connections.mysql.MySQLManager;
import com.br.guilhermematthew.nowly.commons.common.data.DataHandler;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class ClearAccountCommand implements CommandClass {

    private final static String[] tablesToRemove = {"registros", "accounts", "hardcoregames", "kitpvp", "premium_map",
            "accounts_to_delete", "cosmetics", "preferences"};

    public static void clearAccount(final String nick) {
        BungeeMain.runLater(() -> {
            BungeeMain.runAsync(() -> {
                for (String table : tablesToRemove) {
                    MySQLManager.deleteFromTable(table, "nick", nick);
                }
            });
        }, 1, TimeUnit.SECONDS);

    }

    @SuppressWarnings("deprecation")
    @Command(name = "clearaccount", aliases = {"removeaccount"}, groupsToUse = {Groups.ADMIN})
    public void clearaccount(BungeeCommandSender commandSender, String label, String[] args) {
        if (args.length == 1) {

            String nick = MySQLManager.getString("accounts", "nick", args[0], "nick");

            if (nick.equalsIgnoreCase("N/A")) {
                commandSender.sendMessage("§cEste jogador não possuí uma conta no servidor.");
                return;
            }

            DataHandler dataHandler = null;

            if (CommonsGeneral.getProfileManager().containsProfile(nick)) {
                dataHandler = BungeeMain.getBungeePlayer(nick).getDataHandler();
            } else {
                dataHandler = new DataHandler(nick, null);

                try {
                    dataHandler.load(DataCategory.ACCOUNT);
                } catch (SQLException ex) {
                    dataHandler = null;
                    ex.printStackTrace();
                    commandSender.sendMessage("§cOcorreu um erro ao tentar carregar a conta do jogador.");
                }
            }

            if (dataHandler == null)
                return;

            CommonsGeneral.getProfileManager()
                    .removeGamingProfile(CommonsGeneral.getUUIDFetcher().getOfflineUUID(nick));
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(nick);

            if (target != null) {
                target.disconnect("§4§lAVISO\n\n§fTodos os seus dados foram apagados do servidor.");
            }

            BungeeMain.console(commandSender.getNick() + " deletou todos os dados do usuario: §7" + nick);

            commandSender.sendMessage("§aConta de §7'§a" + nick + "§7' apagada com sucesso!");

            clearAccount(nick);
        } else {
            commandSender.sendMessage("§cUtilize: /clearaccount <Nick>");
        }
    }
}