package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.br.guilhermematthew.nowly.hardcoregames.events.player.PlayerDamagePlayerEvent;

public class Boxer extends Kit {

	public Boxer() {
		initialize(getClass().getSimpleName());
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDamage(PlayerDamagePlayerEvent event) {
		if (containsHability(event.getDamaged())) {
			event.setDamage(event.getDamage() - 1.0D);
		}
	}

	@Override
	protected void clean(Player player) {
	}
}