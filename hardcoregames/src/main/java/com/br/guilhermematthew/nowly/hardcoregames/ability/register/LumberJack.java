package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;

public class LumberJack extends Kit {

	public LumberJack() {
		initialize(getClass().getSimpleName());
		
		setItens(new ItemBuilder().material(Material.WOOD_AXE).name(getItemColor() + "Kit " + getName()).build());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent event) {
		if (event.isCancelled() || HardcoreGamesMain.getGameManager().isInvencibilidade())
			return;

		if ((event.getPlayer().getItemInHand().getType().equals(Material.WOOD_AXE))
				&& (containsHability(event.getPlayer()))) {

			Block b = event.getBlock().getRelative(BlockFace.UP), b1 = event.getBlock().getRelative(BlockFace.DOWN);

			while (b.getType().name().contains("LOG")) {
				b.breakNaturally();
				b = b.getRelative(BlockFace.UP);
			}
			while (b1.getType().name().contains("LOG")) {
				b1.breakNaturally();
				b1 = b1.getRelative(BlockFace.DOWN);
			}
		}
	}

	@Override
	protected void clean(Player player) {
	}
}