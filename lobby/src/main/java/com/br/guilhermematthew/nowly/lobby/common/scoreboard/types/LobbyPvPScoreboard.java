package com.br.guilhermematthew.nowly.lobby.common.scoreboard.types;

import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import org.bukkit.entity.Player;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.sidebar.Sidebar;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.sidebar.SidebarManager;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import com.br.guilhermematthew.nowly.lobby.common.scoreboard.ScoreboardCommon;

public class LobbyPvPScoreboard extends ScoreboardCommon {


	@Override
	public void createScoreboard(Player player) {
		Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
		BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
		Groups group = bukkitPlayer.getGroup();
		sidebar.setTitle("§2§lLEAGUE");
		
		sidebar.addBlankLine();
		sidebar.addLine("§7Bem-vindo ao PvP");
		sidebar.addLine("§7Selecione um modo!");
		sidebar.addBlankLine();
		sidebar.addLine("rank", "Rank: ", group.getColor() + (group.getLevel() == Groups.MEMBRO.getLevel() ? "Membro" : group.getTag().getColor() + group.getTag().getName()));
        sidebar.addBlankLine();
		sidebar.addLine("lobbyId", "Lobby: ", "§7#" + BukkitMain.getServerID());
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
		sidebar.updateLine("rank", "Rank: ", group.getColor() + (group.getLevel() == Groups.MEMBRO.getLevel() ? "Membro" : group.getTag().getColor() + group.getTag().getName()));
		sidebar.updateLine("onlines", "Players: ", "§a" + StringUtility.formatValue(
				CommonsGeneral.getServersManager().getAmountOnNetwork()));
	}

}