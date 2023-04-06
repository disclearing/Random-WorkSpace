package com.br.guilhermematthew.nowly.pvp.events;

import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerCancellableEvent;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlayerDamagePlayerEvent extends PlayerCancellableEvent {
	
    private Player damaged;
    private double damage;
    
    public PlayerDamagePlayerEvent(Player damager, Player damaged, double damage) {
        super(damager);
        this.damaged = damaged;
        this.damage = damage;
    }
}