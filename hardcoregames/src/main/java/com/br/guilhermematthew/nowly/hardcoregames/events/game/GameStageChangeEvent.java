package com.br.guilhermematthew.nowly.hardcoregames.events.game;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.br.guilhermematthew.nowly.commons.common.serverinfo.enums.GameStages;

import lombok.Getter;

public class GameStageChangeEvent extends Event {
	
    @Getter
    public static final HandlerList handlerList = new HandlerList();
	
	private GameStages lastStage, newStage;

	public GameStageChangeEvent(GameStages lastStage, GameStages newStage) {
		this.lastStage = lastStage;
		this.newStage = newStage;
	}

	public GameStages getNewStage() {
		return newStage;
	}

	public GameStages getLastStage() {
		return lastStage;
	}
	
    public HandlerList getHandlers() {
        return handlerList;
    }
}
