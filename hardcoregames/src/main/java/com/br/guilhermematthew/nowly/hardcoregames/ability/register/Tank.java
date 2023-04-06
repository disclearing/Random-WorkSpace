package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Tank extends Kit {

	public Tank() {
		initialize(getClass().getSimpleName());
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if ((event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
					|| (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {

				if (containsHability(p)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent event) {
		final Player morreu = event.getEntity(), killer = morreu.getKiller();

		if (killer == null) {
			return;
		}

		if (!(killer instanceof Player)) {
			return;
		}

		if (containsHability(killer)) {
			World world = morreu.getWorld();
			world.createExplosion(morreu.getLocation(), 2.0F);
		}
	}

	@Override
	protected void clean(Player player) {
	}
}