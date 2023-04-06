package com.br.guilhermematthew.nowly.hardcoregames;

import java.io.File;

import com.br.guilhermematthew.nowly.hardcoregames.game.GameManager;
import com.br.guilhermematthew.nowly.hardcoregames.listeners.BorderListener;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.MenuListener;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandFramework;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.server.ServerLoadedEvent;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.enums.GameStages;
import com.br.guilhermematthew.nowly.commons.common.utility.system.DateUtils;
import com.br.guilhermematthew.nowly.hardcoregames.game.types.AutomaticEventType;
import com.br.guilhermematthew.nowly.hardcoregames.game.types.NormalType;
import com.br.guilhermematthew.nowly.hardcoregames.listeners.GeneralListeners;
import com.br.guilhermematthew.nowly.hardcoregames.listeners.PreGameListeners;
import com.br.guilhermematthew.nowly.hardcoregames.listeners.ScoreboardListeners;
import com.br.guilhermematthew.nowly.hardcoregames.manager.kit.KitLoader;
import com.br.guilhermematthew.nowly.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import com.br.guilhermematthew.nowly.hardcoregames.manager.structures.StructuresManager;
import com.br.guilhermematthew.nowly.hardcoregames.manager.timer.TimerManager;

import lombok.Getter;

public class HardcoreGamesMain extends JavaPlugin {

	@Getter
	private static HardcoreGamesMain instance;
	
	@Getter
	private static final TimerManager timerManager = new TimerManager();
	
	@Getter
	private static final GameManager gameManager   = new GameManager();
	
	public void onLoad() {
		instance = this;
		
		saveDefaultConfig();
		Bukkit.getServer().unloadWorld("world", false);
		deleteDir(new File("world"));
	}
	
	public void onEnable() {
		if (CommonsGeneral.correctlyStarted()) {
			BukkitServerAPI.registerServer();

			Bukkit.setDefaultGameMode(GameMode.ADVENTURE);
			
			if (CommonsConst.RANDOM.nextBoolean()) {
				String value = getConfig().getString("DOUBLE_KIT");
				HardcoreGamesOptions.DOUBLE_KIT = value.isEmpty();
				console("Partida iniciada no modo DOUBLE KIT!");
			}
			
			getGameManager().setGameType(BukkitMain.getServerType() == ServerType.CHAMPIONS ? new AutomaticEventType() : new NormalType());
			
			KitLoader.load();
			
			BukkitCommandFramework.INSTANCE.loadCommands(this, "com.br.guilhermematthew.nowly.hardcoregames.commands");
			
			getGameManager().setStage(GameStages.WAITING);
			getGameManager().getGameType().initialize();
			
			PluginManager pluginManager = getServer().getPluginManager();
			pluginManager.registerEvents(new BorderListener(), this);
			pluginManager.registerEvents(new GeneralListeners(), this);
			pluginManager.registerEvents(new PreGameListeners(), this);
			pluginManager.registerEvents(new ScoreboardListeners(), this);

			StructuresManager.loadItens();
			HardcoreGamesScoreboard.init();
			MenuListener.registerListeners();
			
			cleanWorld();
		} else {
			Bukkit.shutdown();
		}
	}

	public void onDisable() {
		
	}
	
	public static void console(String msg) {
		Bukkit.getConsoleSender().sendMessage("[HardcoreGames] " + msg);
	}
	
	public static void runAsync(Runnable runnable) {
		Bukkit.getScheduler().runTaskAsynchronously(getInstance(), runnable);	
	}
	
	public static void runLater(Runnable runnable) {
		runLater(runnable, 5);
	}
	
	public static void runLater(Runnable runnable, long ticks) {
		Bukkit.getScheduler().runTaskLater(getInstance(), runnable, ticks);	
	}
	
	private void cleanWorld() {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(getInstance(), () -> {
			World world = getServer().getWorld("world");
			world.setDifficulty(Difficulty.NORMAL);
			world.setAutoSave(false);

			org.bukkit.WorldBorder border = world.getWorldBorder();
			border.setCenter(0, 0);
			border.setSize(806);

			world.setSpawnLocation(0, getServer().getWorlds().get(0).getHighestBlockYAt(0, 0) + 5, 0);
			world.setAutoSave(false);

			((CraftWorld) world).getHandle().savingDisabled = true;

			long time = System.currentTimeMillis();

			try {
				for (int x = 0; x <= 28; x++) {
					for (int z = 0; z <= 28; z++) {
						world.getSpawnLocation().clone().add(x * 16, 0, z * 16).getChunk().load(true);
						world.getSpawnLocation().clone().add(x * -16, 0, z * -16).getChunk().load(true);
						world.getSpawnLocation().clone().add(x * 16, 0, z * -16).getChunk().load(true);
						world.getSpawnLocation().clone().add(x * -16, 0, z * 16).getChunk().load(true);
					}

					if (x % 2 == 0)
						console("[World] Loading chunks! " + DateUtils.formatSeconds((int) ((System.currentTimeMillis() - time) / 1000))
								+ " have passed! - used mem: "
								+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 2L
										/ 1048576L));
				}
			} catch (OutOfMemoryError ex) {
				ex.printStackTrace();
			}

			world.setDifficulty(Difficulty.NORMAL);

			if (world.hasStorm())
				world.setStorm(false);

			world.setTime(0L);
			world.setWeatherDuration(999999999);
			world.setGameRuleValue("doDaylightCycle", "false");
			world.setGameRuleValue("announceAdvancements", "false");

			console("Criando bordas...");

			time = System.currentTimeMillis();

			for (int x = -401; x <= 401; x++)
				if  ((x == -401) || (x == 401))
					for (int z = -401; z <= 401; z++)
						for (int y = 0; y <= 150; y++) {
							world.getBlockAt(x, y, z).setType(Material.BEDROCK);
			}

			for (int z = -401; z <= 401; z++)
				if  ((z == -401) || (z == 401))
					for (int x = -401; x <= 401; x++)
						for (int y = 0; y <= 150; y++) {
							world.getBlockAt(x, y, z).setType(Material.BEDROCK);
			}

			console("Bordas criadas " + DateUtils.formatSeconds((int) ((System.currentTimeMillis() - time) / 1000)));

			world.getEntities().forEach(Entity::remove);

			getServer().getPluginManager().callEvent(new ServerLoadedEvent());
		});
	}
	
	public void deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (String child : children) {
				deleteDir(new File(dir, child));
			}
		}
		dir.delete();
	}
}