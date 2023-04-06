package com.br.guilhermematthew.nowly.commons.bukkit.custom.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class BukkitUpdateEvent extends Event {

    public static final HandlerList handlers = new HandlerList();
    private final UpdateType type;
    private final long currentTick;

    public BukkitUpdateEvent(UpdateType type) {
        this(type, -1);
    }

    public BukkitUpdateEvent(UpdateType type, long currentTick) {
        this.type = type;
        this.currentTick = currentTick;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public enum UpdateType {
        TICK, SEGUNDO, MINUTO, HORA
    }
}