package com.br.guilhermematthew.nowly.pvp.events;

import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerEvent;
import org.bukkit.entity.Player;

import lombok.Getter;

@Getter
public class GamerDeathEvent extends PlayerEvent {

	private Player killer;
	
	public GamerDeathEvent(Player player, Player killer) {
		super(player);
		this.killer = killer;
	}

	public boolean hasKiller() {
		return killer != null;
	}
}