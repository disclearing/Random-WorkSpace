package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;

public class Digger extends Kit {

	public Digger() {
		initialize(getClass().getSimpleName());
		
		setUseInvincibility(true);
		
		setItens(new ItemBuilder().material(Material.DRAGON_EGG).name(getItemColor() + "Kit " + getName()).amount(5)
				.build());
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		ItemStack item = event.getItemInHand();

		if (item != null && item.getTypeId() == Material.DRAGON_EGG.getId() && useAbility(event.getPlayer())) {
			if (hasCooldown(event.getPlayer())) {
				sendMessageCooldown(event.getPlayer());
				return;
			}
			if (event.getBlock().getY() > 110) {
				return;
			}
			addCooldown(event.getPlayer(), getCooldownSeconds());

			final Block b = event.getBlock();
			b.setType(Material.AIR);

			new BukkitRunnable() {
				public void run() {
					int dist = (int) Math.ceil((5 - 1) / 2.0D);

					for (int y = -1; y >= -5; y--) {
						for (int x = -dist; x <= dist; x++) {
							for (int z = -dist; z <= dist; z++) {

								if (b.getY() + y > 0) {
									Block block = b.getWorld().getBlockAt(b.getX() + x, b.getY() + y, b.getZ() + z);
									if (block.getType() != Material.BEDROCK) {
										block.setType(Material.AIR);
									}
									block = null;
								}
							}
						}
					}
				}
			}.runTaskLater(HardcoreGamesMain.getInstance(), 20);
		}
		item = null;
	}

	@Override
	protected void clean(Player player) {
	}
}