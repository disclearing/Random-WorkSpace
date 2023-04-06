package com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PlayerChangeVisibilityEvent extends PlayerCancellableEvent {

    private final boolean visibility;

    public PlayerChangeVisibilityEvent(Player player, final boolean visibility) {
        super(player);
        this.visibility = visibility;
    }
}