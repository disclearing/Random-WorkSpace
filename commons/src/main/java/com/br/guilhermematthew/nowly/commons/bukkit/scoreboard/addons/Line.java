package com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.addons;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Line {

    private String name, prefix, suffix;
    private int lineId;

    public Line(String name, String prefix, String suffix, int lineId) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
        this.lineId = lineId;
    }
}