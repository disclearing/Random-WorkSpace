package com.br.guilhermematthew.nowly.lobby.common.inventory.types;

import org.bukkit.Material;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.MenuInventory;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.MenuItem;
import com.br.guilhermematthew.nowly.lobby.common.inventory.InventoryCommon;

public class PvPInventory extends MenuInventory {

	public PvPInventory() {
		super("Escolha um modo", 3);
		
		update();
	}
	
	public void update() {
		ItemBuilder itemBuilder = new ItemBuilder();
		
		setItem(10, new MenuItem(itemBuilder.type(Material.DIAMOND_SWORD).name("§aArena").lore("§e" + CommonsGeneral.getServersManager().getNetworkServer("arena").getOnlines() + " jogando").build(), InventoryCommon.getDefaultClickHandler()));
		setItem(11, new MenuItem(itemBuilder.type(Material.GLASS).name("§aFPS").lore("§e" + CommonsGeneral.getServersManager().getNetworkServer("fps").getOnlines() + " jogando").build(), InventoryCommon.getDefaultClickHandler()));
		setItem(12, new MenuItem(itemBuilder.type(Material.LAVA_BUCKET).name("§aLavaChallenge").lore("§e" + CommonsGeneral.getServersManager().getNetworkServer("lavachallenge").getOnlines() + " jogando").build(), InventoryCommon.getDefaultClickHandler()));
		
		itemBuilder = null;
	}
}