package com.br.guilhermematthew.nowly.commons.bukkit.api.player;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitSettings;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerUpdateTabEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class PlayerAPI {

    public static boolean isRealMovement(final PlayerMoveEvent event) {
        return (event.getFrom().getBlockX() != event.getTo().getBlockX()) || (event.getFrom().getBlockZ() != event.getTo().getBlockZ());
    }

    public static int getXPKill(final Player player, long doubleXPTime) {
        boolean doubleActive = doubleXPTime > System.currentTimeMillis();

        if (BukkitSettings.DOUBLE_XP_OPTION)
            doubleActive = true;

        int xp = CommonsConst.RANDOM.nextInt(16);

        if (xp < 12)
            xp = 12;

        if (doubleActive) {
            xp = xp * 2;
            player.sendMessage(BukkitMessages.KILL_MESSAGE_XP.replace("%quantia%", "" + xp) + " (2x)");
        } else {
            player.sendMessage(BukkitMessages.KILL_MESSAGE_XP.replace("%quantia%", "" + xp));
        }

        return xp;
    }

    public static int getCoinsKill(final Player player, long doubleCoinsTime) {
        boolean doubleActive = doubleCoinsTime > System.currentTimeMillis();

        if (BukkitSettings.DOUBLE_COINS_OPTION)
            doubleActive = true;

        int coins = CommonsConst.RANDOM.nextInt(100);

        if (coins < 80)
            coins = 80;

        if (doubleActive) {
            coins = coins * 2;
            player.sendMessage(BukkitMessages.KILL_MESSAGE_COINS.replace("%quantia%", "" + coins) + " (2x)");
        } else {
            player.sendMessage(BukkitMessages.KILL_MESSAGE_COINS.replace("%quantia%", "" + coins));
        }

        return coins;
    }

    public static int getPing(final Player player) {
        return ((CraftPlayer) player).getHandle().ping;
    }

    public static String getHealth(Player player) {
        return getHealth(player.getHealth());
    }

    public static String getHealth(double health) {
        return NumberFormat.getCurrencyInstance().format(health / 2).replace("$", "").replace("R", "")
                .replace(",", ".");
    }

    public static String getAddress(final Player player) {
        return player.getAddress().getAddress().getHostAddress();
    }

    public static void dropItems(final Player p, final Location l) {
        ArrayList<ItemStack> itens = new ArrayList<>();

        for (ItemStack item : p.getPlayer().getInventory().getContents()) {
            if ((item != null) && (item.getType() != Material.AIR)) {
                if (item.hasItemMeta()) {
                    if ((item.getItemMeta().hasDisplayName()) && (item.getItemMeta().getDisplayName().contains("Kit"))) {
                        continue;
                    }
                    itens.add(item.clone());
                } else {
                    itens.add(item);
                }
            }
        }

        for (ItemStack item : p.getPlayer().getInventory().getArmorContents()) {
            if ((item != null) && (item.getType() != Material.AIR)) {
                itens.add(item.clone());
            }
        }

        if ((p.getPlayer().getItemOnCursor() != null) && (p.getPlayer().getItemOnCursor().getType() != Material.AIR)) {
            itens.add(p.getPlayer().getItemOnCursor().clone());
        }

        dropItems(p, itens, l);
    }

    @SuppressWarnings("deprecation")
    public static void dropItems(Player player, List<ItemStack> itens, final Location location) {
        World world = location.getWorld();

        for (ItemStack item : itens) {
            if ((item != null) && (item.getType() != Material.AIR)) {
                if (item.hasItemMeta()) {
                    world.dropItemNaturally(location, item.clone()).getItemStack().setItemMeta(item.getItemMeta());
                } else {
                    world.dropItemNaturally(location, item);
                }
            }
        }

        player.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
        player.getPlayer().getInventory().clear();
        player.getPlayer().setItemOnCursor(new ItemStack(0));

        itens.clear();
    }

    public static void clearEffects(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    public static boolean isFull(final Inventory inventory) {
        return inventory.firstEmpty() == -1;
    }

    public static void updateTab(final Player player) {
        Bukkit.getPluginManager().callEvent(new PlayerUpdateTabEvent(player));
    }
}