package com.br.guilhermematthew.nowly.hardcoregames.events.player;

import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerEvent;
import org.bukkit.entity.Player;

import lombok.Getter;

public class PlayerRegisterKitEvent extends PlayerEvent {

	@Getter
	private String kitName;
	
	public PlayerRegisterKitEvent(final Player player, final String kitName) {
		super(player);
		this.kitName = kitName;
	}
}