package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.util.Vector;

import com.br.guilhermematthew.nowly.hardcoregames.events.player.PlayerDamagePlayerEvent;

public class Hulk extends Kit {

	public Hulk() {
		initialize(getClass().getSimpleName());
	}

	@EventHandler
	public void onInteract(PlayerInteractEntityEvent e) {
		if (e.getPlayer() instanceof Player && e.getRightClicked() instanceof Player) {
			Player p = (Player) e.getPlayer();

			if (containsHability(p)) {
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

	@EventHandler
	public void onDeath(EntityDeathEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (p.isInsideVehicle()) {
				p.leaveVehicle();
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onDamage(PlayerDamagePlayerEvent event) {
		Player damaged = event.getDamaged(), damager = event.getPlayer();

		if ((damaged.isInsideVehicle()) && (containsHability(damager))) {
			event.setCancelled(true);

			damaged.leaveVehicle();

			damaged.setVelocity(new Vector(damager.getLocation().getDirection().getX() + 0.3, 1.5,
					damager.getLocation().getDirection().getZ() + 0.3));
		}
	}

	@Override
	protected void clean(Player player) {
	}
}