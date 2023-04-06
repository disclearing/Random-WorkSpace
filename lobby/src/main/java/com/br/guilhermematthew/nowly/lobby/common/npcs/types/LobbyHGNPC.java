package com.br.guilhermematthew.nowly.lobby.common.npcs.types;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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

public class LobbyHGNPC extends NPCCommon {

	@Getter
	private static Hologram playingHG, playingChampions, playingEventos;
	
	@SuppressWarnings("deprecation")
	@Override
	public void create() {
		createLocations();
		
		Skin skin = SkinFetcher.fetchSkinByNick("EuFakeON", "7ac29642-e158-4a34-8909-53dbce9efcbd", true);
		
		NPC npc = NPCLib.createNPC("HG", "§7§a", "47kypxflog", skin, Material.MUSHROOM_SOUP.getId());
		npc.create(PluginConfiguration.getLocation(LobbyMain.getInstance(), "bots.hg"));
		
		skin = SkinFetcher.fetchSkinByNick("SpectayUS", "49f67001-bf18-4164-b37a-5f3e1ab460d5", true);
		npc = NPCLib.createNPC("Evento", "§7§f", "v990g8u6wq", skin, Material.CAKE.getId());
		npc.create(PluginConfiguration.getLocation(LobbyMain.getInstance(), "bots.evento"));
		
		skin = SkinFetcher.fetchSkinByNick("iHuxNOWLY", "43d75222-0290-48ea-a18a-440d77b16047", true);
		npc = NPCLib.createNPC("Champions", "§7§c", "gytixcqcj7", skin, Material.DIAMOND_CHESTPLATE.getId());
		npc.create(PluginConfiguration.getLocation(LobbyMain.getInstance(), "bots.champion"));

		createHolograms();
		registerListeners();
	}

	@Override
	public void update() {
		playingHG.setText("§e" + CommonsGeneral.getServersManager().getAmountOnlineHardcoreGames(false) + " jogando.");
		playingChampions.setText("§e" + CommonsGeneral.getServersManager().getHardcoreGamesServer("Champions", 1).getOnlines() + " jogando.");
		playingEventos.setText("§e" + CommonsGeneral.getServersManager().getHardcoreGamesServer("Evento", 1).getOnlines() + " jogando.");
	}

	@Override
	public void createLocations() {
		PluginConfiguration.createLocations(LobbyMain.getInstance(), "bots.hg", "bots.evento", "bots.champion");
	}

	@Override
	public void createHolograms() {
		playingHG = HologramAPI.createHologram("jogandoAgora", NPCManager.getNPCByName("HG").getLocationForHologram().clone().add(0, 0.01, 0),
				"§e0 jogando.");
		
		playingHG.spawn();
		playingHG.addLineAbove("bot", "§bHG Mix");
		
		playingChampions = HologramAPI.createHologram("jogandoAgora", NPCManager.getNPCByName("Champions").getLocationForHologram().clone().add(0, 0.01, 0),
				"§e0 jogando.");

		playingChampions.spawn();
		playingChampions.addLineAbove("bot", "§bChampions");
		playingChampions.addLineAbove("bot", "§e15h 17h 19h 21h");
		
		playingEventos = HologramAPI.createHologram("jogandoAgora", NPCManager.getNPCByName("Evento").getLocationForHologram().clone().add(0, 0.01, 0),
				"§e0 jogando.");
		
		playingEventos.spawn();
		playingEventos.addLineAbove("bot", "§bEvento");
	}

	@Override
	public void registerListeners() {
		Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
			
			@EventHandler
			public void onInteract(NPCInteractEvent event) {
				LobbyUtility.handleInteract(event.getPlayer(),
						ServerType.getServer(event.getNpc().getCustomName()), BukkitMain.getServerType());
			}
			
		}, LobbyMain.getInstance());
	}
}