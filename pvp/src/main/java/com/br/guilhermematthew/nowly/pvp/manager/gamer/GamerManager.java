package com.br.guilhermematthew.nowly.pvp.manager.gamer;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import lombok.Getter;

public class GamerManager {

	@Getter
	private static HashMap<UUID, Gamer> gamers = new HashMap<>();
	
	public static Gamer getGamer(UUID uniqueId) {
		return gamers.get(uniqueId);
	}
	
	public static Gamer getGamer(Player player) {
		return gamers.get(player.getUniqueId());
	}
	
	public static void addGamer(UUID uniqueId) {
		gamers.put(uniqueId, new Gamer(uniqueId));
	}
	
	public static void removeGamer(UUID uniqueId) {
		gamers.remove(uniqueId);
	}
}