package com.br.guilhermematthew.nowly.commons.bukkit.api.menu;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class MenuListener implements Listener {

    private static Listener listener;

    @Getter
    private static boolean openMenus;

    public static void registerListeners() {
        if (openMenus) return;

        openMenus = true;

        listener = new Listener() {

            @EventHandler(priority = EventPriority.LOWEST)
            public void onInventoryClickListener(InventoryClickEvent event) {
                if (event.getInventory() == null)
                    return;

                Inventory inv = event.getInventory();
                if (inv.getType() != InventoryType.CHEST)
                    return;

                if (inv.getHolder() == null)
                    return;

                if (!(inv.getHolder() instanceof MenuHolder))
                    return;

                event.setCancelled(true);

                if (event.getClickedInventory() != inv)
                    return;

                if (!(event.getWhoClicked() instanceof Player))
                    return;

                if (event.getSlot() < 0)
                    return;

                MenuHolder holder = (MenuHolder) inv.getHolder();
                MenuInventory menu = holder.getMenu();

                if (menu.hasItem(event.getSlot())) {
                    Player p = (Player) event.getWhoClicked();
                    MenuItem item = menu.getItem(event.getSlot());

                    item.getHandler().onClick(p, inv,
                            ((event.getAction() == InventoryAction.PICKUP_HALF) ? ClickType.RIGHT : ClickType.LEFT),
                            event.getCurrentItem(), event.getSlot());

                    p = null;
                    item = null;
                }

                holder = null;
                menu = null;
                inv = null;
            }

            @EventHandler
            public void onClose(InventoryCloseEvent event) {
                if (event.getInventory() == null)
                    return;

                Inventory inv = event.getInventory();
                if (inv.getType() != InventoryType.CHEST)
                    return;

                if (inv.getHolder() == null)
                    return;

                if (!(inv.getHolder() instanceof MenuHolder))
                    return;

                if (!(event.getPlayer() instanceof Player))
                    return;

                MenuHolder holder = (MenuHolder) inv.getHolder();
                if (holder.isOnePerPlayer()) {
                    holder.destroy();

                    holder = null;
                }

                inv = null;
            }
        };

        Bukkit.getServer().getPluginManager().registerEvents(listener, BukkitMain.getInstance());
    }

    public static void unregisterListeners() {
        openMenus = false;

        HandlerList.unregisterAll(listener);

        listener = null;
    }
}