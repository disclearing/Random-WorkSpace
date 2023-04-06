package com.br.guilhermematthew.nowly.pvp.menu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.br.guilhermematthew.nowly.pvp.ability.Kit;
import com.br.guilhermematthew.nowly.pvp.mode.arena.ArenaScoreboard;
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
import com.br.guilhermematthew.nowly.pvp.StringUtils;
import com.br.guilhermematthew.nowly.pvp.manager.gamer.Gamer;
import com.br.guilhermematthew.nowly.pvp.manager.gamer.GamerManager;
import com.br.guilhermematthew.nowly.pvp.manager.kit.KitManager;
import com.br.guilhermematthew.nowly.pvp.menu.enums.InventoryMode;

public class KitSelector extends MenuInventory {

	private static final int ITEMS_PER_PAGE = 21;
	private static final int PREVIOUS_PAGE_SLOT = 45;
	private static final int NEXT_PAGE_SLOT = 53;
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

		Gamer gamer = GamerManager.getGamer(player1.getUniqueId());

		List<Kit> kitList = new ArrayList<>(KitManager.getKits().values());

		kitList.sort(Comparator.comparing(Kit::getName));

		List<MenuItem> itens = new ArrayList<>();

		ItemBuilder itemBuilder = new ItemBuilder();

		for (Kit kit : kitList) {
			List<String> lore = new ArrayList<>();

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

						lore.add("");
						if (isOp) {
							lore.add("§cEsta combinaçăo de Kit está bloqueada.");
							itens.add(new MenuItem(
									itemBuilder.type(Material.STAINED_GLASS_PANE).durability(14)
											.name("§c" + kit.getName()).lore(lore).build(),
									new KitSelectHandler(kit.getName(), inventoryMode)));
						} else {
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
					(player, arg1, arg2, arg3, arg4) -> new KitSelector(player, page - 1, inventoryMode).open(player)),
					PREVIOUS_PAGE_SLOT);
		}

		if ((itens.size() / ITEMS_PER_PAGE) + 1 <= page) {
			setItem(NEXT_PAGE_SLOT, itemBuilder.type(Material.AIR).build());
		} else {
			setItem(new MenuItem(itemBuilder.type(Material.ARROW).name("§aPróxima Página").build(),
					(player, arg1, arg2, arg3, arg4) -> new KitSelector(player, page + 1, inventoryMode).open(player)),
					NEXT_PAGE_SLOT);
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
			setItem(CENTER,
					itemBuilder.type(Material.REDSTONE_BLOCK)
							.name("§c" + (inventoryMode == InventoryMode.LOJA ? "Nenhum Kit para comprar!"
									: "Nenhum kit para selecionar!"))
							.build());
		}

		if (inventoryMode != InventoryMode.LOJA) {
			Kit kitSelected = null;

			if (inventoryMode == InventoryMode.KIT_PRIMARIO) {
				if (!gamer.getKit1().equalsIgnoreCase("Nenhum")) {
					kitSelected = KitManager.getKitInfo(gamer.getKit1());
				}
			} else {
				if (!gamer.getKit2().equalsIgnoreCase("Nenhum")) {
					kitSelected = KitManager.getKitInfo(gamer.getKit2());
				}
			}

			setItem(49,
					kitSelected == null
							? itemBuilder.type(Material.SLIME_BALL).name("§cNenhum Kit Selecionado").build()
							: itemBuilder.type(kitSelected.getIcon().getType())
									.name("§aKit Atual: " + kitSelected.getName()).build());

		}

		itens.clear();
		kitList.clear();
	}

	public static boolean continuePlayer(final Player player, final String kit, final InventoryMode modo) {
		if(modo == InventoryMode.KIT_PRIMARIO) return true;
		if(modo == InventoryMode.LOJA) return !KitManager.hasPermissionKit(player, kit, false);

		return KitManager.hasPermissionKit(player, kit, false);
	}

	private static class LojaKitsHandler implements MenuClickHandler {

		private final Kit kit;

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

				bukkitPlayer.sendPacket(new CPacketCustomAction(bukkitPlayer.getNick(), bukkitPlayer.getUniqueId()).type(PacketType.BUKKIT_SEND_INFO).
						field("add-perm").fieldValue("pvp.kit." + kit.getName().toLowerCase()));

				player.sendMessage(StringUtils.VOCE_COMPROU_O_KIT.replace("%kit%", kit.getName()));
				player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
			} else {
				player.sendMessage(StringUtils.VOCE_NAO_TEM_COINS_O_SUFICIENTE_PARA_COMPRAR_O_KIT.replace("%valor%",
						StringUtility.formatValue((kit.getPrice() - bukkitPlayer.getInt(DataType.COINS)))));
			}
		}
	}

	private static class KitSelectHandler implements MenuClickHandler {

		private final String kit;
		private final InventoryMode inventoryMode;

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

			if (inventoryMode == InventoryMode.KIT_SECUNDARIO && !KitManager.hasPermissionKit(player, kit, true)) {
				return;
			}

			Gamer gamer = GamerManager.getGamer(player.getUniqueId());

			if (inventoryMode == InventoryMode.KIT_PRIMARIO) {
				if (!gamer.getKit1().equalsIgnoreCase(kit)) {
					if (!KitManager.isSameKit(kit, gamer.getKit2())) {
						if (!KitManager.hasCombinationOp(kit, gamer.getKit2())) {
							gamer.setKit1(kit);

							ArenaScoreboard.updateScoreboard(player);

							player.sendMessage(StringUtils.VOCE_SELECIONOU_O_KIT_PRIMARIO.replace("%kit%", kit));
						} else {
							player.sendMessage(StringUtils.VOCE_NAO_PODE_USAR_ESTA_COMBINAÇĂO_DE_KIT);
						}
					} else {
						player.sendMessage(StringUtils.VOCE_NAO_PODE_USAR_O_MESMO_KIT);
					}
				} else {
					player.sendMessage(StringUtils.VOCE_NAO_PODE_USAR_O_MESMO_KIT);
				}
			} else {
				if (!gamer.getKit2().equalsIgnoreCase(kit)) {
					if (!KitManager.isSameKit(kit, gamer.getKit1())) {
						if (!KitManager.hasCombinationOp(kit, gamer.getKit1())) {
							gamer.setKit2(kit);

							ArenaScoreboard.updateScoreboard(player);
							
							player.sendMessage(StringUtils.VOCE_SELECIONOU_O_KIT_SECUNDARIO.replace("%kit%", kit));
						} else {
							player.sendMessage(StringUtils.VOCE_NAO_PODE_USAR_ESTA_COMBINAÇĂO_DE_KIT);
						}
					} else {
						player.sendMessage(StringUtils.VOCE_NAO_PODE_USAR_O_MESMO_KIT);
					}
				} else {
					player.sendMessage(StringUtils.VOCE_NAO_PODE_USAR_O_MESMO_KIT);
				}
			}
		}
	}
}