package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.commons.bukkit.utility.LocationUtil;
import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Poseidon extends Kit {

	public Poseidon() {
		initialize(getClass().getSimpleName());
	}

	@EventHandler
	public void onRealMove(PlayerMoveEvent event) {
		if (!LocationUtil.isRealMovement(event.getFrom(), event.getTo()))
			return;
		
		if (event.isCancelled())
			return;

		if (event.getTo().getBlock().getType().equals(Material.WATER)
				|| event.getTo().getBlock().getType().equals(Material.STATIONARY_WATER)) {

			if (containsHability(event.getPlayer())) {
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0), true);
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0), true);
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0), true);
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player && containsHability((Player) e.getEntity())
				&& e.getCause() == DamageCause.DROWNING) {
			e.setCancelled(true);
		}
	}

	@Override
	protected void clean(Player player) {
	}
}