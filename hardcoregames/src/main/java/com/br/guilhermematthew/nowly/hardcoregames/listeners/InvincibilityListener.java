package com.br.guilhermematthew.nowly.hardcoregames.listeners;

import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.StringUtils;
import com.br.guilhermematthew.nowly.hardcoregames.events.game.GameInvincibilityEndEvent;
import com.br.guilhermematthew.nowly.hardcoregames.events.game.GameStageChangeEvent;
import com.br.guilhermematthew.nowly.hardcoregames.events.game.GameTimerEvent;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.Gamer;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;
import com.br.guilhermematthew.nowly.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import com.br.guilhermematthew.nowly.hardcoregames.manager.timer.TimerType;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.PluginManager;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.enums.GameStages;

public class InvincibilityListener implements Listener {
	
	@EventHandler
	public void onTimer(GameTimerEvent event) {
		if (event.getTime() == 0) {
			PluginManager pluginManager = Bukkit.getServer().getPluginManager();
			pluginManager.registerEvents(new GameListener(), HardcoreGamesMain.getInstance());

			pluginManager.callEvent(new GameInvincibilityEndEvent());
			pluginManager.callEvent(new GameStageChangeEvent(GameStages.INVINCIBILITY, GameStages.PLAYING));
		} else {
			event.checkMessage();
		}
	}
	
	@EventHandler
	public void onInvincibilityEnd(GameInvincibilityEndEvent event) {
		int time = 120;
		
		if (BukkitMain.getServerType() == ServerType.CHAMPIONS) {
			time = 60;
		}
		
		HardcoreGamesMain.getGameManager().setStage(GameStages.PLAYING);
		HardcoreGamesMain.getTimerManager().updateTime(time);
		HardcoreGamesMain.getTimerManager().setTimerType(TimerType.COUNT_UP);
		
		for (Gamer gamer : GamerManager.getGamers()) {
			 if (gamer == null) {
				 continue;
			 }
			 if (!gamer.isOnline()) {
				 continue;
			 }
			 HardcoreGamesScoreboard.createScoreboard(gamer);
			 gamer.getPlayer().playSound(gamer.getPlayer().getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
		}
		
		Bukkit.broadcastMessage(StringUtils.INVENCIBILITY_ENDED);
		
		HardcoreGamesMain.getGameManager().getGameType().checkWin();
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamage(EntityDamageEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		event.setCancelled(event.toWeatherState());
	}
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onFood(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onInvencibilityEnd(GameStageChangeEvent event) {
		if (event.getLastStage() == GameStages.INVINCIBILITY &&
				event.getNewStage() == GameStages.PLAYING) {
			
			HardcoreGamesMain.console("Removing listeners from InvincibilityListener");
			
			HandlerList.unregisterAll(this);
		}
	}
}