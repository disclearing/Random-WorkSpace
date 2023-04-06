package com.br.guilhermematthew.nowly.commons.bukkit.api.cooldown.types;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

public class Cooldown {

    @Getter
    private final String name;

    @Getter
    private final Long duration;
    private final long startTime = System.currentTimeMillis();

    @Getter
    private final boolean barAPI;

    public Cooldown(final String name, final Long duration, final boolean barAPI) {
        this.name = name;
        this.duration = duration;

        this.barAPI = barAPI;
    }

    public double getPercentage() {
        return (getRemaining() * 100) / duration;
    }

    public double getRemaining() {
        long endTime = startTime + TimeUnit.SECONDS.toMillis(duration);
        return (-(System.currentTimeMillis() - endTime)) / 1000D;
    }

    public boolean expired() {
        return getRemaining() < 0D;
    }
}