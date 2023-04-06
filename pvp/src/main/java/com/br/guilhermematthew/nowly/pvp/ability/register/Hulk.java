package com.br.guilhermematthew.nowly.pvp.ability.register;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.util.Vector;

import com.br.guilhermematthew.nowly.pvp.PvPMain;
import com.br.guilhermematthew.nowly.pvp.ability.Kit;
import com.br.guilhermematthew.nowly.pvp.events.PlayerDamagePlayerEvent;

public class Hulk extends Kit {

	public Hulk() {
		initialize(getClass().getSimpleName());
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEntityEvent e) {
		if (e.getPlayer() != null && e.getRightClicked() instanceof Player) {
			Player p = e.getPlayer();
			
			if ((p.getInventory().getItemInHand().getType().equals(Material.AIR)) || 
			     (p.getInventory().getItemInHand().getType() == Material.STONE_SWORD)) {
				
				if (hasAbility(p)) {
					if (hasCooldown(p)) {
						sendMessageCooldown(p);
						return;
					}
					addCooldown(p, getCooldownSeconds());
					
					Player d = (Player) e.getRightClicked();
					p.setPassenger(d);
				}
			}
		}
	}

	@EventHandler
	public void onDeath(EntityDeathEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			
			if (p.isInsideVehicle()) p.leaveVehicle();
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onDamage(PlayerDamagePlayerEvent event) {
		Player damaged = event.getDamaged(),
				damager = event.getPlayer();
		
		if ((damaged.isInsideVehicle()) && (hasAbility(damager))) {
			 event.setCancelled(true);
			 
			 damaged.leaveVehicle();

			 PvPMain.runLater(() -> {
				 damaged.setVelocity(new Vector(damager.getLocation().getDirection().getX() + 0.3, 1.5, damager.getLocation().getDirection().getZ() + 0.3));
			 }, 2);
		}
	}

	@Override
	protected void clean(Player player) {}
}