package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.br.guilhermematthew.nowly.commons.bukkit.api.player.PlayerAPI;

public class Ironman extends Kit {

	public Ironman() {
		initialize(getClass().getSimpleName());
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
			if (Math.random() <= 0.45) {

				if (PlayerAPI.isFull(killer.getInventory())) {
					killer.getWorld().dropItem(killer.getLocation(), new ItemStack(Material.IRON_INGOT));
				} else {
					killer.getInventory().addItem(new ItemStack(Material.IRON_INGOT));
				}
			}
		}
	}

	@Override
	protected void clean(Player player) {
		// TODO Auto-generated method stub

	}
}