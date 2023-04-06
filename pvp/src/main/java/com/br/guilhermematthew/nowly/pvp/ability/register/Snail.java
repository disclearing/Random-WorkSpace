package com.br.guilhermematthew.nowly.pvp.ability.register;

import com.br.guilhermematthew.nowly.pvp.ability.Kit;
import com.br.guilhermematthew.nowly.pvp.events.PlayerDamagePlayerEvent;
import com.br.guilhermematthew.nowly.pvp.listeners.CombatLogListener;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Snail extends Kit {

	public Snail() {
		initialize(getClass().getSimpleName());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void playerDamage(PlayerDamagePlayerEvent event) {
		if (hasAbility(event.getPlayer()) && Math.random() <= 0.33 &&
				!CombatLogListener.isProtected(event.getPlayer()) && !CombatLogListener.isProtected(event.getDamaged())) {
						
            event.getDamaged().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 1));
            event.getDamaged().getLocation().getWorld().playEffect(event.getDamaged().getLocation()
                    .add(0.0D, 0.4D, 0.0D), Effect.STEP_SOUND, 159, (short) 13);
		}
	}

	@Override
	protected void clean(Player player) {}
}