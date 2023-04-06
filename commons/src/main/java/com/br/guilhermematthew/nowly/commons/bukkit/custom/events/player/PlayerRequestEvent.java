package com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PlayerRequestEvent extends PlayerCancellableEvent {

    private final String requestType;

    public PlayerRequestEvent(Player player, String requestType) {
        super(player);
        this.requestType = requestType;
    }
}