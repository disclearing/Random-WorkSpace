package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;

public class Thor extends Kit {

	public Thor() {
		initialize(getClass().getSimpleName());

		setItens(new ItemBuilder().material(Material.WOOD_AXE).name(getItemColor() + "Kit " + getName()).build());
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if ((event.getPlayer().getItemInHand().getType().equals(Material.WOOD_AXE))
				&& (event.getAction() == Action.RIGHT_CLICK_BLOCK) && (containsHability(event.getPlayer()))) {

			if (hasCooldown(event.getPlayer())) {
				sendMessageCooldown(event.getPlayer());
				return;
			}
			addCooldown(event.getPlayer(), getCooldownSeconds());

			Location loc = new Location(
					event.getPlayer().getWorld(), event.getClickedBlock().getX(), (event.getPlayer().getWorld()
							.getHighestBlockYAt(event.getClickedBlock().getX(), event.getClickedBlock().getZ())),
					event.getClickedBlock().getZ());

			loc = loc.subtract(0, 1, 0);
			if (loc.getBlock().getType() == Material.AIR) {
				loc = loc.subtract(0, 1, 0);
			}

			LightningStrike strike = loc.getWorld().strikeLightning(loc);

			for (Entity nearby : strike.getNearbyEntities(4.0D, 4.0D, 4.0D)) {
				if ((nearby instanceof Player)) {
					nearby.setFireTicks(100);
				}
				event.getPlayer().setFireTicks(0);
			}

			strike = null;

			loc.getBlock().getRelative(BlockFace.UP).setType(Material.FIRE);
			loc = null;
		}
	}

	@EventHandler
	public void damage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (event.getCause() == DamageCause.LIGHTNING) {
				if (containsHability(player)) {
					player.setFireTicks(0);
					event.setCancelled(true);
				}
			}
		}
	}

	@Override
	protected void clean(Player player) {
	}
}