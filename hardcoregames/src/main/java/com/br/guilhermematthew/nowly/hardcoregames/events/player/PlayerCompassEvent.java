package com.br.guilhermematthew.nowly.hardcoregames.events.player;

import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerEvent;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlayerCompassEvent extends PlayerEvent {
	
	private CompassAction action;
	private Player target;
	
    public PlayerCompassEvent(Player player, CompassAction action) {
		super(player);
		this.action = action;
	}
    
	public enum CompassAction {
		LEFT, RIGHT;
	}
}