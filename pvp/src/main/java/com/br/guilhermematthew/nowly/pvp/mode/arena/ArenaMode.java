package com.br.guilhermematthew.nowly.pvp.mode.arena;

import com.br.guilhermematthew.nowly.pvp.menu.KitSelector;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.PlayerInventory;

import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ActionItemStack;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ActionItemStack.InteractHandler;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.api.player.PlayerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.vanish.VanishAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.manager.configuration.PluginConfiguration;
import com.br.guilhermematthew.nowly.pvp.PvPMain;
import com.br.guilhermematthew.nowly.pvp.StringUtils;
import com.br.guilhermematthew.nowly.pvp.manager.gamer.Gamer;
import com.br.guilhermematthew.nowly.pvp.manager.gamer.GamerManager;
import com.br.guilhermematthew.nowly.pvp.manager.kit.KitLoader;
import com.br.guilhermematthew.nowly.pvp.menu.enums.InventoryMode;

import lombok.Getter;
import lombok.Setter;

public class ArenaMode {

	@Getter @Setter
	private static Location spawn;
	
	public static void init() {
		PluginConfiguration.createLocation(PvPMain.getInstance(), "spawn");
		
		setSpawn(PluginConfiguration.getLocation(PvPMain.getInstance(), "spawn"));
		
		KitLoader.load();
		
		ActionItemStack.register("§cRetornar ao Lobby", lobbyAction);
		ActionItemStack.register("§aSelecionar kit 1", kitPrimaryAction);
		ActionItemStack.register("§aSelecionar kit 2", kitSecundaryAction);
		ActionItemStack.register("§aLoja de Kits", kitShopAction);
		ActionItemStack.register("§aBússola", compassAction);
		
		setSpawn(PluginConfiguration.getLocation(PvPMain.getInstance(), "spawn"));
		
		Bukkit.getServer().getPluginManager().registerEvents(new ArenaListeners(), PvPMain.getInstance());
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
		
		player.setNoDamageTicks(20);
		player.setFallDistance(-5);
		
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
		
		playerInventory.setItem(0, itemBuilder.type(Material.CHEST).name("§aSelecionar kit 1").build());
		playerInventory.setItem(1, itemBuilder.type(Material.CHEST).amount(2).name("§aSelecionar kit 2").build());
		playerInventory.setItem(2, itemBuilder.type(Material.EMERALD).name("§aLoja de Kits").build());
		
		playerInventory.setItem(8, itemBuilder.type(Material.BED).name("§cRetornar ao Lobby").build());
		
		player.updateInventory();
		
		ArenaScoreboard.updateScoreboard(player);
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
	
	private final static InteractHandler kitPrimaryAction = (player, itemStack, itemAction, clickedBlock) -> {
		if (itemAction.name().contains("LEFT")) return true;

		new KitSelector(player, InventoryMode.KIT_PRIMARIO).open(player);
		return true;
	};
	
	private final static InteractHandler kitSecundaryAction = (player, itemStack, itemAction, clickedBlock) -> {
		if (itemAction.name().contains("LEFT")) return true;
		
		new KitSelector(player, InventoryMode.KIT_SECUNDARIO).open(player);
		return true;
	};
	
	private final static InteractHandler kitShopAction = (player, itemStack, itemAction, clickedBlock) -> {
		if (itemAction.name().contains("LEFT")) return true;
		
		new KitSelector(player, InventoryMode.LOJA).open(player);
		return true;
	};
	
	private final static InteractHandler compassAction = (player, itemStack, itemAction, clickedBlock) -> {
		if (itemAction == Action.PHYSICAL) return true;
		
        final Player alvo = getRandomPlayer(player);
        
        if (alvo == null) {
        	player.sendMessage(StringUtils.BUSSOLA_NOT_FINDED);
        	player.setCompassTarget(player.getWorld().getSpawnLocation());
        } else {
        	player.sendMessage(StringUtils.BUSSOLA_FINDED.replace("%nick%", alvo.getName()));
       	    player.setCompassTarget(alvo.getLocation());
        }
		return true;
	};
	
	private static Player getRandomPlayer(final Player player) {
		Player target = null;
		
		for (Player inWarp : Bukkit.getOnlinePlayers()) {
			 if (inWarp == player) {
				 continue;
			 }
			 if (VanishAPI.isInvisible(inWarp)) {
				 continue;
			 }
			 
			 if (inWarp.getLocation().distance(player.getLocation()) >= 15.0D) {
  	    		 if (target == null) {
  					 target = inWarp;
  	    		 } else {
  					 double distanciaAtual = target.getLocation().distance(player.getLocation());
  					 double novaDistancia = inWarp.getLocation().distance(player.getLocation());
  					 if (novaDistancia < distanciaAtual) {
  					     target = inWarp;
  						 if (novaDistancia <= 30) {
  							 break;
  						 }
  					 }
  	    		 }
			 }
		}
		return target;
	}
}