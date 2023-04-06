package com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerEvent extends Event {
    public Player player;

    private static final HandlerList handlers = new HandlerList();

    public PlayerEvent(Player who) {
        this(who, false);
    }

    public PlayerEvent(Player who, boolean async) {
        super(async);
        this.player = who;
    }

    public Player getPlayer() {
        return this.player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}