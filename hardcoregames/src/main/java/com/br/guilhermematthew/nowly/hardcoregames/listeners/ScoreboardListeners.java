package com.br.guilhermematthew.nowly.hardcoregames.listeners;

import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.events.game.GameStageChangeEvent;
import com.br.guilhermematthew.nowly.hardcoregames.events.game.GameTimerEvent;
import com.br.guilhermematthew.nowly.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.server.ServerStopEvent;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.enums.GameStages;

public class ScoreboardListeners implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		update(1);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		update(1);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTimer(GameTimerEvent event) {
		update(2);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent event) {
		update(1);
	}
	
	public void update(int code) {
		HardcoreGamesMain.runLater(() -> {
			if (code == 1) {
				Bukkit.getOnlinePlayers().stream().filter(check -> check != null).filter(check -> check.isOnline())
				.forEach(onlines -> HardcoreGamesScoreboard.getScoreBoardCommon().updateGaming(onlines));
			} else if (code == 2) {
				Bukkit.getOnlinePlayers().stream().filter(check -> check != null).filter(check -> check.isOnline())
				.forEach(onlines -> HardcoreGamesScoreboard.getScoreBoardCommon().updateTime(onlines));
			}
		});
	}
	
	@EventHandler
	public void onStop(ServerStopEvent event) {
		HandlerList.unregisterAll(this);
	}
	
	@EventHandler
	public void onGameEnd(GameStageChangeEvent event) {
		if (event.getNewStage() == GameStages.END) {
			HardcoreGamesMain.console("Removing listeners from ScoreboardListeners");
			
			HandlerList.unregisterAll(this);
		}
	}
}