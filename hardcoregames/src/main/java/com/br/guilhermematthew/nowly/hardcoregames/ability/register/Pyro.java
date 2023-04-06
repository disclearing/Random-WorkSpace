package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;

public class Pyro extends Kit {

	public Pyro() {
		initialize(getClass().getSimpleName());
		
		setItens(new ItemBuilder().name(getItemColor() + "Kit " + getName()).material(Material.FIREBALL)
				.build(), new ItemStack(Material.FLINT_AND_STEEL));
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		Player player = event.getPlayer();

		if (event.getAction() == Action.RIGHT_CLICK_AIR && item != null && item.getType() == Material.FIREBALL
				&& containsHability(player)) {

			event.setCancelled(true);

			if(!hasCooldown(player)) addCooldown(player, "Pyro", 5);
			else return;

			Fireball fireball = player.launchProjectile(Fireball.class);
			fireball.setIsIncendiary(true);
			fireball.setYield(fireball.getYield() * 1.5F);
		}
	}

	@Override
	protected void clean(Player player) {
	}
}