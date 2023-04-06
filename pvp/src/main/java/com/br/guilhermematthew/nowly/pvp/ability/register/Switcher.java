package com.br.guilhermematthew.nowly.pvp.ability.register;

import com.br.guilhermematthew.nowly.pvp.PvPMain;
import com.br.guilhermematthew.nowly.pvp.ability.Kit;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;

public class Switcher extends Kit {

	public Switcher() {
		initialize(getClass().getSimpleName());
		setItens(new ItemBuilder().type(Material.SNOW_BALL).name(getItemColor() + "Kit " + getName()).build());
	}

	@EventHandler
	public void onProjectileLaunch(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (hasAbility(player) && checkItem(player.getItemInHand(), getItemColor() + "Kit " + getName())) {
				event.setCancelled(true);
				player.updateInventory();

				if (hasCooldown(player))
					return;

				val ball = (Snowball) player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.SNOWBALL);
				ball.setShooter(player);
				ball.setMetadata("switch", new FixedMetadataValue(PvPMain.getInstance(), player));
				ball.setVelocity(player.getLocation().getDirection().multiply(1.5));
				
				addCooldown(player, 5);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager().hasMetadata("switch")) {
			Player player = (Player) event.getDamager().getMetadata("switch").get(0).value();

			if (player == null)
				return;

			Location loc = event.getEntity().getLocation().clone();
			event.getEntity().teleport(player.getLocation().clone());
			player.teleport(loc);
		}
	}

	@Override
	protected void clean(Player player) {}
}