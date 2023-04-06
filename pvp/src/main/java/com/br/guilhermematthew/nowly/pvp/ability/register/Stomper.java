package com.br.guilhermematthew.nowly.pvp.ability.register;

import com.br.guilhermematthew.nowly.pvp.manager.combatlog.CombatLogManager;
import com.br.guilhermematthew.nowly.pvp.ability.Kit;
import com.br.guilhermematthew.nowly.pvp.manager.gamer.GamerManager;
import lombok.val;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class Stomper extends Kit {

	public Stomper() {
		initialize(getClass().getSimpleName());
	}
	
	@EventHandler
	public void onFall(EntityDamageEvent event) {
		if (event.isCancelled()) return;
		
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			
			if ((event.getCause() == EntityDamageEvent.DamageCause.FALL) && (hasAbility(p))) {
				
				 if (event.getDamage() <= 4.0) {
					 return;
				 }

				 p.getNearbyEntities(6.0D, 3.0D, 6.0D)
						 .stream()
						 .filter(entity -> (entity instanceof Player) && !GamerManager.getGamer(entity.getUniqueId()).containsKit("AntiTower") && !((Player) entity).isSneaking() && ((Player) entity).getGameMode() != GameMode.CREATIVE)
						 .forEach(entity -> {
							 val player = (Player) entity;

							 player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1.0F, 1.0F);

							 CombatLogManager.removeCombatLog(player);
							 CombatLogManager.removeCombatLog(p);

							 CombatLogManager.setCombatLog(player, p);
							 CombatLogManager.setCombatLog(p, player);

							 // val health = Math.max(0, player.getHealth() - event.getFinalDamage());
							 player.damage(event.getFinalDamage());
							 // player.setHealth(health);
						 });

				 if (event.getDamage() > 4.0D) event.setDamage(4.0D);
			}
		}
	}

	@Override
	protected void clean(Player player) {}
}