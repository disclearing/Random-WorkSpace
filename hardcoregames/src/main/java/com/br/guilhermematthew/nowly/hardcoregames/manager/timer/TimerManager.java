package com.br.guilhermematthew.nowly.hardcoregames.manager.timer;

import org.bukkit.Bukkit;

import com.br.guilhermematthew.nowly.commons.common.utility.system.DateUtils;
import com.br.guilhermematthew.nowly.hardcoregames.events.game.GameTimerEvent;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;
import com.br.guilhermematthew.nowly.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

@Getter @Setter
public class TimerManager {

	private AtomicInteger time             = new AtomicInteger(300);
	private String lastFormatted    = "";
	private int    lastAlive        = 0;
	
	@Setter
	private TimerType timerType = TimerType.COUNTDOWN;
	
	public void onSecond() {
		switch (timerType) {
		case COUNTDOWN:
			time.getAndDecrement();
			break;
		case COUNT_UP:
			time.getAndIncrement();
			break;
		default:
			break;
		}
		
		GameTimerEvent event = new GameTimerEvent(getTime().get());
		Bukkit.getServer().getPluginManager().callEvent(event);
		
		if (event.isChangedTime()) {
			this.time.set(event.getTime());
			lastFormatted = DateUtils.formatSecondsScore(getTime().get());
			
			Bukkit.getOnlinePlayers().forEach(onlines -> 
			HardcoreGamesScoreboard.getScoreBoardCommon().updateTime(onlines));
		}
		
		this.lastFormatted = DateUtils.formatSecondsScore(getTime().get());
		this.lastAlive = GamerManager.getAliveGamers().size();
	}
	
	public void updateAlive() {
		this.lastAlive = GamerManager.getAliveGamers().size();
	}
	
	public void updateTime(int newTime) {
		time.set(newTime);

		this.lastFormatted = DateUtils.formatSecondsScore(getTime().get());
	}
	
	public String getLastFormatted() {
		return lastFormatted;
	}

	public int getLastAlive() {
		return lastAlive;
	}
}