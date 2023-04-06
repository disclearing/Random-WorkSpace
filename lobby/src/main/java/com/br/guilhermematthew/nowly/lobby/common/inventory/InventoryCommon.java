package com.br.guilhermematthew.nowly.lobby.common.inventory;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.ClickType;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.MenuClickHandler;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import com.br.guilhermematthew.nowly.lobby.common.LobbyUtility;
import com.br.guilhermematthew.nowly.lobby.common.inventory.types.GamesInventory;
import com.br.guilhermematthew.nowly.lobby.common.inventory.types.MixInventory;
import com.br.guilhermematthew.nowly.lobby.common.inventory.types.PvPInventory;

public class InventoryCommon {

	private static GamesInventory gamesInventory = null;
	private static MixInventory mixInventory = null;
	private static PvPInventory pvpInventory = null;

	private static final MenuClickHandler fastHGClickHandler = new MenuClickHandler() {

		@Override
		public void onClick(Player player, Inventory inv, ClickType type, ItemStack stack, int slot) {
			player.closeInventory();

			String itemName = ChatColor.stripColor(stack.getItemMeta().getDisplayName().trim());

			ServerType serverClicked = ServerType.getServer(itemName);

			if (BukkitMain.getServerType() == ServerType.LOBBY_HARDCOREGAMES) {
				if (itemName.equalsIgnoreCase("Partida")) {
					serverClicked = ServerType.HARDCORE_GAMES;
				}
			}

				LobbyUtility.handleInteract(player, serverClicked, BukkitMain.getServerType());
			}
		};

	private static final MenuClickHandler defaultClickHandler = new MenuClickHandler() {

		@Override
		public void onClick(Player player, Inventory inv, ClickType type, ItemStack stack, int slot) {
			player.closeInventory();

			String itemName = ChatColor.stripColor(stack.getItemMeta().getDisplayName().trim());

			ServerType serverClicked = ServerType.getServer(itemName);

			if (BukkitMain.getServerType() == ServerType.LOBBY) {
				if (itemName.equalsIgnoreCase("PvP")) {
					serverClicked = ServerType.LOBBY_PVP;
				} else if (itemName.equalsIgnoreCase("HG")) {
					serverClicked = ServerType.LOBBY_HARDCOREGAMES;
				}
			}

			if (BukkitMain.getServerType() == ServerType.LOBBY_PVP) {
				if (itemName.equalsIgnoreCase("PvP")) getPvPInventory().open(player);
				 else if (itemName.equalsIgnoreCase("HG")) {
					serverClicked = ServerType.LOBBY_HARDCOREGAMES;
				}
			}

			if(BukkitMain.getServerType() == ServerType.LOBBY_HARDCOREGAMES) {
				if (itemName.equalsIgnoreCase("PvP")) serverClicked = ServerType.LOBBY_PVP;
			}

			LobbyUtility.handleInteract(player, serverClicked, BukkitMain.getServerType());
		}
	};
	
	public static void update() {
		if (BukkitMain.getServerType() == ServerType.LOBBY_HARDCOREGAMES) {
			getHardcoreGamesInventory().update();
		} else if (BukkitMain.getServerType() == ServerType.LOBBY_PVP) {
			getGamesInventory().update();
			getPvPInventory().update();
		} else if (BukkitMain.getServerType() == ServerType.LOBBY_DUELS) {
			getGamesInventory().update();
		} else {
			getGamesInventory().update();
		}
	}

	public static MenuClickHandler getDefaultClickHandler() {
		return defaultClickHandler;
	}

	public static MenuClickHandler getFastHGClickHandler() {
		return fastHGClickHandler;
	}

	public static GamesInventory getGamesInventory() {
		if (gamesInventory == null) {
			gamesInventory = new GamesInventory();
		}
		return gamesInventory;
	}

	public static MixInventory getHardcoreGamesInventory() {
		if (mixInventory == null) {
			mixInventory = new MixInventory();
		}
		return mixInventory;
	}

	public static PvPInventory getPvPInventory() {
		if (pvpInventory == null) {
			pvpInventory = new PvPInventory();
		}
		return pvpInventory;
	}
}