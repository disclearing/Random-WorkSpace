package com.br.guilhermematthew.nowly.hardcoregames.menu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.ClickType;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.MenuClickHandler;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.MenuInventory;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.MenuItem;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import com.br.guilhermematthew.nowly.commons.custompackets.PacketType;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketCustomAction;
import com.br.guilhermematthew.nowly.hardcoregames.StringUtils;
import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesOptions;
import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.Gamer;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;
import com.br.guilhermematthew.nowly.hardcoregames.manager.kit.KitManager;
import com.br.guilhermematthew.nowly.hardcoregames.menu.enums.InventoryMode;

public class KitSelector extends MenuInventory {

    private static final int ITEMS_PER_PAGE = 28;
    private static final int PREVIOUS_PAGE_SLOT = 27;
    private static final int NEXT_PAGE_SLOT = 35;
    private static final int CENTER = 31;

    private static final int KITS_PER_ROW = 7;
    
    public KitSelector(final Player player) {
        this(player, 1, InventoryMode.KIT_PRIMARIO);
    }
    
    public KitSelector(Player player, InventoryMode inventoryMode) {
        this(player, 1, inventoryMode);
    }

    public KitSelector(Player player, int page, InventoryMode inventoryMode) {
        this(player, page, (3 / ITEMS_PER_PAGE) + 1, inventoryMode);
    }
    
    public KitSelector(Player player1, int page, int maxPages, InventoryMode inventoryMode) {
    	super(inventoryMode.getInventoryName(), 6);
    	
        List<Kit> kitList = new ArrayList<>(KitManager.getKits().values());

        kitList.sort(Comparator.comparing(Kit::getName));
    	
    	List<MenuItem> itens = new ArrayList<>();
    	
    	ItemBuilder itemBuilder = new ItemBuilder();
    	
        Gamer gamer = GamerManager.getGamer(player1.getUniqueId());
    	
		for (Kit kit : kitList) {
			List<String> lore = new ArrayList<String>();

			for (String line : kit.getDescription()) {
				lore.add(line.replaceAll("&", "§"));
			}

			if (inventoryMode == InventoryMode.LOJA) {
				if (continuePlayer(player1, kit.getName(), inventoryMode)) {
					lore.add("");
					lore.add("§fCusto: §6" + StringUtility.formatValue(kit.getPrice()) + " coins");
					lore.add("");
					lore.add("§eClique para comprar.");

					itens.add(new MenuItem(
							itemBuilder.type(kit.getIcon().getType()).name("§a" + kit.getName()).lore(lore).build(),
							new LojaKitsHandler(kit)));
				} else {
					lore.add("");
					lore.add("§cVocê já possuí este Kit.");

					itens.add(new MenuItem(itemBuilder.type(Material.STAINED_GLASS_PANE).durability(14)
							.name("§c" + kit.getName()).lore(lore).build(), new LojaKitsHandler(kit)));
				}
			} else {
				if (continuePlayer(player1, kit.getName(), inventoryMode)) {
					boolean isOp = KitManager.hasCombinationOp(
							inventoryMode == InventoryMode.KIT_PRIMARIO ? gamer.getKit2() : gamer.getKit1(),
							kit.getName());

					if (KitManager.isSameKit(
							inventoryMode == InventoryMode.KIT_PRIMARIO ? gamer.getKit2() : gamer.getKit1(),
							kit.getName())) {
						lore.add("");
						lore.add("§cVocê ja escolheu este Kit.");
						itens.add(new MenuItem(
								itemBuilder.type(Material.STAINED_GLASS_PANE).durability(14)
										.name("§c" + kit.getName()).lore(lore).build(),
								new KitSelectHandler(kit.getName(), inventoryMode)));
					} else {

						if (isOp) {
							lore.add("");
							lore.add("§cEsta combinação de Kit está bloqueada.");
							itens.add(new MenuItem(
									itemBuilder.type(Material.STAINED_GLASS_PANE).durability(14)
											.name("§c" + kit.getName()).lore(lore).build(),
									new KitSelectHandler(kit.getName(), inventoryMode)));
						} else {
							lore.add("");
							lore.add("§eClique para selecionar.");
							itens.add(
									new MenuItem(
											itemBuilder.type(kit.getIcon().getType()).name("§a" + kit.getName())
													.lore(lore).build(),
											new KitSelectHandler(kit.getName(), inventoryMode)));
						}
					}
				} else {
					lore.add("");
					lore.add("§cVocê não possui este Kit.");
					itens.add(
							new MenuItem(
									itemBuilder.type(Material.STAINED_GLASS_PANE).durability(14)
											.name("§c" + kit.getName()).lore(lore).build(),
									new KitSelectHandler(kit.getName(), inventoryMode)));
				}
			}

			lore.clear();
			lore = null;
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
        			(player, arg1, arg2, arg3, arg4) -> new KitSelector(player, page - 1, inventoryMode).open(player)), PREVIOUS_PAGE_SLOT);
        }

        if ((itens.size() / ITEMS_PER_PAGE) + 1 <= page) {
        	setItem(NEXT_PAGE_SLOT, itemBuilder.type(Material.AIR).build());
        } else {
        	setItem(new MenuItem(itemBuilder.type(Material.ARROW).name("§aPróxima Página").build(),
        			(player, arg1, arg2, arg3, arg4) -> new KitSelector(player, page + 1, inventoryMode).open(player)), NEXT_PAGE_SLOT);
        }

        int kitSlot = 10;

        for (int i = pageStart; i < pageEnd; i++) {
             setItem(itens.get(i), kitSlot);
             
             if (kitSlot % 9 == KITS_PER_ROW) {
                 kitSlot += 3;
                 continue;
             }
             
             kitSlot += 1;
        }
    	
        if (itens.size() == 0) {
        	setItem(CENTER, itemBuilder.type(Material.REDSTONE_BLOCK).name("§c" + (inventoryMode == InventoryMode.LOJA ? 
        			"Nenhum Kit para comprar!" : "Nenhum kit para selecionar!")).build());
        }
        
        itens.clear();
    	kitList.clear();
    }
  
	public static boolean continuePlayer(final Player player, final String kit, final InventoryMode modo) {
		if(modo == InventoryMode.KIT_PRIMARIO && HardcoreGamesOptions.DOUBLE_KIT) return true;
		if(modo == InventoryMode.LOJA) return !KitManager.hasPermissionKit(player, kit, false);
		return KitManager.hasPermissionKit(player, kit, false);
	}
    
    private static class LojaKitsHandler implements MenuClickHandler {
    	
    	private Kit kit;
    	
        public LojaKitsHandler(Kit kit) {
            this.kit = kit;
        }
    	
        @Override
        public void onClick(Player player, Inventory arg1, ClickType clickType, ItemStack arg3, int arg4) {
        	if (clickType != ClickType.LEFT) {
        		return;
        	}

			if(!continuePlayer(player, kit.getName(), InventoryMode.LOJA)) return;
        	player.closeInventory();
        	
        	BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());

        	if (bukkitPlayer.getInt(DataType.COINS) >= kit.getPrice()) {	
    			bukkitPlayer.getDataHandler().getData(DataType.COINS).remove(kit.getPrice());
				bukkitPlayer.getDataHandler().saveCategory(DataCategory.ACCOUNT);
    			
				bukkitPlayer.sendPacket(new CPacketCustomAction(bukkitPlayer.getNick(), bukkitPlayer.getUniqueId()).type(PacketType.BUKKIT_SEND_INFO).
						field("add-perm").fieldValue("hg.kit." + kit.getName().toLowerCase()));
				
    			player.sendMessage(StringUtils.VOCE_COMPROU_O_KIT.replace("%kit%", kit.getName()));
    			player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        	} else {
				player.sendMessage(StringUtils.VOCE_NAO_TEM_COINS_O_SUFICIENTE_PARA_COMPRAR_O_KIT.replace("%valor%",
						StringUtility.formatValue((kit.getPrice() - bukkitPlayer.getInt(DataType.COINS)))));
        	}
        }
    }
    
    private static class KitSelectHandler implements MenuClickHandler {
    	
    	private String kit;
    	private InventoryMode inventoryMode;
    	
        public KitSelectHandler(String kit, InventoryMode inventoryMode) {
            this.kit = kit;
            this.inventoryMode = inventoryMode;
        }
    	
        @Override
        public void onClick(Player player, Inventory arg1, ClickType clickType, ItemStack arg3, int arg4) {
        	if (clickType != ClickType.LEFT) {
        		return;
        	}
        	
            player.closeInventory();
            player.performCommand("kit" + (inventoryMode == InventoryMode.KIT_PRIMARIO ? " " : "2 ") + kit);
        }
    }
}