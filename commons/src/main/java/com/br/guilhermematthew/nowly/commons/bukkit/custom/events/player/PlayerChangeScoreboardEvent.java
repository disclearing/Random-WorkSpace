package com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PlayerChangeScoreboardEvent extends PlayerCancellableEvent {

    private final ScoreboardChangeType changeType;

    public PlayerChangeScoreboardEvent(Player player, final ScoreboardChangeType changeType) {
        super(player);
        this.changeType = changeType;
    }

    public enum ScoreboardChangeType {
        ATIVOU, DESATIVOU
    }
}