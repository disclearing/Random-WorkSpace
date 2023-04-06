package com.br.guilhermematthew.nowly.commons.common.serverinfo.enums;

public enum GameType {

    UNKNOWN("Unknown"),
    SOLO("Solo"),
    DUO("Duo");

    private final String name;

    GameType(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}