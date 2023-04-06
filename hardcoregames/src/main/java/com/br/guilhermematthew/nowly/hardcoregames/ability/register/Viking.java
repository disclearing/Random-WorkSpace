package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.meta.ItemMeta;

import com.br.guilhermematthew.nowly.hardcoregames.events.player.PlayerDamagePlayerEvent;

public class Viking extends Kit {

	public Viking() {
		initialize(getClass().getSimpleName());
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDamage(PlayerDamagePlayerEvent event) {
		Player player = event.getPlayer();
		if (containsHability(player)) {
			if (player.getItemInHand() != null && player.getItemInHand().getType().name().contains("AXE")) {
				event.setDamage(event.getDamage() + 2.0D);
				player.getItemInHand().setDurability((short) 0);
				ItemMeta meta = player.getItemInHand().getItemMeta();
				if (!meta.spigot().isUnbreakable()) {
					meta.spigot().setUnbreakable(true);
					player.getItemInHand().setItemMeta(meta);
					player.updateInventory();
				}
			}
		}
	}

	@Override
	protected void clean(Player player) {
	}
}