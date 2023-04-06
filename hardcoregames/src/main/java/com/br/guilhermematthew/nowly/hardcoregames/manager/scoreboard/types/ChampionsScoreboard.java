package com.br.guilhermematthew.nowly.hardcoregames.manager.scoreboard.types;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.sidebar.Sidebar;
import com.br.guilhermematthew.nowly.commons.common.connections.mysql.MySQLManager;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.profile.addons.League;
import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.Gamer;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;
import com.br.guilhermematthew.nowly.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import com.br.guilhermematthew.nowly.hardcoregames.manager.scoreboard.ScoreboardCommon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChampionsScoreboard extends ScoreboardCommon {

	@Override
	public void createGamerScoreboard(Player player, Gamer gamer, Sidebar sidebar) {
		sidebar.hide();
		sidebar.show();
		
		sidebar.setTitle(HardcoreGamesScoreboard.SCOREBOARD_TITLE);
		
		League league = League.getRanking(BukkitMain.getBukkitPlayer(player.getUniqueId()).getInt(DataType.XP));
		Gamer gamerkit = GamerManager.getGamer(player.getUniqueId());
		sidebar.addBlankLine();
		sidebar.addLine("time", getMessageTime(), "§7" + HardcoreGamesMain.getTimerManager().getLastFormatted());
		sidebar.addLine("gaming", "Players: ", "§7" + getGaming() + "§7/" + Bukkit.getMaxPlayers());
		sidebar.addBlankLine();
		sidebar.addLine("kit1", "§fKit 1: ", "§a" + gamer.getKit1());
		if (HardcoreGamesMain.getGameManager().isGaming() && gamer.getKills() >= 1) {
			sidebar.addLine("kills", "§fKills: ", "§a" + gamer.getKills());
		}
		sidebar.addBlankLine();
		sidebar.addLine("mode", "Modo: ", "§aSolo");
		sidebar.addLine("rank", "Rank: ", "§7" + league.getColor() + league.getSymbol() + "§7(" + MySQLManager.getPlayerPositionRanking(BukkitMain.getBukkitPlayer(player.getUniqueId()).getNick()) + "§7º)");
		sidebar.addBlankLine();
		sidebar.addLine("§aleaguemc.com.br");
		
		sidebar.update();
	}

	@Override
	public void createSpecScoreboard(Player player, Gamer gamer, Sidebar sidebar, int playerMode) {
		sidebar.hide();
		sidebar.show();
		
		sidebar.setTitle(HardcoreGamesScoreboard.SCOREBOARD_TITLE);
		
		League league = League.getRanking(BukkitMain.getBukkitPlayer(player.getUniqueId()).getInt(DataType.XP));
		
		sidebar.addBlankLine();
		sidebar.addLine("time", getMessageTime(), "§7" + HardcoreGamesMain.getTimerManager().getLastFormatted());
		sidebar.addLine("gaming", "Players: ", "§7" + getGaming());
		sidebar.addBlankLine();
		sidebar.addLine("playerMode", "", getPlayerMode(playerMode));
		sidebar.addBlankLine();
		sidebar.addLine("rank", "Rank: ", "§7" + league.getColor() + league.getSymbol() + "§7(" + MySQLManager.getPlayerPositionRanking(BukkitMain.getBukkitPlayer(player.getUniqueId()).getNick()) + "§7)");
		sidebar.addBlankLine();
		sidebar.addLine("§aleaguemc.com.br");
		
		sidebar.update();
	}
}