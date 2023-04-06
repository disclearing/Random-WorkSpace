package com.br.guilhermematthew.nowly.hardcoregames.events.game;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

public class GameInvincibilityEndEvent extends Event {
	
    @Getter
    public static final HandlerList handlerList = new HandlerList();
	
    public HandlerList getHandlers() {
        return handlerList;
    }
}