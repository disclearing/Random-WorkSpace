package com.br.guilhermematthew.nowly.hardcoregames.listeners;

import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.events.player.PlayerDamagePlayerEvent;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.br.guilhermematthew.nowly.commons.bukkit.api.vanish.VanishAPI;

public class SpectatorListener implements Listener {

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player)event.getEntity();
			if (!GamerManager.getGamer(player.getUniqueId()).isPlaying()) {
			    event.setCancelled(true);
			}
			player = null;
		}
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		
		if (!VanishAPI.inAdmin(player)) {
			if (!GamerManager.getGamer(player.getUniqueId()).isPlaying()) {
				event.setCancelled(true);
			}
		}
		
		player = null;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDamage(PlayerDamagePlayerEvent event) {
		if (VanishAPI.inAdmin(event.getPlayer())) {
			event.setCancelled(true);
			return;
		}
		
		if (!GamerManager.getGamer(event.getPlayer().getUniqueId()).isPlaying()) {
			event.setCancelled(true);
			return;
		}
		
		if (VanishAPI.inAdmin(event.getDamaged())) {
			event.setCancelled(true);
			return;
		}
		if (!GamerManager.getGamer(event.getDamaged().getUniqueId()).isPlaying()) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		if (!GamerManager.getGamer(event.getPlayer().getUniqueId()).isPlaying()) {
			event.setCancelled(true);
			return;
		}
		if (VanishAPI.inAdmin(event.getPlayer())) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onTarget(EntityTargetEvent event) {
		if (event.getTarget() instanceof Player) {
			final Player player = (Player) event.getTarget();
			if (!GamerManager.getGamer(player.getUniqueId()).isPlaying()) {
				event.setCancelled(true);
				return;
			}
			if (VanishAPI.inAdmin(player)) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onInteractBlock(PlayerInteractEvent event) {
		if (!GamerManager.getGamer(event.getPlayer().getUniqueId()).isPlaying()) {
			if (event.getAction() == Action.PHYSICAL) {
				event.setCancelled(true);
				return;
			}
			
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Block b = event.getClickedBlock();
				if (b.getState() instanceof DoubleChest || b.getState() instanceof Chest || b.getState() instanceof Hopper || b.getState() instanceof Dispenser || b.getState() instanceof Furnace || b.getState() instanceof Beacon) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (!GamerManager.getGamer(event.getPlayer().getUniqueId()).isPlaying()) {
			event.setCancelled(true);
			return;
		}
		if (VanishAPI.inAdmin(event.getPlayer())) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (!HardcoreGamesMain.getGameManager().isPreGame()) {
			final Player player = event.getPlayer();
			if (VanishAPI.inAdmin(player)) {
				event.setCancelled(false);
				return;
			}
			if (!GamerManager.getGamer(player.getUniqueId()).isPlaying()) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (!HardcoreGamesMain.getGameManager().isPreGame()) {
			final Player player = event.getPlayer();
			if (VanishAPI.inAdmin(player)) {
				event.setCancelled(false);
				return;
			}
			if (!GamerManager.getGamer(player.getUniqueId()).isPlaying()) {
				event.setCancelled(true);
				return;
			}
		}
	}
}