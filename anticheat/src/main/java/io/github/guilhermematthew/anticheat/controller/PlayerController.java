package io.github.guilhermematthew.anticheat.controller;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlayerController {

    private int clicks;
    private long expireTime = System.currentTimeMillis() + 1000;
    public void addClicks() {
        clicks++;
    }

    private long startTime = System.currentTimeMillis();
    private int click;
    private long lastClick;
    public void addClick() {
        this.click++;
        this.lastClick = System.currentTimeMillis();
    }

}
