package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;


public class Demoman extends Kit {

	public Demoman() {
		initialize(getClass().getSimpleName());
		
		setItens(
				new ItemBuilder().material(Material.GRAVEL).amount(8).name(getItemColor() + "Kit " + getName()).build(),
				new ItemBuilder().material(Material.STONE_PLATE).name(getItemColor() + "Kit " + getName()).amount(8)
						.build());
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if (e.isCancelled())
			return;

		if (e.getBlock().getType().name().contains("PLATE") && containsHability(e.getPlayer())) {
			e.getBlock().setMetadata("demoman", new FixedMetadataValue(HardcoreGamesMain.getInstance(), e.getPlayer()));
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.isCancelled())
			return;

		if (event.getAction() == Action.PHYSICAL && event.getClickedBlock() != null
				&& event.getClickedBlock().hasMetadata("demoman")
				&& event.getClickedBlock().getRelative(BlockFace.DOWN).getType() == Material.GRAVEL) {

			event.getClickedBlock().getMetadata("demoman").stream()
					.findFirst()
					.ifPresent(rawMetadata -> {
						val player = (Player) rawMetadata.value();

						if(!player.equals(event.getPlayer())) event.getPlayer().getWorld().createExplosion(event.getClickedBlock().getLocation(), 4.1F);
					});
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getCause().toString().contains("EXPLOSION") && event.getEntity() instanceof Player
				&& containsHability((Player) event.getEntity())) {
			double damage = event.getDamage();
			double porcent = 50.0D;
			if (((Player) event.getEntity()).isBlocking()) {
				porcent = 75.0D;
			}
			event.setDamage(event.getDamage() - ((damage / 100) * porcent));
		}
	}

	@Override
	protected void clean(Player player) {
	}
}