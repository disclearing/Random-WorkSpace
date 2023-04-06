package com.br.guilhermematthew.nowly.commons.bukkit.api.hologram;

import lombok.Getter;
import lombok.val;

@Getter
public class PlayerTop implements Comparable<PlayerTop> {

    private final String playerName;
	private final int value;

    public PlayerTop(String playerName, int value) {
        this.playerName = playerName;
        this.value = value;
    }

    @Override
    public int compareTo(PlayerTop o) {
        return o.getValue() - getValue();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof PlayerTop)) return false;

        val topObj = (PlayerTop) obj;

        return topObj.getPlayerName().equals(playerName) && topObj.getValue() == value;
    }
}