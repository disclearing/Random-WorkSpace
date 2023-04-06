package com.br.guilhermematthew.nowly.pvp.ability.register;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

import com.br.guilhermematthew.nowly.pvp.ability.Kit;

public class NoFall extends Kit {

	public NoFall() {
		initialize(getClass().getSimpleName());
	}
	
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL && hasAbility(player)) {
            	event.setCancelled(true);
            }
        }
    }

	@Override
	protected void clean(Player player) {}
}