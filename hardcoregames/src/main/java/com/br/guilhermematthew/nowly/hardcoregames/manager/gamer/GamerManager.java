package com.br.guilhermematthew.nowly.hardcoregames.manager.gamer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GamerManager {

	private static HashMap<UUID, Gamer> gamers = new HashMap<>();
	
	public static Gamer getGamer(UUID uniqueId) {
		return gamers.get(uniqueId);
	}
	
	public static Gamer getGamer(Player player) {
		return gamers.getOrDefault(player.getUniqueId(), null);
	}
	public static boolean containsGamer(UUID uniqueId) {
		return gamers.containsKey(uniqueId);
	}
	
	public static void addGamer(UUID uniqueId) {
		gamers.put(uniqueId, new Gamer(uniqueId));
	}
	
	public static void removeGamer(UUID uniqueId) {
		gamers.remove(uniqueId);
	}
	
	public static Collection<Gamer> getGamers() {
		return gamers.values();
	}
	
	public static List<Player> getAliveGamers() {
		List<Player> alives = new ArrayList<>();
		
		for (Player onlines : Bukkit.getOnlinePlayers()) {
			 Gamer gamer = getGamer(onlines.getUniqueId());
			 if (gamer == null) continue;
			 
			 if (gamer.isPlaying()) {
				 alives.add(onlines);
			 }
		}
		
		return alives;
	}
	
	public static List<Gamer> getGamersVivos() {
		List<Gamer> vivos = new ArrayList<>();
		
		for (Gamer gamers : getGamers()) {
			 if (gamers.isPlaying()) {
				 vivos.add(gamers);
			 }
		}
		
		return vivos;
	}
	
	public static List<Gamer> getGamersSpecs() {
		List<Gamer> specs = new ArrayList<>();
		
		for (Gamer gamers : getGamers()) {
			 if (!gamers.isPlaying() && gamers.isOnline()) {
				 specs.add(gamers);
			 }
		}
		
		return specs;
	}
	
	public static List<Gamer> getGamersEliminateds() {
		List<Gamer> list = new ArrayList<>();
		
		for (Gamer gamers : getGamers()) {
			 if (gamers.isEliminado()) {
				 list.add(gamers);
			 }
		}
		
		return list;
	}
	
	public static List<Gamer> getGamersToRelog() {
		List<Gamer> list = new ArrayList<>();
		
		for (Gamer gamers : getGamers()) {
			 if (gamers.isRelogar()) {
				 list.add(gamers);
			 }
		}
		
		return list;
	}
}