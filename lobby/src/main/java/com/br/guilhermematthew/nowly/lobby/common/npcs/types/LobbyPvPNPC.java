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

public class LobbyPvPNPC extends NPCCommon {

	@Getter
	private static Hologram playingArena, playingFPS, playingLavaChallenge;
	
	@SuppressWarnings("deprecation")
	@Override
	public void create() {
		createLocations();
		
		Skin skin = SkinFetcher.fetchSkinByNick("Kongamitow", "e13fb198-4ecb-41f4-93af-5cb0acd390f7", true);
		
		NPC npc = NPCLib.createNPC("Arena", "§7", "py3lz629zv", skin, Material.STONE_SWORD.getId());
		npc.create(PluginConfiguration.getLocation(LobbyMain.getInstance(), "bots.arena"));
		
		skin = SkinFetcher.fetchSkinByNick("iibzNowly", "d212253c-3388-4653-91b7-7379dbd4ca4d", true);
		npc = NPCLib.createNPC("FPS", "§7§1", "rh7rgj4q7t", skin, Material.DIAMOND_SWORD.getId());
		npc.create(PluginConfiguration.getLocation(LobbyMain.getInstance(), "bots.fps"));
		
		skin = SkinFetcher.fetchSkinByNick("Procurador", "ca163766-6db2-4c42-b33a-a574fbaf6cff", true);
		npc = NPCLib.createNPC("LavaChallenge", "§7§2", "5hdtvact0p", skin, Material.LAVA_BUCKET.getId());
		npc.create(PluginConfiguration.getLocation(LobbyMain.getInstance(), "bots.lavachallenge"));

		createHolograms();
		registerListeners();
	}

	@Override
	public void update() {
		playingArena.setText("§e" + CommonsGeneral.getServersManager().getNetworkServer("arena").getOnlines() + " jogando.");
		playingFPS.setText("§e" + CommonsGeneral.getServersManager().getNetworkServer("fps").getOnlines() + " jogando.");
		playingLavaChallenge.setText("§e" + CommonsGeneral.getServersManager().getNetworkServer("lavachallenge").getOnlines() + " jogando.");
	}

	@Override
	public void createLocations() {
		PluginConfiguration.createLocations(LobbyMain.getInstance(), "bots.arena", "bots.fps", "bots.lavachallenge");
	}

	@Override
	public void createHolograms() {
		playingArena = HologramAPI.createHologram("jogandoAgora", NPCManager.getNPCByName("Arena").getLocationForHologram().clone().add(0, 0.01, 0),
				"§e0 jogando.");
		
		playingArena.spawn();
		playingArena.addLineAbove("bot", "§bArena");
		
		playingFPS = HologramAPI.createHologram("jogandoAgora", NPCManager.getNPCByName("FPS").getLocationForHologram().clone().add(0, 0.01, 0),
				"§e0 jogando.");
		
		playingFPS.spawn();
		playingFPS.addLineAbove("bot", "§bFPS");
		
		playingLavaChallenge = HologramAPI.createHologram("jogandoAgora", NPCManager.getNPCByName("LavaChallenge").getLocationForHologram().clone().add(0, 0.01, 0),
				"§e0 jogando.");
		
		playingLavaChallenge.spawn();
		playingLavaChallenge.addLineAbove("bot", "§bLava");
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