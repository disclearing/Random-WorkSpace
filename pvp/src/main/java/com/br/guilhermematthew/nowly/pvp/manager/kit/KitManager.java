package com.br.guilhermematthew.nowly.pvp.manager.kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.br.guilhermematthew.nowly.pvp.ability.Kit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class KitManager {
	
	@Getter
	private static final HashMap<String, Kit> kits = new HashMap<>();
	
	public static void giveKitToPlayer(Player player, String kitName) {
		Kit kit = getKitInfo(kitName);
		
		if (kit != null) {
			kit.registerPlayer();
			
			if (kit.getItens() != null) {
				for (ItemStack item : kit.getItens()) {
					 player.getInventory().addItem(item);
				}
			}
		}
	}
	
	public List<Kit> getAllKits() {
		return new ArrayList<>(kits.values());
	}
	
	public static List<Kit> getPlayerKits(Player player) {
		List<Kit> playerKits = new ArrayList<>();
		
		for (Kit kit : kits.values()) {
			 if (player.hasPermission("pvp.kit." + kit.getName().toLowerCase()) || player.hasPermission("pvp.kit.all")) {
				 playerKits.add(kit);
			 }
		 }
		
		 return playerKits;
	}
	
	public static Kit getKitInfo(String nome) {
		return kits.getOrDefault(nome.toLowerCase(), null);
	}
	
	public static boolean hasPermissionKit(final Player player, String kit, boolean msg) {
		if (kit.equalsIgnoreCase("PvP")) return true;
		
		if (player.hasPermission("pvp.kit.all")) return true;
		
		if (player.hasPermission("pvp.kit." + kit.toLowerCase())) return true;
		
		if (msg) player.sendMessage("§cVocê não possuí este Kit.");
		return false;
	}
	
	private final static String[] combinationsBlockeds = {
			"kangaroo-nofall", "nofall-kangaroo",
			"stomper-switcher", "switcher-stomper",
			"stomper-hulk", "hulk-stomper",
			"grappler-kangaroo", "kangaroo-grappler",
			"ajnin-ninja", "ninja-ajnin"};

	public static boolean isSameKit(String kit, String otherKit) {
		return kit.equalsIgnoreCase(otherKit);
	}
	
	public static boolean hasCombinationOp(String kit, String otherKit) {
		boolean isOp = false;
		
		final String playerCombination1 = kit + "-" + otherKit,
				playerCombination2 = otherKit + "-" + kit;
		
		for (String combinations : combinationsBlockeds) {
			 if (playerCombination1.equalsIgnoreCase(combinations)) {
				 isOp = true;
				 break;
			 }
			 if (playerCombination2.equalsIgnoreCase(combinations)) {
				 isOp = true;
				 break;
			 }
		}
		
		return isOp;
	}
}