package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import com.br.guilhermematthew.nowly.hardcoregames.events.player.PlayerDamagePlayerEvent;

public class Achilles extends Kit {

	public Achilles() {
		initialize(getClass().getSimpleName());
	}

	@EventHandler
	public void onPlayerDamage(PlayerDamagePlayerEvent event) {
		if (event.isCancelled())
			return;

		if (useAbility(event.getDamaged())) {
			ItemStack inHand = event.getPlayer().getItemInHand();

			if (inHand == null || inHand.getType() == Material.AIR)
				return;

			if (inHand.getType().name().contains("WOOD") || inHand.getType() == Material.STICK) {
				event.setDamage(Math.max(7, event.getDamage() + 4));
			} else {
				event.setDamage(2);
				event.getPlayer().sendMessage("§c" + event.getDamaged().getName()
						+ " está usando o kit Achilles, itens de madeira causam mais dano.");
			}
			inHand = null;
		}
	}

	@Override
	protected void clean(Player player) {
	}
}