package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;

public class Phantom extends Kit {

	public Phantom() {
		initialize(getClass().getSimpleName());
		setUseInvincibility(true);
		
		setItens(new ItemBuilder().material(Material.FEATHER).name(getItemColor() + "Kit " + getName()).build());
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if ((player.getItemInHand().getType().equals(Material.FEATHER)) && (useAbility(player))) {
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (hasCooldown(player)) {
					sendMessageCooldown(player);
					return;
				}
				addCooldown(player, getCooldownSeconds());
				player.setAllowFlight(true);
				player.setFlying(true);
				player.getWorld().playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 2.0F, 1.0F);

				for (Entity c : player.getNearbyEntities(30, 30, 30)) {
					if (c instanceof Player && c != player) {
						Player d = (Player) c;
						d.sendMessage("§4Há um phantom por perto!");
					}
				}
				
				HardcoreGamesMain.runLater(() -> {
					if (!player.isOnline()) {
						return;
					}
					player.setFallDistance(-10);
					player.playSound(player.getLocation(), Sound.AMBIENCE_RAIN, 3.0F, 4.0F);
					player.setAllowFlight(false);
					player.setFlying(false);
				}, 142L);
			}
		}
	}

	@Override
	protected void clean(Player player) {
	}
}