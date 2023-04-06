package com.br.guilhermematthew.nowly.login;

import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandFramework;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.server.ServerLoadedEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.manager.configuration.PluginConfiguration;
import com.br.guilhermematthew.nowly.commons.bukkit.utility.loader.BukkitListeners;
import com.br.guilhermematthew.nowly.login.manager.LoginManager;

import lombok.Getter;
import lombok.Setter;

public class LoginMain extends JavaPlugin {

	@Getter @Setter
	private static LoginMain instance;
	
	@Getter @Setter
	public static Location spawn;
	
	@Getter @Setter
	private static LoginManager manager;
	
	public void onEnable() {
		if (CommonsGeneral.correctlyStarted()) {
			setInstance(this);
			
			setManager(new LoginManager());
			
			PluginConfiguration.createLocation(getInstance(), "spawn");
			setSpawn(PluginConfiguration.getLocation(getInstance(), "spawn"));
			
			BukkitListeners.loadListeners(getInstance(), "com.br.guilhermematthew.nowly.login.listener");

			BukkitCommandFramework.INSTANCE.loadCommands(this, "com.br.guilhermematthew.nowly.login.commands");

			runLater(() -> {
				getServer().getPluginManager().callEvent(new ServerLoadedEvent());
			}, BukkitMain.getServerType().getSecondsToStabilize() + 40);
		} else {
			Bukkit.shutdown();
		}
	}
	
	public void onDisable() {
		getManager().removeGamers(false);
	}
	
	public static void console(String msg) {
		Bukkit.getConsoleSender().sendMessage("[Login] " + msg);
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