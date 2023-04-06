package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.commons.bukkit.utility.LocationUtil;
import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;

public class Launcher extends Kit {

	public Launcher() {
		initialize(getClass().getSimpleName());
		
		setItens(new ItemBuilder().material(Material.SPONGE).name(getItemColor() + "Kit" + getName()).amount(20)
				.build());
	}

	private final String NOFALL_TAG = "nofall", NOFALL_TAG_TIME = "nofall.time";

	@EventHandler(priority = EventPriority.MONITOR)
	public void onRealMove(PlayerMoveEvent event) {
		if (!LocationUtil.isRealMovement(event.getFrom(), event.getTo()))
			return;
		
		if (event.isCancelled())
			return;

		Material material = event.getTo().getBlock().getRelative(BlockFace.DOWN).getType();

		if (material.equals(Material.SPONGE)) {
			Player player = event.getPlayer();
			
			Location loc = event.getTo().getBlock().getLocation();

			// DamageListener.addBypassVelocity(player);
			Vector sponge = player.getLocation().getDirection().multiply(0).setY(3);
			player.setVelocity(sponge);

			player.setMetadata(NOFALL_TAG, new FixedMetadataValue(HardcoreGamesMain.getInstance(), true));
			player.setMetadata(NOFALL_TAG_TIME,
					new FixedMetadataValue(HardcoreGamesMain.getInstance(), System.currentTimeMillis()));

			player.playSound(loc, Sound.FIREWORK_LAUNCH, 6.0F, 1.0F);
		}

		material = null;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageEvent event) {
		if (event.getCause() != DamageCause.FALL)
			return;
		if (!(event.getEntity() instanceof Player))
			return;

		Player p = (Player) event.getEntity();

		if (p.hasMetadata(NOFALL_TAG)) {
			p.removeMetadata(NOFALL_TAG, HardcoreGamesMain.getInstance());

			if (!p.hasMetadata(NOFALL_TAG_TIME)) {
				event.setCancelled(true);
				return;
			}
			if (GamerManager.getGamer(p.getUniqueId()).containsKit("Stomper")) {
				p.removeMetadata(NOFALL_TAG_TIME, HardcoreGamesMain.getInstance());
				return;
			}

			Long time = p.getMetadata(NOFALL_TAG_TIME).get(0).asLong();
			if (time + 6200 > System.currentTimeMillis()) {
				event.setCancelled(true);
			}

			p.removeMetadata(NOFALL_TAG_TIME, HardcoreGamesMain.getInstance());
		}
	}

	@Override
	protected void clean(Player player) {
	}
}