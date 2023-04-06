package com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerCancellableEvent extends Event implements Cancellable {
    public Player player;

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    public PlayerCancellableEvent(Player player) {
        this(player, false);
    }

    public PlayerCancellableEvent(Player player, boolean async) {
        super(async);
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
