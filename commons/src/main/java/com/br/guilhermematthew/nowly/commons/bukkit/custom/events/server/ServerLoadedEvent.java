package com.br.guilhermematthew.nowly.commons.bukkit.custom.events.server;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerLoadedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}