package com.br.guilhermematthew.nowly.hardcoregames.manager.kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.br.guilhermematthew.nowly.hardcoregames.StringUtils;
import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesOptions;
import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import com.br.guilhermematthew.nowly.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.hardcoregames.events.player.PlayerRegisterKitEvent;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.Gamer;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class KitManager {
	
	@Getter
	private final static HashMap<String, Kit> kits = new HashMap<>();
	
	@Getter
	private final static ArrayList<String> kitsDesativados = new ArrayList<>();
	
	public static List<Kit> getAllKits() {
		return new ArrayList<>(kits.values());
	}
	
	public static List<Kit> getAllKitsAvailables() {
		List<Kit> allKits = new ArrayList<>();
		
		for (Kit kit : kits.values()) {
			if (!getKitsDesativados().contains(kit.getName().toLowerCase())) {
			    allKits.add(kit);
			}
		 }
		
		 return allKits;
	}
	
	public static void disableKit(final String name) {
		if (!getKitsDesativados().contains(name.toLowerCase())) {
			getKitsDesativados().add(name.toLowerCase());
		}
	}
	
	public static void disableKits(final String... kits) {
		for (String kit : kits) {
			 disableKit(kit);
		}
	}
	
	public static void enableKit(final String name) {
		getKitsDesativados().remove(name.toLowerCase());
	}
	
	public static void enableKits(final String... kits) {
		for (String kit : kits) {
			 enableKit(kit);
		}
	}
	
	public static List<Kit> getPlayerKits(Player player) {
		List<Kit> playerKits = new ArrayList<>();
		
		for (Kit kit : getAllKits()) {
			 if (player.hasPermission("hg.kit." + kit.getName().toLowerCase()) || player.hasPermission("hg.kit.all")) {
				 playerKits.add(kit);
			 } else {
				 if (hasPermissionKit(player, kit.getName(), false)) {
					 playerKits.add(kit);
				 }
			 }
		 }
		 return playerKits;
	}
	
	public static Kit getKitInfo(String nome) {
		if (nome.equalsIgnoreCase("Nenhum")) return null; //FAST RESULT
		
		return kits.get(nome.toLowerCase());
	}
	
	public static boolean hasPermissionKit(Player player, String kit, boolean msg) {
		return hasPermissionKit(player, kit, msg, true);
	}
	
	public static boolean hasPermissionKit(Player player, String kit, boolean msg, boolean checkAllFree) {
		if (checkAllFree) if (HardcoreGamesOptions.KITS_FREE) return true;
		
		if (player.hasPermission("hg.kit.all")) return true;
		if (player.hasPermission("hg.kit." + kit.toLowerCase())) return true;
		
		if (msg) {
			player.sendMessage(StringUtils.VOCE_NAO_POSSUI_ESTE_KIT);
		}
		return false;
	}
	
	public static void giveBussola(Player player) {
		if (!player.getInventory().contains(Material.COMPASS)) {
			player.getInventory().addItem(new ItemBuilder().type(Material.COMPASS).name("§Bússola").build());
			player.updateInventory();
		}
	}
	
	public static void give(Player player, String name, boolean firstKit) {
		if (name.equalsIgnoreCase("Nenhum")) return;
		
        Kit playerKit = KitManager.getKitInfo(name);
		 
        if (playerKit != null) {
        	playerKit.cleanPlayer(player);
        	
			if (playerKit.getName().equalsIgnoreCase("Surprise")) {
				playerKit = getRandomAvailableKit();

				if(firstKit) GamerManager.getGamer(player.getUniqueId()).setKit1(playerKit.getName());
				else GamerManager.getGamer(player.getUniqueId()).setKit2(playerKit.getName());
			}

			if (playerKit.getItens() != null) {
				for (ItemStack item : playerKit.getItens()) {
					 player.getInventory().addItem(item);
				}
			}
			
			playerKit.addUsing();
			 
			if (!playerKit.isListenerRegistred()) {
				playerKit.registerListener();
			}
			
			 if (playerKit.isCallEventRegisterPlayer()) {
				 Bukkit.getServer().getPluginManager().callEvent(new PlayerRegisterKitEvent(player, playerKit.getName()));
			 }
		}
	}
    
	public static void removeKits(Player player, boolean setInGamer) {
    	Gamer gamer = GamerManager.getGamer(player.getUniqueId());
    	
        Kit kit1 = getKitInfo(gamer.getKit1()),
        		kit2 = getKitInfo(gamer.getKit2());
        
        if (kit1 != null) kit1.unregisterPlayer(player);
        
        if (kit2 != null) kit2.unregisterPlayer(player);
        
        if (setInGamer) {
        	gamer.setKit1("Nenhum");
        	gamer.setKit2("Nenhum");
        }
	}
	
	public static void removeIfContainsKit(Player player, String kitName) {
    	Gamer gamer = GamerManager.getGamer(player.getUniqueId());
    	
        Kit kit1 = getKitInfo(gamer.getKit1()),
        		kit2 = getKitInfo(gamer.getKit2());
        
        boolean removed = false;
        
        if (kit1 != null && kit1.getName().equalsIgnoreCase(kitName)) {
        	kit1.unregisterPlayer(player);
        	
        	gamer.setKit1("Nenhum");
        	
        	removed = true;
        }
        
        if (kit2 != null && kit2.getName().equalsIgnoreCase(kitName)) {
        	kit2.unregisterPlayer(player);
        	
        	gamer.setKit2("Nenhum");
        	
        	removed = true;
        }
        
        if (removed) {
        	player.sendMessage(StringUtils.SEU_KIT_FOI_DESATIVADO.replace("%kit%", kitName));
        }
	}
	
	public static void giveItensKit(Player player, String kitName) {
		Kit kit = getKitInfo(kitName);
		
		if (kit != null) {
			if (kit.getItens() != null) {
				for (ItemStack item : kit.getItens()) {
					 player.getInventory().addItem(item);
				}
			}
		}
		
		player.updateInventory();
	}
    
	
	//PEGAR KIT APOS A PARTIDA TER INICIADO.
	public static void handleKitSelect(Player player, boolean primary, String kitToSet) {
    	Gamer gamer = GamerManager.getGamer(player.getUniqueId());
    	
    	Kit olderKit = getKitInfo(primary ? gamer.getKit1() : gamer.getKit2()),
    			playerKit = getKitInfo(kitToSet);
    	
    	if (olderKit != null) {
        	if (olderKit.getName().equalsIgnoreCase(kitToSet)) return;
    		olderKit.unregisterPlayer(player);
    		
    		if (olderKit.getItens() != null) {
	        	for (ItemStack item : olderKit.getItens()) {
		        	 if (player.getInventory().contains(item)) {
		        		 player.getInventory().remove(item);
		        	 }
	        	}
    		}
    	}
    	
        if (playerKit != null) {
        	if (kitToSet.equalsIgnoreCase("Surprise")) {
        		playerKit = getRandomAvailableKit();
				kitToSet = playerKit.getName();
        		player.sendMessage(StringUtils.O_KIT_SURPRISE_ESCOLHEU_O_KIT.replace("%kit%", playerKit.getName()));
        	}
        	
        	playerKit.registerPlayer();
        	
        	PlayerInventory playerInventory = player.getInventory();
        	
			if (playerKit.getItens() != null) {
				for (ItemStack item : playerKit.getItens()) {
					 playerInventory.addItem(item);
				}
			}
			
			if (!playerInventory.contains(Material.COMPASS)) {
				playerInventory.addItem(new ItemBuilder().type(Material.COMPASS).name("§6Bússola").build());
			}
        }
        
        if (primary) {
        	gamer.setKit1(kitToSet);
       		HardcoreGamesScoreboard.getScoreBoardCommon().updateKit1(player, kitToSet);
        } else {
        	gamer.setKit2(kitToSet);
    		HardcoreGamesScoreboard.getScoreBoardCommon().updateKit2(player, kitToSet);
        }
	}
	
	private final static String[] combinationsBlockeds = {
			"Stomper-Phantom",
			"Stomper-Tower",
			"Stomper-Jumper",
			"Stomper-Launcher",
			"Stomper-Grappler",
			"Stomper-Flash",
			"Stomper-Kangaroo",
			"Stomper-Ninja",
			"Viking-Urgal",
			"Demoman-Tank",
			"Kangaroo-Grappler",
			"Phantom-Kangaroo",
			"Hulk-Phantom",
			"Fisherman-Magma",
			"Fisherman-Fireman",
			"Fisherman-Demoman",
			"Phantom-Switcher",
			"Gladiator-Ninja",
			"Gladiator-Phantom",
			"Gladiator-Kangaroo",
			"Blink-Stomper",
			""};

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
	
	private static Kit getRandomAvailableKit() {
		val kits = getAllKitsAvailables();

		val kit = kits.get(CommonsConst.RANDOM.nextInt(kits.size()));
		if(kit != null && !kit.getName().equals("Surprise")) return kit;

		return getRandomAvailableKit();
	}
}