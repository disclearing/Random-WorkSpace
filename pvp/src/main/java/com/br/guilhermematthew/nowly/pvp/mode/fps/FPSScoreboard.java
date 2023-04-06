package com.br.guilhermematthew.nowly.pvp.mode.fps;

import org.bukkit.entity.Player;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.sidebar.Sidebar;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.sidebar.SidebarManager;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.profile.addons.League;

public class FPSScoreboard {

	public static void createScoreboard(Player player) {
		Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
		sidebar.setTitle("§b§lARENA: FPS");
		
		BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
		League league = League.getRanking(bukkitPlayer.getInt(DataType.XP));
		
		sidebar.addBlankLine();
		sidebar.addLine("kills", "Kills: ", "§7" + bukkitPlayer.getIntFormatted(DataType.PVP_KILLS));
		sidebar.addLine("deaths", "Deaths: ", "§7" + bukkitPlayer.getIntFormatted(DataType.PVP_DEATHS));
		sidebar.addLine("killstreak", "Killstreak: ", "§a" + bukkitPlayer.getIntFormatted(DataType.PVP_KILLSTREAK));
		sidebar.addBlankLine();
		sidebar.addLine("coins", "Coins: ", "§6" + bukkitPlayer.getIntFormatted(DataType.COINS));
		sidebar.addBlankLine();
		sidebar.addLine("§aleaguemc.com.br");
		
		sidebar.update();
		
		bukkitPlayer = null;
		league = null;
		sidebar = null;
	}
	
	public static void updateScoreboard(Player player) {
		Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());

		BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
		League league = League.getRanking(bukkitPlayer.getInt(DataType.XP));
		
		sidebar.updateLine("killstreak", "Killstreak: ", "§a" + bukkitPlayer.getIntFormatted(DataType.PVP_KILLSTREAK));
		sidebar.updateLine("kills", "Kills: ", "§7" + bukkitPlayer.getIntFormatted(DataType.PVP_KILLS));
		sidebar.updateLine("deaths", "Deaths: ", "§7" + bukkitPlayer.getIntFormatted(DataType.PVP_DEATHS));
		sidebar.updateLine("coins", "Coins: ", "§6" + bukkitPlayer.getIntFormatted(DataType.COINS));
		
		bukkitPlayer = null;
		sidebar = null;
	}
}