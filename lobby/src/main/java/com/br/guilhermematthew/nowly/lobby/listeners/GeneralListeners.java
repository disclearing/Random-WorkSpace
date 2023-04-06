package com.br.guilhermematthew.nowly.lobby.listeners;

import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.sidebar.Sidebar;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.sidebar.SidebarManager;
import com.br.guilhermematthew.nowly.commons.bukkit.utility.LocationUtil;
import com.br.guilhermematthew.nowly.lobby.common.scoreboard.animating.StringAnimation;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent.UpdateType;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerChangeScoreboardEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerRequestEvent;
import com.br.guilhermematthew.nowly.lobby.LobbyMain;
import com.br.guilhermematthew.nowly.lobby.commands.ServerCommand;
import com.br.guilhermematthew.nowly.lobby.common.inventory.InventoryCommon;

public class GeneralListeners implements Listener {

	private StringAnimation animation;
	private String text = "";
	public static int MINUTES = 0;
	
	@EventHandler
	public void onRequest(PlayerRequestEvent event) {
		if (event.getRequestType().equalsIgnoreCase("update-scoreboard")) {
			LobbyMain.getScoreBoardCommon().createScoreboard(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onMinute(BukkitUpdateEvent event) {
		if (event.getType() != UpdateType.MINUTO) return;
		
		if (MINUTES++ == 20) {
			if (LobbyMain.getHologramCommon() != null) {
				LobbyMain.getHologramCommon().update();
			}
			MINUTES = 0;
		}
	}
	
	@EventHandler
	public void onRealMove(PlayerMoveEvent event) {
		if(!LocationUtil.isRealMovement(event.getFrom(), event.getTo())) return;

        Material material = event.getTo().getBlock().getRelative(BlockFace.DOWN).getType();
		
        if (material == Material.SLIME_BLOCK) {
        	Player player = event.getPlayer();
        	
			player.setVelocity(player.getLocation().getDirection().multiply(3.1).setY(0.5));
    		player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 6.0F, 1.0F);
    		player.setFallDistance(-30);
        }
	}
	
	@EventHandler
	public void onSecond(BukkitUpdateEvent event) {
		if (event.getType() != UpdateType.SEGUNDO) {
			return;
		}
		
		CommonsGeneral.getServersManager().sendRequireUpdate();
		InventoryCommon.update();
		
		if (LobbyMain.getNpcCommon() != null) {
			LobbyMain.getNpcCommon().update();
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onUpdateScoreboard(BukkitUpdateEvent event) {
		if (event.getType() != UpdateType.TICK) return;
		if (event.getCurrentTick() % 40 != 0) return;

		Bukkit.getOnlinePlayers().forEach(player -> LobbyMain.getScoreBoardCommon().updateScoreboard(player));
	}
	
	@EventHandler
	public void onChangeScoreboard(PlayerChangeScoreboardEvent event) {
		if (event.getChangeType() == PlayerChangeScoreboardEvent.ScoreboardChangeType.DESATIVOU) {
			event.setCancelled(true);
		} else {
			LobbyMain.getScoreBoardCommon().createScoreboard(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onSpread(BlockSpreadEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onFood(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		event.setCancelled(event.toWeatherState());
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		event.setCancelled(true);
		
		if (event.getCause() == DamageCause.VOID) {
			event.getEntity().teleport(LobbyMain.getSpawn());
		}
	}
	
	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockFade(BlockFadeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPortal(PlayerPortalEvent event) {
		event.setCancelled(true);
	}

	/*
	@EventHandler
	public void onBlockForm(BlockFormEvent event) {
		event.setCancelled(true);
	}*/

	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		event.setCancelled(true);
	}

	/*
	@EventHandler
	public void onBlockGrow(BlockGrowEvent event) {
		event.setCancelled(true);
	}*/

	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onExplosion(ExplosionPrimeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntitySpawn(CreatureSpawnEvent event) {
		event.setCancelled(true);
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
	public void onItemSpawn(ItemSpawnEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPhysics(BlockPhysicsEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onBreak(BlockBreakEvent event) {
		event.setCancelled((!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) ||
				(!ServerCommand.autorizados.contains(event.getPlayer().getUniqueId())));
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		event.setCancelled((!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) ||
				(!ServerCommand.autorizados.contains(event.getPlayer().getUniqueId())));
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.PHYSICAL) {
			event.setCancelled(true); 
			return;
		}

		event.setCancelled((!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) ||
				(!ServerCommand.autorizados.contains(event.getPlayer().getUniqueId())));
	}
	
	@EventHandler
	public void onRegain(EntityRegainHealthEvent event) {
		event.setCancelled(true);
	}
}