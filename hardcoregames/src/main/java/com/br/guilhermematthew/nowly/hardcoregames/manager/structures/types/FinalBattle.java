package com.br.guilhermematthew.nowly.hardcoregames.manager.structures.types;

import java.util.ArrayList;

import com.br.guilhermematthew.nowly.hardcoregames.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitSettings;

public class FinalBattle {

	private final int altura = 128;
	private final int radius = 30;
	
	@SuppressWarnings("deprecation")
	public void create() {
		BukkitSettings.DANO_OPTION = false;
		BukkitSettings.PVP_OPTION = false;

		Location location = Bukkit.getWorld("world").getBlockAt(0, 2, 0).getLocation();
		World world = location.getWorld();

		for (int x = -30; x < 30; x++) {
			 for (int y = -1; y < 128; y++) {
				  for (int z = -30; z < 30; z++) {
					  location.clone().add(x, y, z).getBlock().setType(Material.AIR);

					   // WorldEditAPI.setAsyncBlock(world, location.add(x, y, z), Material.AIR.getId());
				  }
			 }
		}

		for (int x = -30; x <= 30; x++)
			if  ((x == -30) || (x == 30))
				for (int z = -30; z <= 30; z++)
					for (int y = 0; y <= 150; y++) {
						world.getBlockAt(x, y, z).setType(Material.BEDROCK);
					}

		for (int z = -30; z <= 30; z++)
			if  ((z == -30) || (z == 30))
				for (int x = -30; x <= 30; x++)
					for (int y = 0; y <= 150; y++) {
						world.getBlockAt(x, y, z).setType(Material.BEDROCK);
					}

		final Location loc = world.getBlockAt(0, 5, 0).getLocation();
		
		final ArrayList<Player> players = (ArrayList<Player>) world.getPlayers();
		
		new BukkitRunnable() {
			
			int teleporteds = 0;
			final int toTeleport = players.size();
			
			public void run() {
				if (teleporteds > toTeleport) {
					cancel();
					BukkitSettings.DANO_OPTION = true;
					BukkitSettings.PVP_OPTION = true;
					return;
				}
				
				try {
					Player target = players.get(teleporteds);
					target.setFallDistance(-5);
					target.setNoDamageTicks(30);
					
					target.teleport(loc);
				} catch (Exception ex) {}
				
				teleporteds++;
			}
		}.runTaskTimer(BukkitMain.getInstance(), 2L, 2L);

		Bukkit.broadcastMessage(StringUtils.ARENA_FINAL_SPAWNED);
	}
}