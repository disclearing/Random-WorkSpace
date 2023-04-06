package com.br.guilhermematthew.nowly.hardcoregames.events.player;

import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerEvent;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PlayerSpectateEvent extends PlayerEvent {
	
	private SpectateAction action;
	private Player target;
	
    public PlayerSpectateEvent(Player player, SpectateAction action) {
		super(player);
		this.action = action;
	}
    
	public enum SpectateAction {
		SAIU, ENTROU;
	}
}