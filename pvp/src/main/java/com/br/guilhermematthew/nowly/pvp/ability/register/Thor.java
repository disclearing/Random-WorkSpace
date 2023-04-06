package com.br.guilhermematthew.nowly.pvp.ability.register;

import java.util.HashSet;

import com.br.guilhermematthew.nowly.pvp.ability.Kit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;

public class Thor extends Kit {

	public Thor() {
		initialize(getClass().getSimpleName());
		setItens(new ItemBuilder().type(Material.WOOD_AXE).name(
				getItemColor() + "Kit " + getName()).build());
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getAction() == Action.PHYSICAL) return;

		if ((e.getPlayer().getItemInHand().getType().equals(Material.WOOD_AXE)) && (e.getAction().name().contains("RIGHT")) && 
				(hasAbility(e.getPlayer()))) {
			
			if (hasCooldown(e.getPlayer())) {
				sendMessageCooldown(e.getPlayer());
				return;
			}
			
			Location loc = e.getPlayer().getTargetBlock((HashSet<Byte>) null, 25).getLocation();
			if (!loc.getBlock().getType().equals(Material.AIR)) {
				addCooldown(e.getPlayer(), getCooldownSeconds());
				
				LightningStrike strike = loc.getWorld().strikeLightning(loc);
				
				for (Entity nearby : strike.getNearbyEntities(4.0D, 4.0D, 4.0D)) {
					 if (nearby instanceof Player) {
						 nearby.setFireTicks(100);
					 }
				}

				e.getPlayer().setFireTicks(0);
			}
		}
	}

	@Override
	protected void clean(Player player) {}
}