package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.bungee.account.permission.BungeePlayerPermissions;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.connections.mysql.MySQLManager;
import com.br.guilhermematthew.nowly.commons.common.data.DataHandler;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.utility.mojang.UUIDFetcher.UUIDFetcherException;
import com.br.guilhermematthew.nowly.commons.common.utility.system.DateUtils;
import com.br.guilhermematthew.nowly.commons.custompackets.PacketType;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketCustomAction;
import com.br.guilhermematthew.servercommunication.server.Server;
import lombok.val;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.SQLException;

public class GroupCommand implements CommandClass {

    @SuppressWarnings("deprecation")
    @Command(name = "group", aliases = {"groups", "setgroup", "groupset"}, groupsToUse = {Groups.ADMIN}, runAsync = true)
    public void group(BungeeCommandSender commandSender, String label, String[] args) {
        if (args.length < 2) {
            commandSender.sendMessage(BungeeMessages.COMMAND_GROUP_USAGE);
        } else {
            String playerNick = MySQLManager.getString("accounts", "nick", args[0], "nick");

            if (playerNick.equalsIgnoreCase("N/A")) {
                commandSender.sendMessage(BungeeMessages.NAO_TEM_CONTA);
            } else {
                if (!Groups.existGrupo(args[1])) {
                    commandSender.sendMessage(BungeeMessages.GRUPO_INEXISTENTE);
                    return;
                }

                Groups group = Groups.getGroup(args[1]);
                if(commandSender.isPlayer()) {
                    val bp = BungeeMain.getBungeePlayer(commandSender.getPlayer().getName());
                    if(group.getLevel() > bp.getGroup().getLevel()) {
                        commandSender.sendMessage(BungeeMessages.GRUPO_ALTO);
                        return;
                    }
                }

                long tempo = 0L;
                if (args.length > 2) {
                    try {
                        tempo = DateUtils.parseDateDiff(args[2], true);
                    } catch (Exception ex) {
                        commandSender.sendMessage(BungeeMessages.TEMPO_INVALIDO);
                        return;
                    }
                }

                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(playerNick);
                DataHandler dataHandler;

                boolean sucess;

                if (target != null) {
                    dataHandler = CommonsGeneral.getProfileManager().getGamingProfile(target.getName()).getDataHandler();

                    dataHandler.set(DataType.GROUP, group.getName());
                    dataHandler.set(DataType.GROUP_TIME, tempo);
                    dataHandler.set(DataType.GROUP_CHANGED_BY, commandSender.getNick());

                    CPacketCustomAction PACKET = new CPacketCustomAction(target.getName(), target.getUniqueId()).
                            type(PacketType.BUNGEE_SEND_UPDATED_STATS).field("group");

                    PACKET.getJson().addProperty("dataCategory-1", dataHandler.buildJSON(DataCategory.ACCOUNT, true).toString());
                    Server.getInstance().sendPacket(target.getServer().getInfo().getName(), PACKET);

                    target.sendMessage(BungeeMessages.VOCE_RECEBEU_UM_GRUPO.
                            replace("%cargo%", group.getColor() + group.getName().toUpperCase()).replace("%duração%",
                                    (tempo == 0L ? "§aETERNA" : "de §a" + DateUtils.formatDifference(tempo))));

                    BungeePlayerPermissions.clearPermissions(target);
                    BungeePlayerPermissions.injectPermissions(target, group.getName());

                    dataHandler.saveCategory(DataCategory.ACCOUNT);
                } else {
                    dataHandler = getDataHandlerByPlayer(playerNick);
                    if (dataHandler == null) {
                        commandSender.sendMessage("§cOcorreu um erro!");
                        return;
                    }

                    dataHandler.set(DataType.GROUP, group.getName());
                    dataHandler.set(DataType.GROUP_TIME, tempo);
                    dataHandler.set(DataType.GROUP_CHANGED_BY, commandSender.getNick());

                    dataHandler.saveCategory(DataCategory.ACCOUNT);
                    sucess = true;
                }

                String message = BungeeMessages.JOGADOR_TEVE_GRUPO_ATUALIZADO_PARA_STAFFER.replace("%nick%", playerNick).
                        replace("%setou%", commandSender.getName()).replace("%cargo%", group.getColor() + group.getName().toUpperCase()).
                        replace("%duração%", (tempo == 0L ? "§aETERNA" : "de §a" + DateUtils.formatDifference(tempo)));

                commandSender.sendMessage(BungeeMessages.VOCE_ATUALIZOU_O_CARGO.
                        replace("%cargo%", group.getColor() + group.getName().toUpperCase()).
                        replace("%nick%", playerNick));

                BungeeMain.getManager().warnStaff(message, Groups.MEMBRO);
            }
        }
    }

    private DataHandler getDataHandlerByPlayer(final String nick) {
        DataHandler dataHandler = null;

        try {
            dataHandler = new DataHandler(nick, CommonsGeneral.getUUIDFetcher().getUUID(nick));
        } catch (UUIDFetcherException e) {
            e.printStackTrace();
            return null;
        }

        try {
            dataHandler.load(DataCategory.ACCOUNT);
        } catch (SQLException ex) {
            BungeeMain.console("§cOcorreu um erro ao tentar carregar a categoria 'ACCOUNT' de um jogador -> " + ex.getLocalizedMessage());
            dataHandler = null;
        }

        return dataHandler;
    }
}