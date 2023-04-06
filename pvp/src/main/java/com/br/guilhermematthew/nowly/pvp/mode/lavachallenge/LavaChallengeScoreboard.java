package com.br.guilhermematthew.nowly.pvp.mode.lavachallenge;

import org.bukkit.entity.Player;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.sidebar.Sidebar;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.sidebar.SidebarManager;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.profile.addons.League;

public class LavaChallengeScoreboard {

	public static void createScoreboard(Player player) {
		Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
		sidebar.hide();
		sidebar.show();
		
		sidebar.setTitle("§b§lARENA: LAVA");
		
		BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
		League league = League.getRanking(bukkitPlayer.getInt(DataType.XP));
		
		sidebar.addBlankLine();
		sidebar.addLine("coins", "Coins: ", "§6" + bukkitPlayer.getIntFormatted(DataType.COINS));
		sidebar.addBlankLine();
		sidebar.addLine("§aleaguemc.com.br");
		
		sidebar.update();
	}
	
	public static void updateScoreboard(Player player) {
		Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());

		BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
		League league = League.getRanking(bukkitPlayer.getInt(DataType.XP));

		sidebar.addLine("coins", "Coins: ", "§6" + bukkitPlayer.getIntFormatted(DataType.COINS));
	}
}