package com.br.guilhermematthew.nowly.lobby.common;

import org.bukkit.entity.Player;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.types.NetworkServer;
import com.br.guilhermematthew.nowly.lobby.common.inventory.InventoryCommon;

public class LobbyUtility {

	public static void handleInteract(Player player, ServerType serverClicked, ServerType serverConnected) {
		if (serverClicked != ServerType.UNKNOWN) {
			if (serverConnected != ServerType.LOBBY) {
				if (serverClicked == ServerType.HARDCORE_GAMES) {
					InventoryCommon.getHardcoreGamesInventory().open(player);
				} else if (serverClicked == ServerType.BEDWARS) {
					//abrir menu
					player.sendMessage("§cEm breve...");
				} else if (serverClicked == ServerType.THEBRIDGE) {
					//abrir menu
					player.sendMessage("§cEm breve...");
				} else if (serverClicked == ServerType.SKYWARS) {
					//abrir menu
					player.sendMessage("§cEm breve...");
				} else if (serverClicked == ServerType.EVENTO || serverClicked == ServerType.CHAMPIONS) {
					if (CommonsGeneral.getServersManager().getNetworkServer(serverClicked.getName()).isOnline()) {
						BukkitServerAPI.redirectPlayer(player, serverClicked.getName());
					} else {
						player.sendMessage("§cNenhuma sala disponivel no momento.");
					}
				} else {
					checkAndConnect(player, serverClicked.getName());
				}
			} else {
				checkAndConnect(player, serverClicked.getName());
			}
		} else {
			player.sendMessage("§cEm breve.");
		}
	}
	
	private static void checkAndConnect(Player player, String serverName) {
		NetworkServer server = CommonsGeneral.getServersManager().getNetworkServer(serverName);

		if (server != null) {
			if (server.isOnline()) {
				boolean connect = true;
				
				if (server.getOnlines() >= server.getMembersSlots()) {
					if (!player.hasPermission("commons.entrar")) {
						connect = false;
						player.sendMessage("§cOs slots para membros acabaram.");
					}
				}
				
				if (connect) {
					BukkitServerAPI.redirectPlayer(player, serverName);
				}
			} else {
				player.sendMessage("§cNenhuma sala disponivel no momento.");
			}
		} else {

		}
	}
}