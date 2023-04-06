package com.br.guilhermematthew.nowly.pvp.ability.register;

import com.br.guilhermematthew.nowly.commons.bukkit.utility.LocationUtil;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.br.guilhermematthew.nowly.pvp.ability.Kit;

public class Camel extends Kit {

	public Camel() {
		initialize(getClass().getSimpleName());
	}

	@EventHandler
	public void onRealMove(PlayerMoveEvent event) {
		if (!LocationUtil.isRealMovement(event.getFrom(), event.getTo())) return;
		if (event.isCancelled()) return;

		val player = event.getPlayer();
		if(!hasAbility(player)) return;

		val blockType = event.getTo().getBlock().getRelative(BlockFace.DOWN).getType();
		if (blockType.equals(Material.SAND) || blockType.equals(Material.SOUL_SAND)) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 150, 0));
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 150, 0));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1));
		}
	}

	@Override
	protected void clean(Player player) {}
}