package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.CocoaPlant;

public class Cultivator extends Kit {

	public Cultivator() {
		initialize(getClass().getSimpleName());
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent e) {
		if (e.getBlock().getType() == Material.SAPLING && containsHability(e.getPlayer())) {
			e.getBlock().setType(Material.AIR);
			e.getBlock().getWorld().generateTree(e.getBlock().getLocation(), TreeType.TREE);
		} else if (e.getBlock().getType() == Material.CROPS && containsHability(e.getPlayer())) {
			e.getBlock().setData((byte) 7);
		} else if (e.getBlock().getType() == Material.COCOA && containsHability(e.getPlayer())) {
			CocoaPlant bean = (CocoaPlant) e.getBlock().getState().getData();
			if (bean.getSize() != CocoaPlant.CocoaPlantSize.LARGE) {
				bean.setSize(CocoaPlant.CocoaPlantSize.LARGE);
				e.getBlock().setData(bean.getData());
			}
			bean = null;
		}
	}

	@Override
	protected void clean(Player player) {
	}
}
