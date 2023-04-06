package com.br.guilhermematthew.nowly.pvp.mode.fps;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ActionItemStack;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ActionItemStack.InteractHandler;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.api.player.PlayerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.manager.configuration.PluginConfiguration;
import com.br.guilhermematthew.nowly.pvp.PvPMain;
import com.br.guilhermematthew.nowly.pvp.manager.gamer.Gamer;
import com.br.guilhermematthew.nowly.pvp.manager.gamer.GamerManager;

import lombok.Getter;
import lombok.Setter;

public class FPSMode {

	@Getter @Setter
	private static Location spawn;
	
	public static void init() {
		PluginConfiguration.createLocation(PvPMain.getInstance(), "spawn");
		
		ActionItemStack.register("§cRetornar ao Lobby", lobbyAction);
		
		setSpawn(PluginConfiguration.getLocation(PvPMain.getInstance(), "spawn"));
		
		Bukkit.getServer().getPluginManager().registerEvents(new FPSListeners(), PvPMain.getInstance());
	}
	
	public static void refreshPlayer(Player player) {
		Gamer gamer = GamerManager.getGamer(player.getUniqueId());
		gamer.setProtection(true);
		
		if (player.getPassenger() != null) { 
			player.getPassenger().leaveVehicle();
		}
		
		if (player.isInsideVehicle()) {
			player.leaveVehicle();
		}

		player.setFallDistance(-10);
		player.setNoDamageTicks(20);
		
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.updateInventory();
		
		player.setFireTicks(0);
		player.setLevel(0);
		player.setExp((float)0);
        player.setHealth(20.0D);
		
		PlayerAPI.clearEffects(player);
		
		if (player.getGameMode() != GameMode.SURVIVAL) {
			player.setGameMode(GameMode.SURVIVAL);
		}
		
		ItemBuilder itemBuilder = new ItemBuilder();
		
		PlayerInventory playerInventory = player.getInventory();
		
		playerInventory.setHeldItemSlot(0);
		
		playerInventory.setItem(8, itemBuilder.type(Material.BED).name("§cRetornar ao Lobby").build());
		
		player.updateInventory();
		
		FPSScoreboard.updateScoreboard(player);
		playerInventory = null;
		itemBuilder = null;
		gamer = null;
		player = null;
	}
	
	private final static InteractHandler lobbyAction = (player, itemStack, itemAction, clickedBlock) -> {
		if (itemAction.name().contains("LEFT")) return true;
		
		BukkitServerAPI.redirectPlayer(player, "LobbyPvP");
		return true;
	};
}