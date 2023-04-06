package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.bungee.manager.premium.PremiumMapManager;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.connections.mysql.MySQLManager;
import com.br.guilhermematthew.nowly.commons.common.data.DataHandler;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.utility.mojang.UUIDFetcher.UUIDFetcherException;

import java.sql.SQLException;

public class CreateAccountCommand implements CommandClass {

    @Command(name = "createaccount", aliases = {"createacc"}, groupsToUse = {Groups.ADMIN}, runAsync = true)
    public void createaccount(BungeeCommandSender commandSender, String label, String[] args) {
        if (args.length == 1) {

            final String nick = MySQLManager.getString("accounts", "nick", args[0], "nick");

            if (!nick.equalsIgnoreCase("N/A")) {
                commandSender.sendMessage("§cJá existe uma conta criada com este nick.");
                return;
            }

            try {
                DataHandler dataHandler = new DataHandler(nick, CommonsGeneral.getUUIDFetcher().getUUID(nick));
                dataHandler.load(DataCategory.ACCOUNT);

                PremiumMapManager.load(nick);
            } catch (UUIDFetcherException | SQLException ex) {
                commandSender.sendMessage("§cOcorreu um erro ao tentar criar a conta, tente novamente.");
                ex.printStackTrace();
                return;
            }

            commandSender.sendMessage("§aConta criada com sucesso.");
        } else {
            commandSender.sendMessage("§cUtilize: /createaccount <Nick>");
        }
    }
}