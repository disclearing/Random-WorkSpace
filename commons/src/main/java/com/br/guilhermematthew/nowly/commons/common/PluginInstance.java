package com.br.guilhermematthew.nowly.commons.common;

public enum PluginInstance {

    BUNGEECORD, BUKKIT, UNKNOWN;

    public boolean isBukkit() {
        return this == BUKKIT;
    }

    public boolean isBungee() {
        return this == BUNGEECORD;
    }
}