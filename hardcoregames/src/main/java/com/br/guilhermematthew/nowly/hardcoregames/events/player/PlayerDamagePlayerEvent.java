package com.br.guilhermematthew.nowly.hardcoregames.events.player;

import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerCancellableEvent;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PlayerDamagePlayerEvent extends PlayerCancellableEvent {
	
    private Player damaged;
    
    @Setter
    private double damage;
    
    @Setter
    private boolean cancelled;

    public PlayerDamagePlayerEvent(Player damager, Player damaged, double damage) {
        super(damager);
        this.damaged = damaged;
        this.damage = damage;
    }
}