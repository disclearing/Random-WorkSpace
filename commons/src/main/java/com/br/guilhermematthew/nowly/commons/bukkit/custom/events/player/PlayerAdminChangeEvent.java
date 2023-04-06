package com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PlayerAdminChangeEvent extends PlayerCancellableEvent {

    private final AdminChangeType changeType;

    public PlayerAdminChangeEvent(Player player, final AdminChangeType changeType) {
        super(player);
        this.changeType = changeType;
    }

    public enum AdminChangeType {
        ENTROU, SAIU
    }
}