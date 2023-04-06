package com.br.guilhermematthew.nowly.lobby.common.inventory.types;

import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import com.br.guilhermematthew.nowly.lobby.common.inventory.InventoryCommon;
import org.bukkit.Material;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.MenuClickHandler;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.MenuInventory;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.MenuItem;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.enums.GameStages;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.types.GameServer;
import com.br.guilhermematthew.nowly.commons.common.utility.system.DateUtils;
import org.bukkit.entity.Item;

public class MixInventory extends MenuInventory {

	private final ItemBuilder itemBuilder;
	
	public MixInventory() {
		super("Salas de HG", 4);
		
		this.itemBuilder = new ItemBuilder();
		
		update();
		onFastRoom();
	}

	public void onFastRoom() {

		ItemBuilder itemBuilder = new ItemBuilder();

		setItem(31, itemBuilder.type(Material.SLIME_BALL).name("§bPartida Rápida").lore(
				"§7Clique para entrar em uma partida", "§7disponivel.").build(), InventoryCommon.getFastHGClickHandler());

	}

	public void update() {
		int slot = 10;
		
		for (GameServer hardcoreGamesServer : CommonsGeneral.getServersManager().getHardcoreGamesServers()) {
			GameStages stage = hardcoreGamesServer.getStage();

			itemBuilder.name((stage == GameStages.WAITING ? "§a" : "§c") + "HG #" + hardcoreGamesServer.getServerId()); 
			
			if (stage == GameStages.WAITING) {
				itemBuilder.type(Material.STAINED_GLASS_PANE).durability(5);
				itemBuilder.lore("§aTorneio iniciando em: " + DateUtils.formatTime(hardcoreGamesServer.getTempo()), 
						"§e" + hardcoreGamesServer.getPlayersGaming() + " aguardando.");
				setItem(slot, new MenuItem(itemBuilder.build(), defaultMenuClickHandler));
			} else if (stage == GameStages.INVINCIBILITY) {
				itemBuilder.type(Material.STAINED_GLASS_PANE).durability(5);
				itemBuilder.lore("§cInvencibilidade acaba em: " + DateUtils.formatTime(hardcoreGamesServer.getTempo()), 
						"§e" + hardcoreGamesServer.getPlayersGaming() + " jogando.");
				setItem(slot, new MenuItem(itemBuilder.build(), defaultMenuClickHandler));
			} else if (stage == GameStages.PLAYING) {
				itemBuilder.type(Material.STAINED_GLASS_PANE).durability(7);
				itemBuilder.lore("§cO torneio iniciou há: " + DateUtils.formatTime(hardcoreGamesServer.getTempo()),
						"§e" + hardcoreGamesServer.getPlayersGaming() + " jogando.");
				setItem(slot, new MenuItem(itemBuilder.build(), defaultMenuClickHandler));
			} else {
				//evento nulo
			}

			slot++;

			/*if (stage == GameStages.LOADING || stage == GameStages.OFFLINE) {
				//evento nulo
			} else {
				//evento nulo
			}*/

		}

	}

	private final MenuClickHandler defaultMenuClickHandler = (player, inventory, clickType, itemStack, slot) -> {
		int id = Integer.parseInt(itemStack.getItemMeta().getDisplayName().split("#")[1]);


		boolean hasPermission = BukkitMain.getBukkitPlayer(player.getUniqueId()).getGroup().getLevel() >= Groups.YOUTUBER.getLevel(),
				permissionToJoin = player.hasPermission("commons.entrar");

		GameServer server = CommonsGeneral.getServersManager().getHardcoreGamesServer(id);

		if (server.isOnline()) {
			boolean connect = true;

			if (server.isWhiteList() && !hasPermission) {
				connect = false;
				player.sendMessage("§cEsta sala está disponível apenas para a nossa equipe.");
			}

			if (server.getStage() != GameStages.WAITING && !permissionToJoin) {
				connect = false;
				player.sendMessage("§cO torneio já iniciou!");
			}

			if (connect) {
				if (server.getPlayersGaming() < server.getMembersSlots()) {
					BukkitServerAPI.redirectPlayer(player, "HardcoreGames" + id);
				} else {
					if (permissionToJoin) {
						BukkitServerAPI.redirectPlayer(player, "HardcoreGames" + id);
					} else {
						player.sendMessage("§cOs slots para membros acabaram.");
					}
				}
			}
		} else {
			player.sendMessage("§cEsta sala năo está online.");
			player.closeInventory();
		}
	};
}