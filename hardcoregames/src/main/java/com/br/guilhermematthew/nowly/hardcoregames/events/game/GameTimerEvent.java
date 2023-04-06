package com.br.guilhermematthew.nowly.hardcoregames.events.game;

import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.br.guilhermematthew.nowly.commons.common.utility.system.DateUtils;

import lombok.Getter;

public class GameTimerEvent extends Event {
	
    @Getter
    public static final HandlerList handlerList = new HandlerList();
	
    @Getter
    private int time;

	@Getter
	private boolean changedTime = false;

	public void setTime(int time) {
		this.time = time;
		changedTime = true;
	}

    public GameTimerEvent(final int time) {
    	this.time = time;
    }
    
    public HandlerList getHandlers() {
        return handlerList;
    }
    
	public void checkMessage() {
		if (((time >= 10 ? 1 : 0) & (time % 60 == 0 ? 1 : 0)) != 0) {
			handleNotify(Sound.NOTE_PLING, getMensagem(time));
		} else if (time == 30 || time == 15 || time == 10 || time <= 5) {
			handleNotify(Sound.NOTE_PLING, getMensagem(time));
		}
	}
	
	private String getMensagem(int tempo) {
		if (HardcoreGamesMain.getGameManager().isPreGame()) {
			return StringUtils.GAME_START_IN.replace("%tempo%", DateUtils.formatSecondsScore(tempo));
		} else if (HardcoreGamesMain.getGameManager().isInvencibilidade()) {
			return StringUtils.INVENCIBILITY_END_IN.replace("%tempo%", DateUtils.formatSecondsScore(tempo));
		}
		return "";
	}
	
	private void handleNotify(Sound sound, String message) {
    	for (Player player : Bukkit.getOnlinePlayers()) {
		     player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
    	}
    	
		Bukkit.broadcastMessage(message);
    }
}