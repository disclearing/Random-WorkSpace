package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.bungee.account.BungeePlayer;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.connections.mysql.MySQLManager;
import com.br.guilhermematthew.nowly.commons.common.data.DataHandler;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.custompackets.PacketType;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketCustomAction;
import com.br.guilhermematthew.servercommunication.server.Server;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.SQLException;
import java.util.List;

public class AddPermCommand implements CommandClass {

    @Command(name = "addperm", groupsToUse = {Groups.ADMIN}, runAsync = true)
    public void addperm(BungeeCommandSender commandSender, String label, String[] args) {
        if (args.length != 2) {
            commandSender.sendMessage("§cUse: /addperm <Nick> <Permissao>");
            return;
        }

        String nick = MySQLManager.getString("accounts", "nick", args[0], "nick");
        if (nick.equalsIgnoreCase("N/A")) {
            commandSender.sendMessage(BungeeMessages.NAO_TEM_CONTA);
            return;
        }

        final String permission = args[1].toLowerCase();

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(nick);

        String replace = BungeeMessages.PERMISSION_ADDED_FOR_PLAYER.replace("%permissao%", permission).replace("%nick%", nick);
        if (target == null) {
            DataHandler dataHandler = new DataHandler(nick, null);

            try {
                dataHandler.load(DataCategory.ACCOUNT);
            } catch (SQLException ex) {
                commandSender.sendMessage("§cOcorreu um erro ao tentar carregar a conta do jogador.");
                BungeeMain.console("Ocorreu um erro ao tentar carregar a conta de um jogador (AddPermCommand) -> " + ex.getLocalizedMessage());
                return;
            }

            List<String> playerPermissionsList = dataHandler.getData(DataType.PERMISSIONS).getList();

            if (!playerPermissionsList.contains(permission)) {
                playerPermissionsList.add(permission);

                dataHandler.saveCategory(DataCategory.ACCOUNT);
            }
            commandSender.sendMessage(replace);
        } else {
            BungeePlayer bungeePlayer = BungeeMain.getBungeePlayer(target.getName());

            List<String> playerPermissions = bungeePlayer.getData(DataType.PERMISSIONS).getList();

            if (!playerPermissions.contains(permission)) {
                playerPermissions.add(permission);

                bungeePlayer.getData(DataType.PERMISSIONS).setValue(playerPermissions);

                CPacketCustomAction PACKET = new CPacketCustomAction(target.getName(), target.getUniqueId()).
                        type(PacketType.BUNGEE_SEND_UPDATED_STATS).field("add-perm").fieldValue(permission);

                PACKET.getJson().addProperty("dataCategory-1", bungeePlayer.getDataHandler().buildJSON(DataCategory.ACCOUNT, true).toString());

                Server.getInstance().sendPacket(target.getServer().getInfo().getName(), PACKET);

            }
            commandSender.sendMessage(replace);
        }
    }
}