package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.commons.bukkit.utility.LocationUtil;
import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.br.guilhermematthew.nowly.hardcoregames.events.player.PlayerDamagePlayerEvent;

public class Magma extends Kit {

	public Magma() {
		initialize(getClass().getSimpleName());
	}

	@EventHandler
	public void onRealMove(PlayerMoveEvent event) {
		if (!LocationUtil.isRealMovement(event.getFrom(), event.getTo())) return;
		
		if (event.isCancelled())
			return;

		if (event.getTo().getBlock().getType().equals(Material.WATER)
				|| event.getTo().getBlock().getType().equals(Material.STATIONARY_WATER)) {

			if (containsHability(event.getPlayer())) {
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0), true);
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0), true);
				event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 0), true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDamage(PlayerDamagePlayerEvent event) {
		if (useAbility(event.getPlayer()) && Math.random() <= 0.35) {
			event.getDamaged().setFireTicks(140);
			event.getDamaged().getLocation().getWorld().playEffect(
					event.getDamaged().getLocation().add(0.0D, 0.4D, 0.0D), Effect.STEP_SOUND, 159, (short) 13);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.isCancelled())
			return;

		if (event.getEntity() instanceof Player) {
			if (event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK
					|| event.getCause() == DamageCause.LAVA) {
				if (containsHability((Player) event.getEntity())) {
					event.setCancelled(true);
					event.getEntity().setFireTicks(0);

					((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0),
							true);
					((Player) event.getEntity())
							.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0), true);
				}
			}
		}
	}

	@Override
	protected void clean(Player player) {
	}
}