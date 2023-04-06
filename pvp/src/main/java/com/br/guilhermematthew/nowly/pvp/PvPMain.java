package com.br.guilhermematthew.nowly.pvp;

import com.br.guilhermematthew.nowly.pvp.mode.arena.ArenaMode;
import com.br.guilhermematthew.nowly.pvp.mode.fps.FPSMode;
import com.br.guilhermematthew.nowly.pvp.mode.lavachallenge.LavaChallengeMode;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandFramework;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.server.ServerLoadedEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.utility.loader.BukkitListeners;

import lombok.Getter;
import lombok.Setter;

public class PvPMain extends JavaPlugin {

	@Getter @Setter
	private static PvPMain instance;
	
	public void onLoad() {
		setInstance(this);
		
		saveDefaultConfig();
	}
	
	public void onEnable() {
		if (CommonsGeneral.correctlyStarted()) {
			BukkitServerAPI.registerServer();
			
			boolean findedMode = false;
			
			switch (BukkitMain.getServerType()) {
			case PVP_FPS:
				FPSMode.init();
				findedMode = true;
				break;
			case PVP_LAVACHALLENGE:
				LavaChallengeMode.init();
				
				LavaChallengeMode.updateDifficulty(getConfig().getInt("LavaChallenge.MinHit.Facil"),
						getConfig().getInt("LavaChallenge.MinHit.Medio"),
						getConfig().getInt("LavaChallenge.MinHit.Dificil"),
						getConfig().getInt("LavaChallenge.MinHit.Extremo"));
				
				findedMode = true;
				break;
			case PVP_ARENA:
				ArenaMode.init();
				findedMode = true;
				break;
			default:
				break;
			}
			
			if (!findedMode) {
				Bukkit.shutdown();
				return;
			}
			
			BukkitListeners.loadListeners(getInstance(), "com.br.guilhermematthew.nowly.pvp.listeners");
			BukkitCommandFramework.INSTANCE.loadCommands(this, "com.br.guilhermematthew.nowly.pvp.commands");
			
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
		Bukkit.getConsoleSender().sendMessage("[PvP] " + msg);
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