package com.br.guilhermematthew.nowly.lobby.common.inventory.types;

import org.bukkit.Material;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.MenuInventory;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import com.br.guilhermematthew.nowly.lobby.common.inventory.InventoryCommon;

public class GamesInventory extends MenuInventory {

	public GamesInventory() {
		super("Selecione um modo", 3);
		
		update();
	}
	
	public void update() {
		ItemBuilder itemBuilder = new ItemBuilder();
		
		ServerType serverType = BukkitMain.getServerType();

		setItem(10, itemBuilder.type(Material.IRON_CHESTPLATE).name("§aPvP").lore(
				"§7" + CommonsGeneral.getServersManager().getAmountOnlinePvP(serverType != ServerType.LOBBY_PVP) + " §7jogando agora!").build(), InventoryCommon.getDefaultClickHandler());
	
		setItem(11, itemBuilder.type(Material.MUSHROOM_SOUP).name("§aHG").lore(
				"§7" + CommonsGeneral.getServersManager().getAmountOnlineHardcoreGames(
						serverType != ServerType.LOBBY_HARDCOREGAMES) + " §7jogando agora!").build(), InventoryCommon.getDefaultClickHandler());

		setItem(12, itemBuilder.type(Material.DIAMOND_SWORD).name("§aDuels (1v1/Gladiator)").lore(
				"§7" + CommonsGeneral.getServersManager().getAmountOnlineDuels(serverType != ServerType.LOBBY_DUELS) + " §7jogando agora!").build(), InventoryCommon.getDefaultClickHandler());

		setItem(13, itemBuilder.type(Material.CAKE).name("§aParty Games").lore(
				"§70 §7jogando agora!").build(), InventoryCommon.getDefaultClickHandler());
	}
}