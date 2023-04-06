package com.br.guilhermematthew.nowly.hardcoregames.manager.scoreboard;

import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesOptions;
import com.br.guilhermematthew.nowly.hardcoregames.manager.scoreboard.types.ChampionsScoreboard;
import com.br.guilhermematthew.nowly.hardcoregames.manager.scoreboard.types.DoubleKitScoreboard;
import com.br.guilhermematthew.nowly.hardcoregames.manager.scoreboard.types.SingleKitScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.Gamer;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;

public class HardcoreGamesScoreboard {

	private static ScoreboardCommon scoreBoardCommon = null;
	public static String SCOREBOARD_TITLE = "";
	
	public static void init() {
		if (BukkitMain.getServerType() == ServerType.HARDCORE_GAMES) {
		scoreBoardCommon = (
				HardcoreGamesOptions.DOUBLE_KIT ? new DoubleKitScoreboard() : new SingleKitScoreboard());
		} else {
			scoreBoardCommon = (new ChampionsScoreboard());
		}
		
		updateTitle();

		Bukkit.getOnlinePlayers().forEach(HardcoreGamesScoreboard::createScoreboard);
	}
	
	public static void createScoreboard(Player player) {
		createScoreboard(GamerManager.getGamer(player.getUniqueId()));
	}
	
	public static void createScoreboard(Gamer gamer) {
		getScoreBoardCommon().create(gamer);
	}
	
	public static void updateTitle() {
		if (BukkitMain.getServerType() == ServerType.EVENTO) {
			SCOREBOARD_TITLE = "§e§lEVENTO";
		} else if (BukkitMain.getServerType() == ServerType.CHAMPIONS) {
			SCOREBOARD_TITLE = "§e§lCHAMPIONS";
		} else {
			SCOREBOARD_TITLE = "§e§l" + (HardcoreGamesOptions.DOUBLE_KIT ? "DOUBLE KIT" : "SINGLE KIT");
		}
	}
	
	public static ScoreboardCommon getScoreBoardCommon() {
		return scoreBoardCommon;
	}
}