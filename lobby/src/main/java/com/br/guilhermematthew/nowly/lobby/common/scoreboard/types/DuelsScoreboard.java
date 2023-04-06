package com.br.guilhermematthew.nowly.lobby.common.scoreboard.types;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.sidebar.Sidebar;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.sidebar.SidebarManager;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import com.br.guilhermematthew.nowly.lobby.common.scoreboard.ScoreboardCommon;
import org.bukkit.entity.Player;

public class DuelsScoreboard extends ScoreboardCommon {


	@Override
	public void createScoreboard(Player player) {
		Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
		BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
		Groups group = bukkitPlayer.getGroup();
		sidebar.setTitle("§2§lLEAGUE");
		
		sidebar.addBlankLine();
		sidebar.addLine("§eUse §6/duel <player> §epara");
		sidebar.addLine("§edesafiar um oponente.");
		sidebar.addBlankLine();
		sidebar.addLine("§eGladiator:");
		sidebar.addLine("winsGlad", " Wins: §b" + bukkitPlayer.getData(DataType.GLADIATOR_WINS));
		sidebar.addLine("streakGlad", " Winstreak: §b" + bukkitPlayer.getData(DataType.GLADIATOR_WINSTREAK));
		sidebar.addLine("§eSimulator:");
		sidebar.addLine("winsGlad", " Wins: §b0");
		sidebar.addLine("streakGlad", " Winstreak: §b0");
		sidebar.addBlankLine();
		sidebar.addLine("onlines", "Players: ", "§a" + StringUtility.formatValue(CommonsGeneral.getServersManager().getAmountOnNetwork()));
		sidebar.addBlankLine();
		sidebar.addLine("§aleaguemc.com.br");
		
		sidebar.update();
		sidebar = null;
	}
	
	@Override
	public void updateScoreboard(Player player) {
		Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
		BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
		Groups group = bukkitPlayer.getGroup();
		sidebar.updateLine("onlines", "Players: ", "§a" + StringUtility.formatValue(
				CommonsGeneral.getServersManager().getAmountOnNetwork()));
		sidebar.updateLine("winsGlad", " Wins: §b" + bukkitPlayer.getData(DataType.GLADIATOR_WINS));
		sidebar.updateLine("streakGlad", " Winstreak: §b" + bukkitPlayer.getData(DataType.GLADIATOR_WINSTREAK));
	}

}