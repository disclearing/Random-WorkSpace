package com.br.guilhermematthew.nowly.hardcoregames.manager.structures.types;

import java.util.ArrayList;
import java.util.List;

import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.StringUtils;
import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesOptions;
import com.br.guilhermematthew.nowly.hardcoregames.utility.HardcoreGamesUtility;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.br.guilhermematthew.nowly.hardcoregames.manager.structures.StructuresManager;
import com.br.guilhermematthew.nowly.hardcoregames.utility.schematic.SchematicUtility;

import lombok.Getter;

@Getter
public class Feast {

	private boolean spawned;
	private Location location;
	
	private Location enchantmentTable = null;
	private List<Block> chests = new ArrayList<>();
	
	private Listener listener;
	
	public void createFeast(final Location location) {
		if(isSpawned()) return;

		this.location = location;

		SchematicUtility.spawnarSchematic("feast", location, false);
		
		final int x = location.getBlockX(), 
				y = location.getBlockY(), 
				z = location.getBlockZ();
		
		final String coords = "(" + x + ", " + y + ", " + z + ")";
		
		registerListener();

		val totalTime = HardcoreGamesMain.getTimerManager().getTime().get() + 301;
		new BukkitRunnable() {
			public void run() {
				spawned = true;

				val segundos = totalTime - HardcoreGamesMain.getTimerManager().getTime().get();
				if (!HardcoreGamesOptions.FEAST) {
					cancel();
					Bukkit.broadcastMessage("§cO feast foi cancelado!");
					destroyListener();
					return;
				}

				if (segundos == 300 || segundos == 240 || segundos == 180 || segundos == 120) {
					Bukkit.broadcastMessage(StringUtils.FEAST_SPAWN_IN.replace("%coords%", coords).replace("%tempo%",
							segundos / 60 + " minutos"));

				} else if (segundos == 60) {
					Bukkit.broadcastMessage(StringUtils.FEAST_SPAWN_IN.replace("%coords%", coords).replace("%tempo%", "1 minuto"));
				} else if (segundos == 30 || segundos == 15 || segundos == 10 || (segundos > 1 && segundos <= 5)) {
					Bukkit.broadcastMessage(StringUtils.FEAST_SPAWN_IN.replace("%coords%", coords).replace("%tempo%", segundos + " segundos"));
				} else if (segundos == 1) {
					Bukkit.broadcastMessage(StringUtils.FEAST_SPAWN_IN.replace("%coords%", coords).replace("%tempo%", segundos + " segundo"));
				} else if (segundos <= 0) {
					HardcoreGamesUtility.strikeLightning(location);

					destroyListener();

					fillChests();

					Bukkit.broadcastMessage(StringUtils.FEAST_SPAWNED.replace("%coords%", coords));
					cancel();
				}
			}
		}.runTaskTimer(HardcoreGamesMain.getInstance(), 0, 20);
	}
	
	public void fillChests() {
		if (location == null) return;
			
		for (Block chest : chests) {
			 chest.setType(Material.CHEST);
			  
			 StructuresManager.addChestItens((Chest) chest.getLocation().getBlock().getState());
		}
		
		if (enchantmentTable != null) {
			enchantmentTable.getBlock().setType(Material.ENCHANTMENT_TABLE);
		}
		
		chests.clear();
		chests = null;
	}
	
	public void registerListener() {
		listener = new Listener() {
			
			@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
			public void onPlace(final BlockBreakEvent event) {
				if (enchantmentTable == null) {
					return;
				}
				
				if (event.getBlock().getLocation().distance(enchantmentTable) < 15) {
					event.setCancelled(true);
				}
			}
		};
		
		Bukkit.getServer().getPluginManager().registerEvents(listener, HardcoreGamesMain.getInstance());
	}
	
	public void destroyListener() {
		HandlerList.unregisterAll(listener);
		
		listener = null;
	}

	public void addChest(Block block) {
		chests.add(block);
	}

	public void setEnchantmentTable(Location location) {
		this.enchantmentTable = location;
	}
}