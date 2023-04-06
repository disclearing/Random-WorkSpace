package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.api.player.PlayerAPI;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Specialist extends Kit {

	private final ItemStack item;

	public Specialist() {
		initialize(getClass().getSimpleName());

		item = new ItemBuilder().material(Material.ENCHANTMENT_TABLE).name(getItemColor() + "Kit " + getName())
				.build();

		setItens(item);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent event) {
		final Player morreu = event.getEntity(), killer = morreu.getKiller();

		if (killer == null) {
			return;
		}

		if (containsHability(killer)) killer.setLevel(killer.getLevel() + 1);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onInteract(PlayerInteractEvent e) {
		if(e.getAction() == Action.PHYSICAL) return;

		val player = e.getPlayer();

		if(!containsHability(player)) return;
		if(!item.equals(e.getItem())) return;

		e.setCancelled(true);
		player.openEnchanting(null, true);
	}

	/*@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		if (event.getBlock().getType() == Material.ENCHANTMENT_TABLE) {
			if (containsHability(event.getPlayer())) {
				
				if (!PlayerAPI.isFull(event.getPlayer().getInventory())) {
					event.getBlock().setType(Material.AIR);
					event.getPlayer().getInventory().addItem(new ItemBuilder().material(Material.ENCHANTMENT_TABLE).name(getItemColor() + "Kit " + getName()).build());
				}
				
			}
		}
	}*/

	@Override
	protected void clean(Player player) {
		// TODO Auto-generated method stub

	}
}