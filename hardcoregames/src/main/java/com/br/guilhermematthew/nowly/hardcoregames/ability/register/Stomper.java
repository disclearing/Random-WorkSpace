package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import java.util.List;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class Stomper extends Kit {

	private final double STOMPER_FALL_NERF = 3.5D;

	public Stomper() {
		initialize(getClass().getSimpleName());
	}

	@EventHandler
	public void onFall(EntityDamageEvent event) {
		if (event.isCancelled())
			return;

		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();

			if ((event.getCause() == EntityDamageEvent.DamageCause.FALL) && (containsHability(p))) {

				if (event.getDamage() <= 4.0) {
					return;
				}

				List<Entity> entity = p.getNearbyEntities(6.0D, 3.0D, 6.0D);
				boolean stomped = false;

				for (Entity en : entity) {
					if (en instanceof Player) {
						Player stompados = (Player) en;

						if ((!GamerManager.getGamer(stompados.getUniqueId()).isPlaying())
								|| (!stompados.getGameMode().equals(GameMode.SURVIVAL))) {
							continue;
						}

						stomped = true;

						double damage = 4.0D, life = stompados.getHealth();

						if (!stompados.isSneaking()) {
							damage = p.getFallDistance() - STOMPER_FALL_NERF;
						}

						stompados.playSound(stompados.getLocation(), Sound.ANVIL_BREAK, 1.0F, 1.0F);

						if (life - damage <= 0.0D) {
							stompados.setHealth(1.0D);
							stompados.damage(4.0, p);
						} else {
							stompados.setHealth(life - damage);
						}
					}
				}

				if (stomped) {
					p.playSound(p.getLocation(), Sound.ANVIL_BREAK, 1.0F, 1.0F);
				}

				if (event.getDamage() > 4.0D) {
					event.setDamage(4.0D);
				}
			}
		}
	}

	@Override
	protected void clean(Player player) {
	}
}