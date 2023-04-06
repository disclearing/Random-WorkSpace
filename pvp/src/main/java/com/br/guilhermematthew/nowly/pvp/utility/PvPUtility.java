package com.br.guilhermematthew.nowly.pvp.utility;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.api.player.PlayerAPI;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.pvp.PvPMain;
import com.br.guilhermematthew.nowly.pvp.StringUtils;

public class PvPUtility {
	
	public static void dropItens(Player player, Location loc) {
		ArrayList<ItemStack> itens = new ArrayList<>();
		
	    for (ItemStack item : player.getPlayer().getInventory().getContents()) {
	         if ((item != null) && (item.getType() != Material.AIR)) {
	        	  if (item.getType() == Material.BROWN_MUSHROOM || item.getType() == Material.RED_MUSHROOM || item.getType() == Material.BOWL ||
	        			  item.getType() == Material.MUSHROOM_SOUP) {
	        		  itens.add(item);
	        	  }
	         }
	    }
		
		PlayerAPI.dropItems(player, itens, loc);
		
		itens.clear();
	}
	
	public static void handleStats(final Player killer, final Player died) {
		BukkitPlayer bukkitPlayerDied = BukkitMain.getBukkitPlayer(died.getUniqueId());
		
		if (killer != null) {
			died.sendMessage(StringUtils.VOCE_MORREU_PARA.replace("%nick%", killer.getName()));
			
			checkStreakLose(killer.getName(), died.getName(),
					StringUtils.KILLSTREAK_LOSE, bukkitPlayerDied.getInt(DataType.PVP_KILLSTREAK));
		} else {
			died.sendMessage(StringUtils.VOCE_MORREU);
		}
		
		died.sendMessage(BukkitMessages.DEATH_MESSAGE_XP.replace("%quantia%", "2"));
		died.sendMessage(BukkitMessages.DEATH_MESSAGE_COINS.replace("%quantia%", "10"));
		
		bukkitPlayerDied.add(DataType.PVP_DEATHS);	
		bukkitPlayerDied.remove(DataType.COINS, 10);
		bukkitPlayerDied.removeXP(2);
		bukkitPlayerDied.set(DataType.PVP_KILLSTREAK, 0);
		
		if (killer == null) {
			PvPMain.runAsync(() -> bukkitPlayerDied.getDataHandler().saveCategorys(DataCategory.ACCOUNT, DataCategory.KITPVP));
			return;
		}
		
		BukkitPlayer bukkitPlayerKiller = BukkitMain.getBukkitPlayer(killer.getUniqueId());
		
		killer.sendMessage(StringUtils.VOCE_MATOU.replace("%nick%", died.getName()));
		
		final int xp = PlayerAPI.getXPKill(killer, bukkitPlayerKiller.getLong(DataType.DOUBLEXP_TIME)),
				coins = PlayerAPI.getCoinsKill(killer, bukkitPlayerKiller.getLong(DataType.DOUBLECOINS_TIME));
		
		bukkitPlayerKiller.add(DataType.COINS, coins);
		bukkitPlayerKiller.addXP(xp);
		bukkitPlayerKiller.add(DataType.PVP_KILLS);
		
		int atualKillStreak = bukkitPlayerKiller.getInt(DataType.PVP_KILLSTREAK) + 1;
		bukkitPlayerKiller.set(DataType.PVP_KILLSTREAK, atualKillStreak);
		
		if (atualKillStreak > bukkitPlayerKiller.getInt(DataType.PVP_MAXKILLSTREAK)) {
			bukkitPlayerKiller.set(DataType.PVP_MAXKILLSTREAK, atualKillStreak);
		}
		
		checkStreakWin(killer.getName(), atualKillStreak, StringUtils.KILLSTREAK_WIN);
		
		PvPMain.runAsync(() -> {
			bukkitPlayerKiller.getDataHandler().saveCategorys(DataCategory.ACCOUNT, DataCategory.KITPVP);
			bukkitPlayerDied.getDataHandler().saveCategorys(DataCategory.ACCOUNT, DataCategory.KITPVP);
		});
	}
	
	public static void checkStreakWin(String nick, int value, String message) {
        if (value >= 10 && value % 10 == 0) {
        	Bukkit.broadcastMessage(message.replace("%nick%", nick).replace("%valor%", "" + value));
        }
    }
	
	public static void checkStreakLose(String killer, String loser, String message, int winstreak) {
		if (winstreak >= 10) {
			Bukkit.broadcastMessage(message.replace("%loser%", loser).replace("%killer%", killer).replace("%valor%", "" + winstreak));
		}
	}

	public static void repairArmor(Player killer) {
		killer.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
		killer.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
	   	killer.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
	   	killer.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
	}
}