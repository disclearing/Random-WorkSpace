package com.br.guilhermematthew.nowly.pvp.ability.register;

import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.pvp.PvPMain;
import com.br.guilhermematthew.nowly.pvp.ability.Kit;

public class Jumper extends Kit {

	public Jumper() {
		initialize(getClass().getSimpleName());
		
		setItens(new ItemBuilder().type(Material.EYE_OF_ENDER).name(getItemColor() + "Kit " + getName()).build());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onJumperListener(PlayerInteractEvent event) {
		if(event.getAction() == Action.PHYSICAL) return;

		Player p = event.getPlayer();
		
		if (hasAbility(p)) {
			if (checkItem(p.getItemInHand(), getItemColor() + "Kit " + getName())) {
				event.setCancelled(true);
				p.updateInventory();
				
				if (!hasCooldown(p)) {
					addCooldown(p, getCooldownSeconds());
					p.setFallDistance(0);
					EnderPearl ender = p.launchProjectile(EnderPearl.class);
					ender.setPassenger(p);
					ender.setMetadata("Jumper", new FixedMetadataValue(PvPMain.getInstance(), p.getUniqueId()));
					ender.setShooter(null);
				} else {
					sendMessageCooldown(p);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJumperHit(ProjectileHitEvent event) {
		if (!event.getEntity().hasMetadata("Jumper"))
			return;
		if (event.getEntity().getPassenger() != null) {
			event.getEntity().eject();
		}
	}

	@Override
	protected void clean(Player player) {}
}