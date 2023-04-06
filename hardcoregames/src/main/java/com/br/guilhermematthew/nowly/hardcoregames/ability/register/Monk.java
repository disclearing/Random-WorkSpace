package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;

public class Monk extends Kit {

	public Monk() {
		initialize(getClass().getSimpleName());
		
		setItens(new ItemBuilder().material(Material.BLAZE_ROD).name(getItemColor() + "Kit " + getName()).build());
	}

	@EventHandler
	public void onInteract(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof Player) {
			Player p = e.getPlayer();
			if ((p.getItemInHand().getType().equals(Material.BLAZE_ROD)) && (useAbility(p))) {
				if (hasCooldown(p)) {
					sendMessageCooldown(p);
					return;
				}
				Player d = (Player) e.getRightClicked();
				final ItemStack item = d.getItemInHand();
				final int r = CommonsConst.RANDOM.nextInt(35);
				final ItemStack i = d.getInventory().getItem(r);
				d.getInventory().setItem(r, item);
				d.setItemInHand(i);
				addCooldown(p, getCooldownSeconds());
				p.sendMessage("§aMonkado!");
				d.sendMessage("§aVocê foi monkado!");
			}
		}
	}

	@Override
	protected void clean(Player player) {
	}
}