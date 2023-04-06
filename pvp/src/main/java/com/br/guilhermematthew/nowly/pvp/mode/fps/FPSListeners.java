package com.br.guilhermematthew.nowly.pvp.mode.fps;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.api.title.TitleAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.manager.configuration.PluginConfiguration;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import com.br.guilhermematthew.nowly.pvp.PvPMain;
import com.br.guilhermematthew.nowly.pvp.commands.ServerCommand;
import com.br.guilhermematthew.nowly.pvp.events.GamerDeathEvent;
import com.br.guilhermematthew.nowly.pvp.listeners.CombatLogListener;
import com.br.guilhermematthew.nowly.pvp.utility.PvPUtility;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerRequestEvent;
import com.br.guilhermematthew.nowly.pvp.StringUtils;
import com.br.guilhermematthew.nowly.pvp.manager.gamer.Gamer;
import com.br.guilhermematthew.nowly.pvp.manager.gamer.GamerManager;

public class FPSListeners implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		player.teleport(FPSMode.getSpawn());
		
	    FPSScoreboard.createScoreboard(player);
	    FPSMode.refreshPlayer(player);

		if (BukkitMain.getServerType() == ServerType.PVP_FPS) {
			TitleAPI.sendTitle(player, "§f", "§b§lFPS", 0, 0, 3);
		}

	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(FPSMode.getSpawn());
	}
	
	@EventHandler
	public void onAutoRespawn(PlayerRespawnEvent event) {
		FPSMode.refreshPlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onRequest(PlayerRequestEvent event) {
		if (event.getRequestType().equalsIgnoreCase("update-scoreboard")) {
			FPSScoreboard.updateScoreboard(event.getPlayer());
		} else if (event.getRequestType().equalsIgnoreCase("teleport-spawn")) {
			if (CombatLogListener.isProtected(event.getPlayer())) {
				event.getPlayer().sendMessage(StringUtils.VOCE_JA_ESTA_NO_SPAWN);
				return;
			}
			
		    FPSMode.refreshPlayer(event.getPlayer());
		    event.getPlayer().teleport(FPSMode.getSpawn());
		    event.getPlayer().sendMessage(StringUtils.VOCE_FOI_PARA_O_SPAWN);
		}
	}
	
	@EventHandler
	public void onWarpDeath(GamerDeathEvent event) {
		PvPUtility.dropItens(event.getPlayer(), event.getPlayer().getLocation());

		PvPUtility.handleStats(event.getKiller(), event.getPlayer());

		FPSScoreboard.updateScoreboard(event.getKiller());

		if (event.hasKiller()) {
			PvPUtility.repairArmor(event.getKiller());
		}
	}

	@EventHandler(
			priority = EventPriority.LOW
	)
	public void onSpawnMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Gamer gamer = GamerManager.getGamer(player);

		if(gamer.isProtection() && player.getLocation().distance(PluginConfiguration.getLocation(PvPMain.getInstance(), "spawn")) > 8.5D) {
			gamer.setProtection(false);

			ItemBuilder itemBuilder = new ItemBuilder();

			PlayerInventory playerInventory = player.getInventory();
			playerInventory.clear();

			playerInventory.setHeldItemSlot(0);

			playerInventory.setHelmet(new ItemStack(Material.IRON_HELMET));
			playerInventory.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
			playerInventory.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
			playerInventory.setBoots(new ItemStack(Material.IRON_BOOTS));

			playerInventory.setItem(0, itemBuilder.type(Material.DIAMOND_SWORD).name("§7Espada de Diamante")
					.unbreakable().enchantment(Enchantment.DAMAGE_ALL).build());

			playerInventory.setItem(13, itemBuilder.type(Material.BOWL).amount(64).build());
			playerInventory.setItem(14, itemBuilder.type(Material.RED_MUSHROOM).amount(64).build());
			playerInventory.setItem(15, itemBuilder.type(Material.BROWN_MUSHROOM).amount(64).build());

			ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);

			for (ItemStack is : playerInventory.getContents()) {
				if (is == null) {
					playerInventory.addItem(soup);
				}
			}

			player.updateInventory();
		}

	}
	/*@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntytyDamageEvent(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		Player player = (Player) event.getEntity();
		
		if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
			Gamer gamer = GamerManager.getGamer(player);


			if (gamer.isProtection()) {
				event.setCancelled(true);

				player.setFallDistance(5);
				player.setNoDamageTicks(20);
				gamer.setProtection(false);
				
				ItemBuilder itemBuilder = new ItemBuilder();

				PlayerInventory playerInventory = player.getInventory();
				playerInventory.clear();
				
				playerInventory.setHeldItemSlot(0);

				playerInventory.setHelmet(new ItemStack(Material.IRON_HELMET));
				playerInventory.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
				playerInventory.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
				playerInventory.setBoots(new ItemStack(Material.IRON_BOOTS));
				
				playerInventory.setItem(0, itemBuilder.type(Material.DIAMOND_SWORD).name("§7Espada de Diamante")
						.unbreakable().enchantment(Enchantment.DAMAGE_ALL).build());

				playerInventory.setItem(13, itemBuilder.type(Material.BOWL).amount(64).build());
				playerInventory.setItem(14, itemBuilder.type(Material.RED_MUSHROOM).amount(64).build());
				playerInventory.setItem(15, itemBuilder.type(Material.BROWN_MUSHROOM).amount(64).build());

				ItemStack soup = new ItemStack(Material.MUSHROOM_SOUP);

				for (ItemStack is : playerInventory.getContents()) {
					if (is == null) {
						playerInventory.addItem(soup);
					}
				}

				player.updateInventory();
			}
		} else if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
			event.setCancelled(true);

			if (!GamerManager.getGamer(player).isProtection()) CombatLogListener.checkCombatLog(player);
			FPSMode.refreshPlayer(player);
			player.teleport(FPSMode.getSpawn());
		}
	}*/

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		event.setCancelled((!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) ||
				(!ServerCommand.autorizados.contains(event.getPlayer().getUniqueId())));
	}
}