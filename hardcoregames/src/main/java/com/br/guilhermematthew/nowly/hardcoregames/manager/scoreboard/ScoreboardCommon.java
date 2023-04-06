package com.br.guilhermematthew.nowly.hardcoregames.manager.scoreboard;

import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.br.guilhermematthew.nowly.commons.bukkit.api.vanish.VanishAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.sidebar.Sidebar;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.sidebar.SidebarManager;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.Gamer;

public abstract class ScoreboardCommon {

	public void create(final Gamer gamer) {
		int playerMode = getPlayerMode(gamer.getPlayer(), gamer.isPlaying());
		
		if (playerMode == 1) {
			createGamerScoreboard(gamer.getPlayer(), gamer, SidebarManager.getSidebar(gamer.getUniqueId()));
		} else {
			createSpecScoreboard(gamer.getPlayer(), gamer, SidebarManager.getSidebar(gamer.getUniqueId()), playerMode);
		}
	}
	
	public void updateGaming(final Player player) {
		Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
		sidebar.updateLine("gaming", "Players: ", "§7" + getGaming() + "§7/" + Bukkit.getMaxPlayers());
	}

	public void updateTime(final Player player) {
		Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());

		sidebar.updateLine("time", getMessageTime(), "§7" + HardcoreGamesMain.getTimerManager().getLastFormatted());
	}
	
	public void updateKit1(final Player player, String kitName) {
		Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
		Gamer gamerkit = GamerManager.getGamer(player.getUniqueId());
		sidebar.updateLine("kit1", "§fKit 1: ", "§a" + kitName);

	}
	
	public void updateKit2(final Player player, String kitName) {
		Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
		Gamer gamerkit = GamerManager.getGamer(player.getUniqueId());
		sidebar.updateLine("kit2", "§fKit 2: ", "§a" + kitName);

	}
	
	public void updateKills(final Player player, int kills) {
		Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
		sidebar.updateLine("kills", "§fKills: ", "§a" + kills);
	}
	
	public void updatePlayerMode(Gamer gamer) {
		int playerMode = getPlayerMode(gamer.getPlayer(), gamer.isPlaying());
		
		Sidebar sidebar = SidebarManager.getSidebar(gamer.getUniqueId());
		sidebar.updateLine("playerMode", "", getPlayerMode(playerMode));
	}
	
	public abstract void createGamerScoreboard(Player player, Gamer gamer, Sidebar sidebar);
	public abstract void createSpecScoreboard(Player player, Gamer gamer, Sidebar sidebar, int playerMode);
	
	public void createSpecScoreboard(Player player, Gamer gamer, Sidebar sidebar) {
		createSpecScoreboard(player, gamer, sidebar, getPlayerMode(player, gamer.isPlaying()));
	}
	
	private int getPlayerMode(final Player player, boolean playing) {
		return playing ? 1 : (VanishAPI.inAdmin(player) ? 2 : 0);
	}

	public String getMessageTime() {
		return (HardcoreGamesMain.getGameManager().isPreGame() ? "Iniciando em: " : 
			HardcoreGamesMain.getGameManager().isInvencibilidade() ? "Invencível por: " : "Tempo: ");
	}
	
	public String getGaming() {
		return "" + HardcoreGamesMain.getTimerManager().getLastAlive();
	}
	
	public String getPlayerMode(int playerMode) {
		return playerMode == 2 ? "§cMODO VANISH" : "§8SPECTATOR";
	}
}