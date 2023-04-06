package com.br.guilhermematthew.nowly.lobby.common.npcs.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.apache.commons.lang3.RandomStringUtils;
import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.Hologram;
import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.HologramAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.npc.NPCLib;
import com.br.guilhermematthew.nowly.commons.bukkit.api.npc.NPCManager;
import com.br.guilhermematthew.nowly.commons.bukkit.api.npc.api.NPC;
import com.br.guilhermematthew.nowly.commons.bukkit.api.npc.events.NPCInteractEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.manager.configuration.PluginConfiguration;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import com.br.guilhermematthew.nowly.commons.common.utility.skin.Skin;
import com.br.guilhermematthew.nowly.commons.common.utility.skin.SkinFetcher;
import com.br.guilhermematthew.nowly.lobby.LobbyMain;
import com.br.guilhermematthew.nowly.lobby.common.LobbyUtility;
import com.br.guilhermematthew.nowly.lobby.common.npcs.NPCCommon;

import lombok.Getter;

public class LobbyNPC extends NPCCommon {

	@Getter
	private static Hologram playingHG, playingPvP, playingDuels;
	
	@SuppressWarnings("deprecation")
	@Override
	public void create() {
		createLocations();
		String npcProvider = "§8[NPC] ";
		Skin skin = SkinFetcher.fetchSkinByNick("TheCaioX", "fd9a855c-dfd9-4939-9bac-a0ed3ba0c72e", true);

		NPC npc = NPCLib.createNPC("HG", "§7", "vkxz6shs6d", skin, Material.MUSHROOM_SOUP.getId());
		npc.create(PluginConfiguration.getLocation(LobbyMain.getInstance(), "bots.hg"));
		
		skin = SkinFetcher.fetchSkinByNick("enbees", "d0140ddd-e7b9-41ed-a9cb-bfd81a8015e8", true);
		npc = NPCLib.createNPC("PvP", "§7§5", "0gy1pyow5x", skin, Material.IRON_CHESTPLATE.getId());
		npc.create(PluginConfiguration.getLocation(LobbyMain.getInstance(), "bots.pvp"));
		
		skin = SkinFetcher.fetchSkinByNick("Agera991", "866e1109-3b9f-4e2d-850a-aae37c01c5ca", true);
		npc = NPCLib.createNPC("Duels", "§7§0", "826ejkt171", skin, Material.DIAMOND_SWORD.getId());
		npc.create(PluginConfiguration.getLocation(LobbyMain.getInstance(), "bots.duels"));

		createHolograms();
		registerListeners();
	}

	@Override
	public void update() {
		playingHG.setText("§e" + CommonsGeneral.getServersManager().getAmountOnlineHardcoreGames(true) + " jogando.");
		playingPvP.setText("§e" + CommonsGeneral.getServersManager().getAmountOnlinePvP(true) + " jogando.");
		playingDuels.setText("§e0 jogando.");
	}

	@Override
	public void createLocations() {
		PluginConfiguration.createLocations(LobbyMain.getInstance(), "bots.hg", "bots.pvp", "bots.duels");
	}

	@Override
	public void createHolograms() {
		playingHG = HologramAPI.createHologram("jogandoAgora", NPCManager.getNPCByName("HG").getLocationForHologram().clone().add(0, 0.01, 0),
				"§e0 jogando.");
		
		playingHG.spawn();
		playingHG.addLineAbove("bot", "§bHG");
		
		playingPvP = HologramAPI.createHologram("jogandoAgora", NPCManager.getNPCByName("PvP").getLocationForHologram().clone().add(0, 0.01, 0),
				"§e0 jogando.");
		
		playingPvP.spawn();
		playingPvP.addLineAbove("bot", "§bPvP");

		playingDuels = HologramAPI.createHologram("jogandoAgora", NPCManager.getNPCByName("Duels").getLocationForHologram().clone().add(0, 0.01, 0),
				"§e0 jogando.");

		playingDuels.spawn();
		playingDuels.addLineAbove("bot", "§bDuels");
	}

	@Override
	public void registerListeners() {
		Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
			
			@EventHandler
			public void onInteract(NPCInteractEvent event) {
				ServerType serverClicked = ServerType.getServer(event.getNpc().getCustomName());
				if (event.getNpc().getCustomName().equalsIgnoreCase("PvP")) {
					serverClicked = ServerType.LOBBY_PVP;
				} else if (event.getNpc().getCustomName().equalsIgnoreCase("HG")) {
					serverClicked = ServerType.LOBBY_HARDCOREGAMES;
				} else if (event.getNpc().getCustomName().equalsIgnoreCase("Duels")) {
					serverClicked = ServerType.LOBBY_DUELS;
				}
				
				LobbyUtility.handleInteract(event.getPlayer(), serverClicked, BukkitMain.getServerType());
			}
			
		}, LobbyMain.getInstance());
	}
}