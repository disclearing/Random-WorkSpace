package com.br.guilhermematthew.nowly.pvp.listeners;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.api.vanish.VanishAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent.UpdateType;
import com.br.guilhermematthew.nowly.pvp.commands.ServerCommand;
import com.br.guilhermematthew.nowly.pvp.events.GamerDeathEvent;
import com.br.guilhermematthew.nowly.pvp.events.PlayerDamagePlayerEvent;
import com.br.guilhermematthew.nowly.pvp.manager.combatlog.CombatLogManager;
import com.br.guilhermematthew.nowly.pvp.manager.gamer.GamerManager;

public class GeneralListeners implements Listener {
	
	private int MINUTES = 0;
	
	@EventHandler
	public void onInit(PlayerJoinEvent event) {
		event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation());
	}
	
	@EventHandler
	public void onMinute(BukkitUpdateEvent event) {
		if (event.getType() != UpdateType.MINUTO) return;
		
		MINUTES++;
		
		if (MINUTES == 12) {
			//HologramManager.updateHolograms();
			
			MINUTES = 0;
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		event.setDeathMessage(null);
		event.getDrops().clear();

		Player morreu = event.getEntity(), 
				matou = getKiller(morreu);

		morreu.playSound(morreu.getLocation(), Sound.ANVIL_LAND, 1, 1);
		
		if (matou != null) {
			matou.playSound(matou.getLocation(), Sound.LEVEL_UP, 1, 1);
		}

		Bukkit.getServer().getPluginManager().callEvent(new GamerDeathEvent(morreu, matou));
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		ItemStack item = event.getItemDrop().getItemStack();

		if (item.hasItemMeta()) {
			event.setCancelled(true);
		} else if (item.toString().contains("SWORD") || item.toString().equalsIgnoreCase("COMPASS")
				|| item.toString().contains("AXE")) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onItemPickUp(PlayerPickupItemEvent event) {
		if (VanishAPI.inAdmin(event.getPlayer())) {
			event.setCancelled(true);
			return;
		}

		if (GamerManager.getGamer(event.getPlayer()).isProtection()) {
			event.setCancelled(true);
			return;
		}

		final ItemStack item = event.getItem().getItemStack();

		if (item.getItemMeta().hasDisplayName()) {
			event.setCancelled(true);
			return;
		}
		if (item.getType().toString().contains("SWORD") || item.getType().toString().contains("AXE")) {
			event.setCancelled(true);
			return;
		}
		if (item.getType().toString().contains("HELMET") || item.getType().toString().contains("CHESTPLATE")
				|| item.getType().toString().contains("LEGGING") || item.getType().toString().contains("BOOTS")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onUpdate(BukkitUpdateEvent event) {
		if (event.getType() != UpdateType.SEGUNDO) return;
		
		World world = Bukkit.getServer().getWorld("world");
		
		if (world != null) {
			for (Entity itens : world.getEntitiesByClass(Item.class)) {
				 if (!(itens instanceof Item)) {
					 continue;
				 }
				
				 if (itens.getTicksLived() >= 200) {
					 itens.remove();
				 }
			}
		}
	}
	
	@EventHandler
	public void onExplosion(ExplosionPrimeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
		if ((event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED)
				|| (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN)) {
			event.setCancelled(true);
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
	public void onBlockForm(BlockFormEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockGrow(BlockGrowEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getEntity();
			arrow.remove();
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
	public void onBlockIgnite(BlockIgniteEvent event) {
		// event.setCancelled(true);
	}

	@EventHandler
	public void onEntitySpawn(CreatureSpawnEvent event) {
		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) return;

		Player damager = null;
		
		if (event.getDamager() instanceof Player) {
			damager = (Player) event.getDamager();
		} else if (event.getDamager() instanceof Projectile) {
			Projectile pr = (Projectile) event.getDamager();
			if (pr.getShooter() != null && pr.getShooter() instanceof Player) {
				damager = (Player) pr.getShooter();
			}
		}

		if (damager == null) return;

		if (event.isCancelled()) return;

		PlayerDamagePlayerEvent event2 = new PlayerDamagePlayerEvent(damager, (Player) event.getEntity(),
				event.getDamage());

		Bukkit.getPluginManager().callEvent(event2);
		event.setCancelled(event2.isCancelled());
		event.setDamage(event2.getDamage());
	}

	@EventHandler
	public void onPlayerInteractListener(PlayerInteractEvent event) {
		if(event.getAction() == Action.PHYSICAL) return;

		if ((event.getPlayer().getGameMode() != GameMode.CREATIVE)
				&& (event.getAction() == Action.PHYSICAL && event.getClickedBlock() != null 
				&& event.getClickedBlock().getType() != Material.STONE_PLATE
				&& event.getClickedBlock().getType() != Material.WOOD_PLATE)
				
				|| (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null
						&& (event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE
								|| event.getClickedBlock().getType() == Material.CHEST
								|| event.getClickedBlock().getType() == Material.ENDER_CHEST))) {
			event.setCancelled(true);
		}
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

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (e.getLine(0).equalsIgnoreCase("[sopa]")) {
			e.setLine(0, "");
			e.setLine(1, "§2§lLEAGUE");
			e.setLine(2, "§fSopas");
			e.setLine(3, "");
			e.getPlayer().sendMessage("§aPlaca criada com sucesso.");
		} else if (e.getLine(0).equalsIgnoreCase("[recraft]")) {
			e.setLine(0, "");
			e.setLine(1, "§2§lLEAGUE");
			e.setLine(2, "§fRecraft");
			e.setLine(3, "");
			e.getPlayer().sendMessage("§aPlaca criada com sucesso.");
		} else if (e.getLine(0).equalsIgnoreCase("[lavachallenge]")) {
			boolean created = false;

			if (e.getLine(1).equalsIgnoreCase("[facil]")) {
				e.setLine(2, "§aFácil");
				created = true;
			} else if (e.getLine(1).equalsIgnoreCase("[medio]")) {
				e.setLine(2, "§eMédio");
				created = true;
			} else if (e.getLine(1).equalsIgnoreCase("[dificil]")) {
				e.setLine(2, "§cDifícil");
				created = true;
			} else if (e.getLine(1).equalsIgnoreCase("[extreme]")) {
				e.setLine(2, "§4Extreme");
				created = true;
			} else {
				e.getPlayer().sendMessage("§cPlaca inválida.");
			}

			if (created) {
				e.setLine(0, "");
				e.setLine(1, "§6§lLAVACHALLENGE");
				e.getPlayer().sendMessage("§aPlaca criada com sucesso.");
				e.setLine(3, "");
			}
		}
	}

	@EventHandler
	public void onClickInSign(PlayerInteractEvent event) {
		if(event.getAction() == Action.PHYSICAL) return;

		if ((event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (event.getClickedBlock() != null)
				&& ((event.getClickedBlock().getType().equals(Material.WALL_SIGN))
						|| (event.getClickedBlock().getType().equals(Material.SIGN_POST)))) {
			
			Sign sign = (Sign) event.getClickedBlock().getState();
			String[] lines = sign.getLines();
			
			if (lines[2].contains("Recraft")) {
				Inventory inv = event.getPlayer().getServer().createInventory(null, 9 * 3, "Recraft");
				
				ItemBuilder itemBuilder = new ItemBuilder();
				ItemStack bowl = itemBuilder.type(Material.BOWL).amount(64).build(),
						redMushroom = itemBuilder.type(Material.RED_MUSHROOM).amount(64).build(),
						brownMushroom = itemBuilder.type(Material.BROWN_MUSHROOM).amount(64).build();
				
				for (int slot = 0; slot < 27; slot++) {
					
					if (slot <= 8) {
						inv.setItem(slot, bowl);
					} else if (slot <= 17) {
						inv.setItem(slot, redMushroom);
					} else {
						inv.setItem(slot, brownMushroom);
					}
				}

				event.getPlayer().openInventory(inv);
			} else if (lines[2].contains("Sopa")) {
				Inventory inv = event.getPlayer().getServer().createInventory(null, 9 * 4, "Sopas");
				for (ItemStack i : inv.getContents()) {
					if (i == null)
						inv.setItem(inv.firstEmpty(), new ItemStack(Material.MUSHROOM_SOUP));
				}
				event.getPlayer().openInventory(inv);
			}
		}
	}
	
	public Player getKiller(Player morreu) {
		val log = CombatLogManager.getCombatLog(morreu);

		if(log.isFighting()) {
			val killer = log.getCombatLogged();

			CombatLogManager.removeCombatLog(morreu);
			CombatLogManager.removeCombatLog(killer);

			if(killer.isOnline()) return killer;
		}

		return morreu.getKiller();
	}
}