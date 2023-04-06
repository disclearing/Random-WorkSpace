package com.br.guilhermematthew.nowly.hardcoregames.listeners;

import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesOptions;
import com.br.guilhermematthew.nowly.hardcoregames.events.game.GameStageChangeEvent;
import com.br.guilhermematthew.nowly.hardcoregames.events.game.GameTimerEvent;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;
import com.br.guilhermematthew.nowly.hardcoregames.menu.KitSelector;
import com.br.guilhermematthew.nowly.hardcoregames.menu.enums.InventoryMode;
import com.br.guilhermematthew.nowly.hardcoregames.utility.HardcoreGamesUtility;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.enums.GameStages;

public class PreGameListeners implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		if (player.getGameMode() != GameMode.ADVENTURE) 
			player.setGameMode(GameMode.ADVENTURE);
		
		ItemBuilder itemBuilder = new ItemBuilder();
		
		PlayerInventory playerInventory = player.getInventory();
		
		playerInventory.clear();
		playerInventory.setArmorContents(null);
		
		playerInventory.setItem(0, itemBuilder.type(Material.CHEST).name("§aEscolher Kit").build());
		
		if (HardcoreGamesOptions.DOUBLE_KIT) {
			playerInventory.setItem(1, itemBuilder.type(Material.CHEST).amount(2).name("§aEscolher Kit 2").build());
		}
		
		playerInventory.addItem(itemBuilder.type(Material.EMERALD).name("§6Loja de Kits").build());
		
		playerInventory.setItem(8, itemBuilder.type(Material.BED).name("§cVoltar ao Lobby").build());
		
        player.teleport(HardcoreGamesUtility.getRandomLocation(40));
	}
	
	@EventHandler
	public void onTimer(GameTimerEvent event) {
		World world = Bukkit.getWorlds().get(0);
		
		world.setTime(0);
		world.setStorm(false);
		world.setThundering(false);
		
		if (HardcoreGamesMain.getTimerManager().getLastAlive() < HardcoreGamesOptions.MIN_PLAYERS) {
			HardcoreGamesMain.getTimerManager().updateTime(300);
			event.setTime(300);
			return;
		}
		
		if (event.getTime() == 0) {
			HardcoreGamesMain.getGameManager().getGameType().start();
			return;
		}
		
		if (event.getTime() == 15) {
			Bukkit.getOnlinePlayers().forEach(onlines -> onlines.teleport(HardcoreGamesUtility.getRandomLocation(20)));
		}
		
		event.checkMessage();
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		event.setCancelled(true);
		
		ItemStack itemStack = event.getPlayer().getInventory().getItemInHand();
		
		if (event.getAction().name().contains("RIGHT")) {
			if (BukkitServerAPI.checkItem(itemStack, "§aEscolher Kit 2")) {
				new KitSelector(event.getPlayer(), InventoryMode.KIT_SECUNDARIO).open(event.getPlayer());
			} else if (BukkitServerAPI.checkItem(itemStack, "§aEscolher Kit")) {
				new KitSelector(event.getPlayer(), InventoryMode.KIT_PRIMARIO).open(event.getPlayer());
			} else if (BukkitServerAPI.checkItem(itemStack, "§6Loja de Kits")) {
				new KitSelector(event.getPlayer(), InventoryMode.LOJA).open(event.getPlayer());
			} else if (BukkitServerAPI.checkItem(itemStack, "§cVoltar ao Lobby")) {
				BukkitServerAPI.redirectPlayer(event.getPlayer(), "LobbyHardcoreGames");
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		GamerManager.removeGamer(event.getPlayer().getUniqueId());
		
		BukkitServerAPI.removePlayerFile(event.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onFood(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onItemSpawn(ItemSpawnEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onExplosion(ExplosionPrimeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		event.setCancelled(event.toWeatherState());
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onIgnite(BlockIgniteEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void craftItem(CraftItemEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onStart(GameStageChangeEvent event) {
		if (event.getLastStage() == GameStages.WAITING &&
				event.getNewStage() == GameStages.INVINCIBILITY) {
			
			HardcoreGamesMain.console("Removing listeners from PreGameListeners");
			
			HandlerList.unregisterAll(this);
		}
	}
}