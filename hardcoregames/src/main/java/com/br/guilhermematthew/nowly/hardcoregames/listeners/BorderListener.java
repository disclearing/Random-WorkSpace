package com.br.guilhermematthew.nowly.hardcoregames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent.UpdateType;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.enums.GameStages;
import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.Gamer;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;
import com.br.guilhermematthew.nowly.hardcoregames.utility.HardcoreGamesUtility;

public class BorderListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onUpdate(BukkitUpdateEvent event) {
		if (event.getType() != UpdateType.SEGUNDO) return;
		
		GameStages stage = HardcoreGamesMain.getGameManager().getStage();
		
		for (Player onlines : Bukkit.getOnlinePlayers()) {
			 if (stage == GameStages.WAITING) {
				 if (isNotInBoard(onlines.getLocation(), 120)) {
					 onlines.teleport(HardcoreGamesUtility.getRandomLocation(40));
				 }
			 } else if (stage == GameStages.INVINCIBILITY) { 
				 handleBoardInvincibility(onlines);
			 } else {
				 handleBoard(onlines);
			 }
		}
		
		stage = null;
	}
	
	private void handleBoardInvincibility(Player player) {
		 if (isNotInBoard(player.getLocation(), 401)) {
			 
			 Gamer gamer = GamerManager.getGamer(player.getUniqueId());
			
			 if (gamer != null) {
				 
				 if (gamer.isPlaying()) {
					 player.sendMessage("§aVocê passou da borda e automaticamente foi desclassificado da partida!");
					 
					 player.teleport(HardcoreGamesUtility.getRandomLocation(160));
					 
					 HardcoreGamesMain.getGameManager().getGameType().setEspectador(player);
					 
					 if (!HardcoreGamesUtility.availableToSpec(player)) {
						 BukkitServerAPI.redirectPlayer(player, "LobbyHG", true);
					 }
				 } else {
					 player.teleport(HardcoreGamesUtility.getRandomLocation(160));
				 }
				 
				 gamer = null;
			 } else {
				 player.kickPlayer("WTF? #1");
			 }
		 }
	}

	private void handleBoard(Player player) {
		 if (isNotInBoard(player.getLocation(), 400)) {
			 if (inLauncher(player)) return;
			 
			 Gamer gamer = GamerManager.getGamer(player.getUniqueId());
			 
			 if (gamer != null) {
				 if (gamer.isPlaying()) {
					 player.setFireTicks(100);
					 player.damage(3.0D);
					 player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20, 2));
			 	 } else {
			 		 player.teleport(HardcoreGamesUtility.getRandomLocation(160));
				 }
				
				 gamer = null;
			 } else {
				 player.kickPlayer("WTF? #2");
			 }
		}
	}
	
	private boolean inLauncher(final Player player) {
		if (!player.hasMetadata("nofall")) return false;
			
		Long time = player.getMetadata("nofall.time").get(0).asLong();
        if (time + 6200 > System.currentTimeMillis()) {
        	return true;
        }
        return false;
	}
	
	private boolean isNotInBoard(final Location loc, final int size) {
		return ((loc.getBlockX() > size) || (loc.getBlockX() < -size)
				|| (loc.getBlockZ() > size) || (loc.getBlockY() > 128) || (loc.getBlockZ() < -size));
	}
}