package com.br.guilhermematthew.nowly.commons.bukkit.api.item;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ActionItemStack.InteractHandler;
import com.br.guilhermematthew.nowly.commons.bukkit.listeners.CoreListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ActionItemListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null)
            return;

        ItemStack itemStack = event.getItem();

        try {
            if (itemStack == null || itemStack.getType() == Material.AIR)
                throw new Exception();

            Player player = event.getPlayer();
            Action action = event.getAction();

            if (itemStack.getType() == Material.MUSHROOM_SOUP && action.name().contains("RIGHT")) {
                event.setCancelled(CoreListener.handleSoup(player));
                throw new Exception();
            }


            if (!itemStack.hasItemMeta()) {
                throw new Exception();
            }

            final String displayName = itemStack.getItemMeta().getDisplayName();
            InteractHandler handler = ActionItemStack.getHandler(itemStack.getItemMeta().getDisplayName());

            if (handler == null) {
                throw new NullPointerException("Handler com o nome " + displayName + " com InteractHandler nulo!");
            }
            event.setCancelled(handler.onInteract(player, itemStack, action, event.getClickedBlock()));
        } catch (Exception ex) {
        }
    }
}