package com.br.guilhermematthew.nowly.lobby.common.scoreboard.types;

import com.br.guilhermematthew.nowly.lobby.LobbyMain;
import lombok.val;
import org.bukkit.entity.Player;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.sidebar.Sidebar;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.sidebar.SidebarManager;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import com.br.guilhermematthew.nowly.lobby.common.scoreboard.ScoreboardCommon;

public class LobbyHGScoreboard extends ScoreboardCommon {


	@Override
	public void createScoreboard(Player player) {
		BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
		
		Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());

		sidebar.setTitle("§2§lLEAGUE");
		
		sidebar.addBlankLine();
		sidebar.addLine("§eHG Mix:");
		sidebar.addLine("hgWins", " Wins: §a" + bukkitPlayer.getIntFormatted(DataType.HG_WINS));
		sidebar.addLine("hgKills", " Kills: §a" + bukkitPlayer.getIntFormatted(DataType.HG_KILLS));
		sidebar.addBlankLine();
		sidebar.addLine("§eChampions:");
		sidebar.addLine("eventWins", " Wins: §a" + bukkitPlayer.getIntFormatted(DataType.HG_EVENT_WINS));
		sidebar.addLine("eventKills", " Kills: §a" + bukkitPlayer.getIntFormatted(DataType.HG_EVENT_KILLS));
		sidebar.addBlankLine();
		sidebar.addLine("coins", "Coins: §6" + bukkitPlayer.getIntFormatted(DataType.COINS));
		sidebar.addLine("onlines", "Players: ", "§a" + StringUtility.formatValue(CommonsGeneral.getServersManager().getAmountOnNetwork()));
		sidebar.addBlankLine();
		sidebar.addLine("§aleaguemc.com.br");
		
		sidebar.update();
	}
	
	@Override
	public void updateScoreboard(Player player) {
		val sidebar = SidebarManager.getSidebar(player.getUniqueId());
		if(sidebar == null) return;
		BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());

		sidebar.updateLine("hgWins", " Wins: §a" + bukkitPlayer.getIntFormatted(DataType.HG_WINS));
		sidebar.updateLine("hgKills", " Kills: §a" + bukkitPlayer.getIntFormatted(DataType.HG_KILLS));
		sidebar.updateLine("eventWins", " Wins: §a" + bukkitPlayer.getIntFormatted(DataType.HG_EVENT_WINS));
		sidebar.updateLine("eventKills", " Kills: §a" + bukkitPlayer.getIntFormatted(DataType.HG_EVENT_KILLS));

		sidebar.updateLine("onlines", "Players: ", "§a" + StringUtility.formatValue(
				CommonsGeneral.getServersManager().getAmountOnNetwork()));
	}

}