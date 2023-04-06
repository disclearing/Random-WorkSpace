package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.bungee.account.BungeePlayer;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.common.clan.Clan;
import com.br.guilhermematthew.nowly.commons.common.clan.ClanManager;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.connections.mysql.MySQLManager;
import com.br.guilhermematthew.nowly.commons.common.data.DataHandler;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.punishment.PunishmentHistoric;
import com.br.guilhermematthew.nowly.commons.common.punishment.PunishmentManager;
import com.br.guilhermematthew.nowly.commons.common.punishment.types.Ban;
import com.br.guilhermematthew.nowly.commons.common.punishment.types.Mute;
import com.br.guilhermematthew.nowly.commons.common.utility.mojang.UUIDFetcher.UUIDFetcherException;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import com.br.guilhermematthew.nowly.commons.common.utility.system.DateUtils;
import com.br.guilhermematthew.nowly.commons.custompackets.PacketType;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketCustomAction;
import com.br.guilhermematthew.servercommunication.server.Server;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

public class PunishCommand implements CommandClass {

    public static void handleBan(ProxiedPlayer banned, String nick, String address, String motive, String baniu,
                                 Long tempo, String clan, String data) {

        String message = BungeeMessages.JOGADOR_FOI_BANIDO_PARA_STAFFER.replace("%baniu%", baniu)
                .replace("%nick%", nick).replace("%duração%", (tempo == 0L ? "PERMANENTEMENTE" : "TEMPORARIAMENTE"));

        BungeeMain.getManager().warnStaff(message, Groups.MEMBRO);

        Ban ban = new Ban(nick, address, baniu, motive, tempo);
        ban.ban();

        PunishmentManager.addCache(nick.toLowerCase(), ban);

        BungeeMain.runAsync(() -> {
            try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
                PreparedStatement insert = connection
                        .prepareStatement("INSERT INTO accounts_to_delete_(nick, timestamp) VALUES (?, ?)");

                insert.setString(1, nick);
                insert.setString(2, String.valueOf(System.currentTimeMillis()));

                insert.close();
                insert = null;

            } catch (SQLException ex) {
                CommonsGeneral.error("Ocorreu um erro ao tentar inserir um nick para ser deletado.");
            }

            if (!clan.equalsIgnoreCase("Nenhum")) {
                removePlayerFromClan(banned, nick, clan);
            }
        });
    }

    public static void handleKickBan(ProxiedPlayer target, String motivo, String baniu, String data) {
        target.disconnect(TextComponent.fromLegacyText(BungeeMessages.VOCE_FOI_BANIDO.replace("%baniu%", baniu).replace("%motivo%", motivo)
                .replace("%data%", data)));
    }

    @SuppressWarnings("deprecation")
    public static void handleMute(String nick, String motive, BungeeCommandSender commandSender, Long tempo) {
        Mute mute = new Mute(nick, commandSender.getNick(), motive, tempo);
        mute.mute();

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(nick);

        if (target != null) {
            if (mute.isPermanent()) {
                target.sendMessage(BungeeMessages.VOCE_FOI_MUTADO_PERMANENTEMENTE.replace("%motivo%", motive));
            } else {
                target.sendMessage(BungeeMessages.VOCE_FOI_MUTADO_TEMPORARIAMENTE.replace("%motivo%", motive)
                        .replace("%duração%", DateUtils.formatDifference(tempo)));
            }

            BungeeMain.getBungeePlayer(target.getName()).getPunishmentHistoric().getMuteHistory().add(mute);
        }

        String message = BungeeMessages.JOGADOR_FOI_MUTADO_PARA_STAFFER.replace("%nick%", nick)
                .replace("%mutou%", commandSender.getNick()).replace("%duração%", (tempo == 0L ? "PERMANENTEMENTE" : "TEMPORARIAMENTE"));

        BungeeMain.getManager().warnStaff(message, Groups.MEMBRO);
        mute = null;
    }

    @SuppressWarnings("deprecation")
    public static void removePlayerFromClan(final ProxiedPlayer quit, final String playerNick, final String clanName) {
        boolean removeClan = false, deleteClan = false;

        if (!ClanManager.hasClanData(clanName)) {
            removeClan = true;

            ClanManager.load(clanName);
        }

        Clan clan = ClanManager.getClan(clanName);

        if (clan != null) {

            if (clan.getMemberList().size() == 1) {
                removeClan = true;
                deleteClan = true;
            } else {
                clan.removePlayer(playerNick);

                if (clan.getOwner().equalsIgnoreCase(playerNick)) {
                    String newOwner = "";

                    if (clan.getAdminList().size() == 0) {
                        removeClan = true;
                        deleteClan = true;

                        for (Object object : clan.getOnlines()) {
                            ProxiedPlayer onlines = (ProxiedPlayer) object;
                            onlines.sendMessage(BungeeMessages.CLAN_DESFEITO);

                            BungeePlayer bungeePlayer = BungeeMain.getBungeePlayer(onlines.getName());
                            bungeePlayer.set(DataType.CLAN, "Nenhum");
                            bungeePlayer.setInClanChat(false);

                            Server.getInstance().getServerGeneral().sendPacket(onlines.getServer().getInfo().getName(),
                                    new CPacketCustomAction(onlines.getName(), onlines.getUniqueId())
                                            .type(PacketType.BUNGEE_SEND_PLAYER_ACTION).field("update-data")
                                            .fieldValue("clan").extraValue("Nenhum"));

                            onlines = null;
                        }
                    } else {
                        newOwner = clan.getAdminList().get(0);

                        clan.setOwner(newOwner);

                        for (Object object : clan.getOnlines()) {
                            ProxiedPlayer onlines = (ProxiedPlayer) object;
                            onlines.sendMessage(BungeeMessages.PLAYER_É_O_NOVO_DONO.replace("%nick%", newOwner));
                            onlines = null;
                        }
                    }
                } else {
                    for (Object object : clan.getOnlines()) {
                        ProxiedPlayer onlines = (ProxiedPlayer) object;
                        onlines.sendMessage(BungeeMessages.PLAYER_SAIU_DO_CLAN.replace("%nick%", playerNick));
                        onlines = null;
                    }

                    if (quit != null) {
                        clan.removeOnline(quit);
                    }
                }
            }

            clan = null;
        } else {
            CommonsGeneral.error("Erro ao remover um jogador de um clan. CLAN = NULO?");
        }

        if (removeClan) {
            ClanManager.saveClan(clanName, true);
        }

        final boolean finalClan = deleteClan;
        BungeeMain.runAsync(() -> {
            MySQLManager.executeUpdate(
                    "UPDATE accounts SET data = JSON_SET(data, '$.clan', 'Nenhum') where nick='" + playerNick + "'");
            if (finalClan) {
                MySQLManager.deleteFromTable("clans", "nome", clanName);
            }
        });
    }

    @Command(name = "unmute", aliases = {"desmutar"}, groupsToUse = {Groups.MOD_PLUS}, runAsync = true)
    public void unmute(BungeeCommandSender commandSender, String label, String[] args) {
        if (args.length != 1) {
            commandSender.sendMessage(BungeeMessages.UNMUTE);
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        if (target != null) {
            BungeePlayer bungeePlayer = BungeeMain.getBungeePlayer(target.getName());

            if (bungeePlayer != null) {
                if (bungeePlayer.getPunishmentHistoric().getActualMute() == null) {
                    commandSender.sendMessage(BungeeMessages.CONTA_NAO_ESTA_MUTADA);
                    return;
                }

                if (!bungeePlayer.getPunishmentHistoric().getActualMute().isPunished()) {
                    commandSender.sendMessage(BungeeMessages.CONTA_NAO_ESTA_MUTADA);
                    return;
                }

                bungeePlayer.getPunishmentHistoric().getActualMute().unmute(commandSender.getNick());

                commandSender.sendMessage(BungeeMessages.CONTA_DESMUTADA);

                bungeePlayer = null;
            }

            target = null;
            return;
        }

        String nick = MySQLManager.getString("accounts", "nick", args[0], "nick");

        if (nick.equalsIgnoreCase("N/A")) {
            commandSender.sendMessage(BungeeMessages.NAO_TEM_CONTA);
            return;
        }

        PunishmentHistoric punishHistoric = new PunishmentHistoric(nick);

        try {
            punishHistoric.loadMutes();
        } catch (Exception ex) {
            commandSender.sendMessage("§cOcorreu um erro, tente novamente.");
            BungeeMain.console(
                    "§cOcorreu um erro ao tentar carregar um punimento (Unmute) -> " + ex.getLocalizedMessage());
            return;
        }

        Mute mute = punishHistoric.getActualMute();

        if (mute == null) {
            commandSender.sendMessage(BungeeMessages.CONTA_NAO_ESTA_MUTADA);
        } else {
            if (mute.isPunished()) {

                mute.unmute(commandSender.getNick());

                commandSender.sendMessage(BungeeMessages.CONTA_DESMUTADA);
                punishHistoric = null;
            }
        }
    }

    @Command(name = "unban", aliases = {"desbanir"}, groupsToUse = {Groups.MOD_PLUS}, runAsync = true)
    public void unban(BungeeCommandSender commandSender, String label, String[] args) {
        if (args.length != 1) {
            commandSender.sendMessage(BungeeMessages.UNBAN);
            return;
        }

        String nick = MySQLManager.getString("accounts", "nick", args[0], "nick");
        if (nick.equalsIgnoreCase("N/A")) {
            commandSender.sendMessage(BungeeMessages.NAO_TEM_CONTA);
            return;
        }

        DataHandler dataHandler = getDataHandlerByPlayer(null, nick);

        if (dataHandler == null) {
            commandSender.sendMessage("§cOcorreu um erro, tente novamente.");
            return;
        }

        Ban ban = PunishmentManager.getBanCache(nick);

        if (ban == null) {
            PunishmentHistoric punishHistoric = getPunishmentHistoric(nick, dataHandler);

            try {
                punishHistoric.loadBans();
            } catch (Exception ex) {
                commandSender.sendMessage("§cOcorreu um erro, tente novamente.");
                BungeeMain.console(
                        "§cOcorreu um erro ao tentar carregar um punimento (Unban) -> " + ex.getLocalizedMessage());
                return;
            } finally {
                ban = punishHistoric.getActualBan();
            }
        }

        if (ban != null && !ban.isPunished()) {
            commandSender.sendMessage(BungeeMessages.CONTA_NAO_ESTA_BANIDA);
            return;
        }

        if (ban != null) {
            ban.unban(commandSender.getNick());
            PunishmentManager.removeCache(nick);
        }

        commandSender.sendMessage(BungeeMessages.CONTA_DESBANIDA);

        BungeeMain.getManager().warnStaff(BungeeMessages.JOGADOR_FOI_DESBANIDO_PARA_STAFFER
                        .replace("%nick%", nick).replace("%staffer%", commandSender.getNick()),
                Groups.MEMBRO);

        dataHandler = null;
    }

    @Command(name = "mute", aliases = {"mutar"}, groupsToUse = {Groups.PRIME}, runAsync = true)
    public void mute(BungeeCommandSender commandSender, String label, String[] args) {
        if (args.length < 2) {
            commandSender.sendMessage(BungeeMessages.MUTE);
            return;
        }

        if (!MySQLManager.contains("accounts", "nick", args[0])) {
            commandSender.sendMessage(BungeeMessages.NAO_TEM_CONTA);
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        boolean continuar = continueProcess(commandSender, target);
        if (!continuar) {
            commandSender.sendMessage(BungeeMessages.DONT_MUTE_ABOVE_YOU);
            return;
        }

        handleMute(args[0], StringUtility.createArgs(1, args), commandSender, 0L);
        commandSender.sendMessage(BungeeMessages.JOGADOR_MUTADO_PERMANENTEMENTE);
    }

    @Command(name = "tempmute", groupsToUse = {Groups.PRIME}, runAsync = true)
    public void tempmute(BungeeCommandSender commandSender, String label, String[] args) {
        if (args.length < 3) {
            commandSender.sendMessage(BungeeMessages.TEMPMUTE);
            return;
        }

        if (!MySQLManager.contains("accounts", "nick", args[0])) {
            commandSender.sendMessage(BungeeMessages.NAO_TEM_CONTA);
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);

        boolean continuar = continueProcess(commandSender, target);
        if (!continuar) {
            commandSender.sendMessage(BungeeMessages.DONT_MUTE_ABOVE_YOU);
            return;
        }

        long tempo = 0;
        try {
            tempo = DateUtils.parseDateDiff(args[1], true);
        } catch (Exception ex) {
            commandSender.sendMessage(BungeeMessages.TEMPO_INVALIDO);
            return;
        }

        handleMute(args[0], StringUtility.createArgs(2, args), commandSender, tempo);
        commandSender.sendMessage(
                BungeeMessages.JOGADOR_MUTADO_TEMPORARIAMENTE);
    }

    @SuppressWarnings("deprecation")
    @Command(name = "tempban", groupsToUse = {Groups.MOD}, runAsync = true)
    public void tempban(BungeeCommandSender commandSender, String label, String[] args) {
        String baniu = commandSender.getNick();

        if (args.length < 3) {
            commandSender.sendMessage(BungeeMessages.TEMPBAN);
            return;
        }

        String nick = MySQLManager.getString("accounts", "nick", args[0], "nick");
        if (nick.equalsIgnoreCase("N/A")) {
            commandSender.sendMessage(BungeeMessages.NAO_TEM_CONTA);
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(nick);

        DataHandler dataHandler = getDataHandlerByPlayer(target, nick);

        if (dataHandler == null) {
            commandSender.sendMessage("§cOcorreu um erro, tente novamente.");
            return;
        }

        String lastAddress = dataHandler.getString(DataType.LAST_IP), clan = dataHandler.getString(DataType.CLAN);

        int codeBan = isBanned(nick, lastAddress);
        if (codeBan == -1) {
            commandSender.sendMessage("§cOcorreu um erro, tente novamente.");
            return;
        }

        if (codeBan == 1) {
            commandSender.sendMessage(BungeeMessages.JOGADOR_JA_BANIDO);
            return;
        }

        String motivo = StringUtility.createArgs(2, args);

        boolean continuar = continueProcess(commandSender, target);
        if (!continuar) {
            commandSender.sendMessage(BungeeMessages.DONT_BAN_PLAYER_ABOVE_YOU);
            return;
        }

        long tempo;
        try {
            tempo = DateUtils.parseDateDiff(args[1], true);
        } catch (Exception ex) {
            commandSender.sendMessage(BungeeMessages.TEMPO_INVALIDO);
            return;
        }

        commandSender.sendMessage(
                BungeeMessages.JOGADOR_BANIDO_TEMPORARIAMENTE.replace("%duração%", DateUtils.formatDifference(tempo)));

        final String data = DateUtils.getActualDate(false);
        handleBan(target, nick, lastAddress, motivo, baniu, tempo, clan, data);

        ProxyServer.getInstance().broadcast(BungeeMessages.JOGADOR_FOI_BANIDO_TEMPORARIAMENTE.replace("%nick%", nick));

        if (target != null) handleKickBan(target, motivo, baniu, data);
    }

    @SuppressWarnings("deprecation")
    @Command(name = "ban", groupsToUse = {Groups.MOD}, runAsync = true)
    public void ban(BungeeCommandSender commandSender, String label, String[] args) {
        String baniu = commandSender.getNick();

        if (args.length < 2) {
            commandSender.sendMessage(BungeeMessages.BAN);
            return;
        }

        String nick = MySQLManager.getString("accounts", "nick", args[0], "nick");
        if (nick.equalsIgnoreCase("N/A")) {
            commandSender.sendMessage(BungeeMessages.NAO_TEM_CONTA);
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(nick);

        DataHandler dataHandler = getDataHandlerByPlayer(target, nick);

        if (dataHandler == null) {
            commandSender.sendMessage("§cOcorreu um erro, tente novamente.");
            return;
        }

        String lastAddress = dataHandler.getString(DataType.LAST_IP), clan = dataHandler.getString(DataType.CLAN);

        int codeBan = isBanned(nick, lastAddress);
        if (codeBan == -1) {
            commandSender.sendMessage("§cOcorreu um erro, tente novamente.");
            return;
        }

        if (codeBan == 1) {
            commandSender.sendMessage(BungeeMessages.JOGADOR_JA_BANIDO);
            return;
        }

        String motivo = StringUtility.createArgs(1, args);

        boolean continuar = continueProcess(commandSender, target);
        if (!continuar) {
            commandSender.sendMessage(BungeeMessages.DONT_BAN_PLAYER_ABOVE_YOU);
            return;
        }

        commandSender.sendMessage(BungeeMessages.JOGADOR_BANIDO_PERMANENTEMENTE);

        final String data = DateUtils.getActualDate(false);
        handleBan(target, nick, lastAddress, motivo, baniu, 0L, clan, data);

        ProxyServer.getInstance().broadcast(BungeeMessages.JOGADOR_FOI_BANIDO_PERMANENTEMENTE.replace("%nick%", nick));

        if (target != null) handleKickBan(target, motivo, baniu, data);
    }

    private int isBanned(final String nick, final String lastAddress) {
        PunishmentHistoric punish = new PunishmentHistoric(nick, lastAddress);

        try {
            punish.loadBans();
        } catch (Exception ex) {
            return -1;
        }

        if (punish.getActualBan() == null)
            return 0;

        return punish.getActualBan().isPunished() ? 1 : 0;
    }

    private boolean continueProcess(final BungeeCommandSender commandSender, final ProxiedPlayer target) {
        if (commandSender.getNick().equalsIgnoreCase("CONSOLE"))
            return true;
        if (!BungeeMain.isValid(target))
            return true;

        final BungeePlayer proxyPlayer = BungeeMain.getBungeePlayer(target.getName()),
                proxyExpulsou = BungeeMain.getBungeePlayer(commandSender.getNick());

		return proxyPlayer.getGroup().getLevel() <= proxyExpulsou.getGroup().getLevel();
	}

    private DataHandler getDataHandlerByPlayer(final ProxiedPlayer target, final String nick) {
        DataHandler dataHandler = null;

        if (BungeeMain.isValid(target)) {
            dataHandler = BungeeMain.getBungeePlayer(target.getName()).getDataHandler();
        } else {
            try {
                dataHandler = new DataHandler(nick, CommonsGeneral.getUUIDFetcher().getUUID(nick));
            } catch (UUIDFetcherException e) {
                e.printStackTrace();
                return null;
            }

            try {
                dataHandler.load(DataCategory.ACCOUNT);
            } catch (SQLException ex) {
                BungeeMain.console("§cOcorreu um erro ao tentar carregar a categoria 'ACCOUNT' de um jogador -> "
                        + ex.getLocalizedMessage());
                dataHandler = null;
            }
        }
        return dataHandler;
    }

    private PunishmentHistoric getPunishmentHistoric(String nick, DataHandler dataHandler) {
        PunishmentHistoric punishmentHistoric = null;

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(nick);
        if (BungeeMain.isValid(target)) {
            punishmentHistoric = BungeeMain.getBungeePlayer(nick).getPunishmentHistoric();
        } else {
            punishmentHistoric = new PunishmentHistoric(nick, dataHandler.getString(DataType.LAST_IP));
        }


        return punishmentHistoric;
    }
}