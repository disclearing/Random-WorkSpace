package com.br.guilhermematthew.nowly.pvp.mode.lavachallenge;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.api.player.PlayerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.manager.configuration.PluginConfiguration;
import com.br.guilhermematthew.nowly.pvp.PvPMain;

import lombok.Getter;
import lombok.Setter;

public class LavaChallengeMode {

	@Getter @Setter
	private static Location spawn;
	
	public static HashMap<UUID, Integer> lavaDamages;
	
	public static void init() {
		lavaDamages = new HashMap<>();
		
		setSpawn(PluginConfiguration.createLocation(PvPMain.getInstance(), "spawn"));
		
		Bukkit.getServer().getPluginManager().registerEvents(new LavaChallengeListeners(), PvPMain.getInstance());
	}
	
	public static void refreshPlayer(Player player) {
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
		
		PlayerInventory playerInventory = player.getInventory();
		
		ItemBuilder itemBuilder = new ItemBuilder();
		
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
        
        lavaDamages.put(player.getUniqueId(), 0);

    	LavaChallengeScoreboard.updateScoreboard(player);
	}
	
	public static void updateDifficulty(int minDamageFacil, int minDamageMedio, int minDamageDificil, int minDamageExtremo) {
		LavaDifficulty.FACIL.setMinHits(minDamageFacil);
		LavaDifficulty.MEDIO.setMinHits(minDamageMedio);
		LavaDifficulty.DIFICIL.setMinHits(minDamageDificil);
		LavaDifficulty.EXTREMO.setMinHits(minDamageExtremo);
	}
	
	public enum LavaDifficulty {
		FACIL(20),
		MEDIO(20),
		DIFICIL(20),
		EXTREMO(20);
		
		@Getter @Setter
		int minHits;
		
		LavaDifficulty(int minHits) {
			this.minHits = minHits;
		}
	}
	
	public static void cleanPlayer(Player player) {
		if (lavaDamages.containsKey(player.getUniqueId())) {
			lavaDamages.remove(player.getUniqueId());
		}
	}
}