package com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.tag;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.common.clan.Clan;
import com.br.guilhermematthew.nowly.commons.common.clan.ClanManager;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.profile.GamingProfile;
import com.br.guilhermematthew.nowly.commons.common.profile.addons.League;
import com.br.guilhermematthew.nowly.commons.common.profile.addons.Medals;
import com.br.guilhermematthew.nowly.commons.common.tag.Tag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class TagManager {

    public static final boolean USE_TAGS = true;

    public static void setTag(final Player player, final Groups group) {
        if (!USE_TAGS) return;

        setTag(player, group.getTag());
    }

    public static void setTag(final Player player, final Tag tag) {
        if (!USE_TAGS) return;

        setTag(player, tag, CommonsGeneral.getProfileManager().getGamingProfile(player.getUniqueId()));
    }

    public static void setTag(final Player player, final Tag playerTag, GamingProfile playerProfile) {
        if (!USE_TAGS) return;

        String playerTeamID = playerTag.getTeamCharacter() + player.getUniqueId().toString().substring(0, 12);
        String playerPrefix = playerTag.getColor() + (playerTag.getLevel() == Groups.MEMBRO.getLevel() ? "" : "§l" + playerTag.getPrefix() +
                playerTag.getColor() + " ");
        String playerSuffix = getSuffix(playerProfile);

        Team playerTeam = createTeamIfNotExists(player, player.getName(), playerTeamID, playerPrefix, playerSuffix);

        cleanOldersTeam(player, player.getName(), playerTeam.getName());

        for (Player onlines : Bukkit.getOnlinePlayers()) {
            if (onlines.getUniqueId() == player.getUniqueId()) continue;

            BukkitPlayer bp = BukkitMain.getBukkitPlayer(onlines.getUniqueId());

            String onlineTeamID = bp.getActualTag().getTeamCharacter()
                    + onlines.getUniqueId().toString().substring(0, 12);

            String onlinePrefix = bp.getActualTag().getColor() + (bp.getActualTag().getLevel() == Groups.MEMBRO.getLevel() ? "" : "§l" + bp.getActualTag().getPrefix()
                    + bp.getActualTag().getColor() + " ");

            String onlineSuffix = getSuffix(bp);

            Team onlineTeam = createTeamIfNotExists(player, onlines.getName(), onlineTeamID, onlinePrefix, onlineSuffix);

            cleanOldersTeam(player, onlines.getName(), onlineTeam.getName());

            createTeamIfNotExists(onlines, player.getName(), playerTeam.getName(), playerTeam.getPrefix(), playerTeam.getSuffix());
        }
    }

    private static String getSuffix(final GamingProfile profile) {
        String suffix = "";

        if (BukkitMain.getServerType().useSuffixRank()) {
            League league = League.getRanking(profile.getInt(DataType.XP));

            if (profile.containsFake()) league = League.BronzeI;

            suffix = " " + league.getColor() + league.getSymbol();
        } else {
            if (!profile.containsFake() && !profile.getString(DataType.CLAN).equalsIgnoreCase("Nenhum")) {
                if (profile.getBoolean(DataType.CLAN_TAG_DISPLAY)) {
                    Clan clan = ClanManager.getClan(profile.getString(DataType.CLAN));
                    suffix = " §7[" + clan.getTag() + "]";
                }
            }
        }

        Medals medal = Medals.getMedalById(profile.getInt(DataType.MEDAL));

        if (medal != null) {
            suffix = suffix + " " + medal.getColor() + medal.getSymbol();
        }

        return suffix;
    }

    private static void cleanOldersTeam(final Player player, final String entryName, final String teamName) {
        for (Team team : player.getScoreboard().getTeams()) {
            if (team.hasEntry(entryName) && !team.getName().equals(teamName)) {
                team.unregister();
            }
        }
    }

    public static void removePlayerTag(final String name) {
        for (Player players : Bukkit.getOnlinePlayers()) {
            Team entryTeam = players.getScoreboard().getEntryTeam(name);

            if (entryTeam != null && entryTeam.getEntries().contains(name)) {
                entryTeam.removeEntry(name);

                if (entryTeam.getEntries().isEmpty()) entryTeam.unregister();
            }
        }
    }

    private static Team createTeamIfNotExists(Player p, String entrie, String teamID, String prefix, String suffix) {
        if (p.getScoreboard() == null) {
            p.setScoreboard(Bukkit.getServer().getScoreboardManager().getNewScoreboard());
        }

        Team team = p.getScoreboard().getTeam(teamID);
        if (team == null) team = p.getScoreboard().registerNewTeam(teamID);

        if (!team.hasEntry(entrie)) team.addEntry(entrie);

        team.setPrefix(prefix);
        team.setSuffix(suffix);
        return team;
    }

    public static boolean hasPermission(final Player player, final Groups group) {
        return hasPermission(player, group.getTag());
    }

    public static boolean hasPermission(final Player player, final Tag tag) {
        if (tag.getLevel() == Groups.MEMBRO.getLevel()) {
            return true;
        } else {
            if (player.hasPermission("tag.all"))
                return true;
            if (player.hasPermission("tag." + tag.getName().toLowerCase()))
                return true;

            return CommonsGeneral.getProfileManager().getGamingProfile(player.getUniqueId()).getGroup().getLevel() >= tag
                    .getLevel();
        }
    }

    public static List<Groups> getPlayerGroups(Player player) {
        List<Groups> list = new ArrayList<>();

        for (int i = Groups.values().length; i > 0; i--) {
            Groups tag = Groups.values()[i - 1];

            if (hasPermission(player, tag)) {
                list.add(tag);
            }
        }

        return list;
    }
}