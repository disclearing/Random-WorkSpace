package com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@Setter
public class PlayerUpdateTabEvent extends PlayerEvent {

    private String header, footer;

    public PlayerUpdateTabEvent(Player player) {
        super(player);
    }
}