package com.br.guilhermematthew.nowly.commons.bukkit.api.item;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class ActionItemStack {

    private static HashMap<String, InteractHandler> handlers = null;

    public static void register(final ItemStack itemStack, final InteractHandler handler) {
        register(itemStack.getItemMeta().getDisplayName(), handler);
    }

    public static void register(final String itemName, final InteractHandler handler) {
        if (handlers == null)
            handlers = new HashMap<>();

        handlers.put(itemName, handler);
    }

    public static void unregister(final String itemName) {
        handlers.remove(itemName);
    }

    public static InteractHandler getHandler(final String itemName) {
        return handlers.get(itemName);
    }

    public interface InteractHandler {

        boolean onInteract(Player player, ItemStack itemStack, Action action, Block clickedBlock);
    }
}