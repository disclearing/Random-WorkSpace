package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import java.util.HashMap;
import java.util.Map;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.br.guilhermematthew.nowly.hardcoregames.events.player.PlayerDamagePlayerEvent;

import lombok.Getter;

public class Ninja extends Kit {

	private Map<Player, NinjaHit> ninja;

	public Ninja() {
		initialize(getClass().getSimpleName());
		
		this.ninja = new HashMap<>();
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDamagePlayerEvent(PlayerDamagePlayerEvent event) {
		if (containsHability(event.getPlayer())) {

			NinjaHit ninjaHit = ninja.get(event.getPlayer());
			if (ninjaHit == null) {
				ninjaHit = new NinjaHit(event.getDamaged());
				ninja.put(event.getPlayer(), ninjaHit);
			} else {
				ninjaHit.setTarget(event.getDamaged());
			}
		}
	}

	@EventHandler
	public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
		final Player player = event.getPlayer();

		if (event.isSneaking() && this.ninja.containsKey(player) && containsHability(player)) {

			NinjaHit hit = this.ninja.get(player);
			if (hit == null)
				return;

			if (hit.getTargetExpires() < System.currentTimeMillis())
				return;

			Player target = hit.getTarget();
			if (target.isOnline()) {
				if (player.getLocation().distance(target.getLocation()) > 40) {
					player.sendMessage("§cJogador muito longe!");
					return;
				}
				// Check gladiator

				if (!hasCooldown(player)) {
					player.teleport(target.getLocation());

					addCooldown(player, getCooldownSeconds());

					player.getWorld().playSound(player.getLocation().add(0.0, 0.5, 0.0), Sound.ENDERMAN_TELEPORT, 0.3F,
							1.0F);
				}
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		clean(event.getEntity());

		if (event.getEntity().getKiller() != null) {
			clean(event.getEntity().getKiller());
		}
	}

	protected void clean(Player player) {
		if (this.ninja.containsKey(player)) {
			this.ninja.remove(player);
		}
	}

	@Getter
	private static class NinjaHit {

		private Player target;
		private long targetExpires;

		public NinjaHit(Player target) {
			this.target = target;
			this.targetExpires = System.currentTimeMillis() + 15000;
		}

		public void setTarget(Player player) {
			this.target = player;
			this.targetExpires = System.currentTimeMillis() + 15000;
		}

	}
}