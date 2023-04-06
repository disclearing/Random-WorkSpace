package com.br.guilhermematthew.nowly.commons.bungee.events;

import net.md_5.bungee.api.plugin.Event;

public class BungeeUpdateEvent extends Event {

    private final BungeeUpdateType type;
    private final long seconds;

    public BungeeUpdateEvent(BungeeUpdateType type, long seconds) {
        this.type = type;
        this.seconds = seconds;
    }

    public BungeeUpdateType getType() {
        return type;
    }

    public long getSeconds() {
        return seconds;
    }

    public enum BungeeUpdateType {
        SEGUNDO, MINUTO, HORA
    }
}
