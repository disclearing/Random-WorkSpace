package com.br.guilhermematthew.nowly.hardcoregames.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.ClickType;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.MenuClickHandler;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.MenuInventory;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.MenuItem;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.Gamer;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;
import com.br.guilhermematthew.nowly.hardcoregames.manager.structures.StructuresManager;

public class AliveGamers extends MenuInventory {

    private static final int ITEMS_PER_PAGE = 28;
    private static final int PREVIOUS_PAGE_SLOT = 27;
    private static final int NEXT_PAGE_SLOT = 35;
    private static final int CENTER = 31;
    private static final int HEADS_PER_ROW = 7;
	
	public AliveGamers() {
		this(1);
	}
	
    public AliveGamers(int page) {
        this(page, (3 / ITEMS_PER_PAGE) + 1);
    }
    
    public AliveGamers(int page, int maxPages) {
    	super("Jogadores Vivos", 6);
    	
    	List<MenuItem> itens = new ArrayList<>();
    	
    	ItemBuilder itemBuilder = new ItemBuilder();
    	
		for (Player onlines : Bukkit.getOnlinePlayers()) {
			Gamer gamer = GamerManager.getGamer(onlines.getUniqueId());
			
			if (gamer != null) {
				if (gamer.isPlaying()) {
					itens.add(new MenuItem(itemBuilder.type(Material.SKULL_ITEM).durability(3).name(
							"§a" + onlines.getName()).skin(onlines.getName()).lore(
							 "§fKills: §7" + gamer.getKills(), "§fKit(s): §6" + gamer.getKits()).build(), new ClickHandler()));
				}
				gamer = null;
			}
		}
		
        int pageStart = 0;
        int pageEnd = ITEMS_PER_PAGE;
        
        if (page > 1) {
            pageStart = ((page - 1) * ITEMS_PER_PAGE);
            pageEnd = (page * ITEMS_PER_PAGE);
        }
        
        if (pageEnd > itens.size()) {
            pageEnd = itens.size();
        }

        if (page == 1) {
        	setItem(PREVIOUS_PAGE_SLOT, itemBuilder.type(Material.AIR).build());
        } else {
        	setItem(new MenuItem(itemBuilder.type(Material.ARROW).name("§aPágina Anterior").build(),
        			(player, arg1, arg2, arg3, arg4) -> new AliveGamers(page - 1).open(player)), PREVIOUS_PAGE_SLOT);
        }

        if ((itens.size() / ITEMS_PER_PAGE) + 1 <= page) {
        	setItem(NEXT_PAGE_SLOT, itemBuilder.type(Material.AIR).build());
        } else {
        	setItem(new MenuItem(itemBuilder.type(Material.ARROW).name("§aPróxima Página").build(),
        			(player, arg1, arg2, arg3, arg4) -> new AliveGamers(page + 1).open(player)), NEXT_PAGE_SLOT);
        }

        int kitSlot = 10;

        for (int i = pageStart; i < pageEnd; i++) {
             MenuItem item = itens.get(i);
             setItem(item, kitSlot);
             
             if (kitSlot % 9 == HEADS_PER_ROW) {
                 kitSlot += 3;
                 continue;
             }
             
             kitSlot += 1;
        }
        
        if (itens.size() == 0) {
        	setItem(CENTER, itemBuilder.type(Material.REDSTONE_BLOCK).name("§cNenhum jogador vivo.").build());
        }
        
        if (StructuresManager.getFeast().getLocation() != null) {
        	setItem(4, new MenuItem(new ItemBuilder().type(Material.CAKE).name("§aFeast").build(), new ClickHandler()));
        }
        
        itemBuilder = null;
        itens.clear();
        itens = null;
    }
    
    private static class ClickHandler implements MenuClickHandler {
    	
        @Override
        public void onClick(Player player, Inventory inventory, ClickType clickType, ItemStack item, int slot) {
        	if (clickType != ClickType.LEFT) return;
        	
            player.closeInventory();
            
            if (item.getType() == Material.CAKE) {
            	player.sendMessage("§aVocê foi para o feast!");
            	player.teleport(StructuresManager.getFeast().getLocation().clone().add(0.5, 3, 0.5));
            } else {
            	String name = item.getItemMeta().getDisplayName().replace("§a", "");
            	
            	Player target = Bukkit.getPlayer(name);
            	
            	if (target != null) {
                	player.teleport(target.getLocation().clone().add(0, 1.2, 0));
                	player.sendMessage("§aVocê se teleportou para o §f" + target.getName());
                	
                	target = null;
            	} else {
            		player.sendMessage("§cJogador offline!");
            	}
            }
        }
    }
}