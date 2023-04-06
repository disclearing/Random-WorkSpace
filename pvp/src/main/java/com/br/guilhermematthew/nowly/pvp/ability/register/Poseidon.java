package com.br.guilhermematthew.nowly.pvp.ability.register;

import com.br.guilhermematthew.nowly.pvp.ability.Kit;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Poseidon extends Kit {

	public Poseidon() {
		initialize(getClass().getSimpleName());
	}

	@EventHandler
	public void onRealMove(PlayerMoveEvent event) {
		//if (!event.isRealMovement()) return;
		if (event.isCancelled()) return;

		val player = event.getPlayer();
		if(!hasAbility(player)) return;

		val blockType = event.getTo().getBlock().getType();
		if (blockType.equals(Material.WATER) || blockType.equals(Material.STATIONARY_WATER)) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0));
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0));
		}
	}

	@Override
	protected void clean(Player player) {}
}