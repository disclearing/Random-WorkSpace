package com.br.guilhermematthew.nowly.hardcoregames.ability.utility;

import com.br.guilhermematthew.nowly.hardcoregames.ability.register.Gladiator;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GladiatorFight {
	
	private Player[] players;
	private Location[] locations;
	private Block mainBlock;
	private int seconds, id;
	private boolean ended;
	
	public GladiatorFight(Block mainBlock, int id, Player... players) {
		setSeconds(201);
		setEnded(false);
		setId(id);
		
		setPlayers(players);
		
		setMainBlock(mainBlock);

		this.locations = new Location[2];
		
		this.players[0] = players[0];
		this.players[1] = players[1];
		
		this.locations[0] = players[0].getLocation();
		this.locations[1] = players[1].getLocation();
	}

	public void onSecond() {
		if (isEnded()) return;
		
		for (Player player : getPlayers()) {
			 if (!player.isOnline()) continue;
			 
			 if (player.getLocation().getBlockY() > mainBlock.getLocation().getBlockY() + 9) {
				 cancelGlad();
			 } else if (player.getLocation().getBlockY() < mainBlock.getLocation().getBlockY() - 2) {
				 cancelGlad();
			 }
		}
		
		if (getSeconds() == 80) {
			for (Player player : getPlayers()) {
				 if (!player.isOnline()) continue;
				 
				 player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 9999999, 3));
			}
		} else if (this.seconds == 0) {
			cancelGlad();
			return;
		}
		
		this.seconds--;
	}
	
	public void teleportBack() {
		for (Player player : getPlayers()) {
			 if (!player.isOnline()) continue;
			 
			 player.setFallDistance(-5);
			 player.setNoDamageTicks(40);
			 
			 player.teleport(getBackForPlayer(player));
		}
	}
	
	public Location getBackForPlayer(Player player) {
		if (player.getUniqueId().equals(players[0].getUniqueId())) {
			return locations[0];
		} else {
			return locations[1];
		}
	}
	
	public void cancelGlad() {
		this.ended = true;
		
		for (Player player : getPlayers()) {
			 if (!player.isOnline()) continue;
			 
			 if (player.hasPotionEffect(PotionEffectType.WITHER)) {
				 player.removePotionEffect(PotionEffectType.WITHER);
			 }
			 
			 Gladiator.gladArena.remove(player.getUniqueId());
		}
		
		teleportBack();
		
		Gladiator.removerBlocos(mainBlock.getLocation());
		
		Gladiator.gladiatorFights.remove("arena" + id);
	}
}