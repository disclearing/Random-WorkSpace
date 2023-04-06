package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

public class Fireman extends Kit {

	public Fireman() {
		initialize(getClass().getSimpleName());
		
		setItens(new ItemStack(Material.WATER_BUCKET));
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
				}
			}
		}
	}

	@Override
	protected void clean(Player player) {
	}
}
