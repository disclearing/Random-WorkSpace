package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Worm extends Kit {

	public Worm() {
		initialize(getClass().getSimpleName());
	}

	@EventHandler
	public void entityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if ((e.getCause().equals(DamageCause.FALL)) && (containsHability(p))) {
				if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.DIRT)) {
					if (e.getDamage() >= 8.0D) {
						e.setDamage(8D);
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockDamage(BlockDamageEvent e) {
		if ((e.getBlock().getType().equals(Material.DIRT)) && (containsHability(e.getPlayer()))) {
			e.setInstaBreak(true);

			if (e.getPlayer().getHealth() < 20) {
				double value = e.getPlayer().getHealth() + 2.0;
				if (value > 20)
					value = 20.0;
				e.getPlayer().setHealth(value);
			}

			if (e.getPlayer().getFoodLevel() < 20) {
				int value = e.getPlayer().getFoodLevel() + 1;
				if (value > 20)
					value = 20;

				e.getPlayer().setFoodLevel(value);
			}

			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
		}
	}

	@Override
	protected void clean(Player player) {
	}
}
