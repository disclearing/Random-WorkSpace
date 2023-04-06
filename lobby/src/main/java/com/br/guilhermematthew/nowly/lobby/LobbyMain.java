package com.br.guilhermematthew.nowly.lobby;

import com.br.guilhermematthew.nowly.lobby.common.scoreboard.ScoreboardInstance;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandFramework;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.server.ServerLoadedEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.manager.configuration.PluginConfiguration;
import com.br.guilhermematthew.nowly.commons.bukkit.utility.loader.BukkitListeners;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import com.br.guilhermematthew.nowly.lobby.common.hologram.HologramCommon;
import com.br.guilhermematthew.nowly.lobby.common.hologram.types.LobbyHGHologram;
import com.br.guilhermematthew.nowly.lobby.common.hologram.types.LobbyPvPHologram;
import com.br.guilhermematthew.nowly.lobby.common.npcs.NPCCommon;
import com.br.guilhermematthew.nowly.lobby.common.npcs.types.LobbyHGNPC;
import com.br.guilhermematthew.nowly.lobby.common.npcs.types.LobbyNPC;
import com.br.guilhermematthew.nowly.lobby.common.npcs.types.LobbyPvPNPC;
import com.br.guilhermematthew.nowly.lobby.common.scoreboard.ScoreboardCommon;
import com.br.guilhermematthew.nowly.lobby.common.scoreboard.types.LobbyHGScoreboard;
import com.br.guilhermematthew.nowly.lobby.common.scoreboard.types.LobbyPvPScoreboard;
import com.br.guilhermematthew.nowly.lobby.common.scoreboard.types.LobbyScoreboard;

import lombok.Getter;
import lombok.Setter;

public class LobbyMain extends JavaPlugin {

	@Getter @Setter
	private static LobbyMain instance;
	
	@Getter @Setter
	private static Location spawn;
	
	@Getter @Setter
	private static ScoreboardCommon scoreBoardCommon;
	@Getter @Setter
	private static LobbyScoreboard lobbyBoardCommon;
	
	@Getter @Setter
	private static NPCCommon npcCommon;

	@Getter @Setter
	private static ScoreboardInstance scoreboardInstance;

	@Getter @Setter
	private static HologramCommon hologramCommon;
	
	public void onLoad() {
		setInstance(this);
		
		saveDefaultConfig();
	}

	public void onEnable() {
		if (CommonsGeneral.correctlyStarted()) {
			BukkitServerAPI.registerServer();
			
			Bukkit.setDefaultGameMode(GameMode.ADVENTURE);
			setScoreboardInstance(new ScoreboardInstance());
			CommonsGeneral.getServersManager().init();
			
			BukkitListeners.loadListeners(getInstance(), "com.br.guilhermematthew.nowly.lobby.listeners");
			BukkitCommandFramework.INSTANCE.loadCommands(getInstance(), "com.br.guilhermematthew.nowly.lobby.commands");
			
			BukkitMain.getManager().enablePacketInjector(this);
			BukkitMain.getManager().enableHologram(this);
			BukkitMain.getManager().enableNPC(this);

			setSpawn(PluginConfiguration.createLocation(getInstance(), "spawn"));
			
			setScoreBoardCommon(
					BukkitMain.getServerType() == ServerType.LOBBY               ? new LobbyScoreboard()    : 
				    BukkitMain.getServerType() == ServerType.LOBBY_PVP           ? new LobbyPvPScoreboard() :
				    BukkitMain.getServerType() == ServerType.LOBBY_HARDCOREGAMES ? new LobbyHGScoreboard()  : null);

			if (getScoreBoardCommon() == null) {
				console("Nao foi encontrada o padrao da ScoreboardCommon.");
			}
			
			setNpcCommon(
					BukkitMain.getServerType() == ServerType.LOBBY               ? new LobbyNPC()   : 
					BukkitMain.getServerType() == ServerType.LOBBY_HARDCOREGAMES ? new LobbyHGNPC() : 
					BukkitMain.getServerType() == ServerType.LOBBY_PVP           ? new LobbyPvPNPC() : null);

			if (getNpcCommon() == null) {
				console("Nao foi encontrado o padrao do NPCCommon.");
			} else {
				getNpcCommon().create();
			}
			
			setHologramCommon(
					BukkitMain.getServerType() == ServerType.LOBBY               ? null   : 
					BukkitMain.getServerType() == ServerType.LOBBY_HARDCOREGAMES ? new LobbyHGHologram()   : 
					BukkitMain.getServerType() == ServerType.LOBBY_PVP           ? new LobbyPvPHologram()  : null);
			
			if (getHologramCommon() == null) {
				console("Nao foi encontrado o padrao do HologramCommon.");
			} else {
				getHologramCommon().create();
			}
			
			runLater(() -> {
				getServer().getPluginManager().callEvent(new ServerLoadedEvent());
			}, BukkitMain.getServerType().getSecondsToStabilize());
		} else {
			Bukkit.shutdown();
		}
	}
	
	public void onDisable() {
		
	}

	public static void console(String msg) {
		Bukkit.getConsoleSender().sendMessage("[Lobby] " + msg);
	}
	
	public static void runAsync(Runnable runnable) {
		Bukkit.getScheduler().runTaskAsynchronously(getInstance(), runnable);	
	}
	
	public static void runLater(Runnable runnable) {
		Bukkit.getScheduler().runTaskLater(getInstance(), runnable, 5L);	
	}
	
	public static void runLater(Runnable runnable, long ticks) {
		Bukkit.getScheduler().runTaskLater(getInstance(), runnable, ticks);	
	}
}