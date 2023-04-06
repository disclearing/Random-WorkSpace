package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.br.guilhermematthew.nowly.hardcoregames.events.player.PlayerDamagePlayerEvent;

public class Snail extends Kit {

	public Snail() {
		initialize(getClass().getSimpleName());
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDamage(PlayerDamagePlayerEvent event) {
		if (useAbility(event.getPlayer()) && Math.random() <= 0.33) {

			event.getDamaged().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 1));
			event.getDamaged().getLocation().getWorld().playEffect(
					event.getDamaged().getLocation().add(0.0D, 0.4D, 0.0D), Effect.STEP_SOUND, 159, (short) 13);
		}
	}

	@Override
	protected void clean(Player player) {
	}
}