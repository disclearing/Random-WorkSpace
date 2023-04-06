package com.br.guilhermematthew.nowly.pvp.ability.register;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.pvp.ability.Kit;

public class Monk extends Kit {

	public Monk() {
		initialize(getClass().getSimpleName());
		
		setItens(new ItemBuilder().type(Material.BLAZE_ROD).name(
				getItemColor() + "Kit " + getName()).build());
	}
	
	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof Player) {
			Player p = e.getPlayer();
			
			if ((p.getItemInHand().getType().equals(Material.BLAZE_ROD)) && 
					(hasAbility(p))) {
				
				 if (hasCooldown(p)) {
					 sendMessageCooldown(p);
					 return;
				 }
				
				 Player d = (Player) e.getRightClicked();
				 final ItemStack item = d.getItemInHand();
				 final int r = new Random().nextInt(35);
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
	protected void clean(Player player) {}
}