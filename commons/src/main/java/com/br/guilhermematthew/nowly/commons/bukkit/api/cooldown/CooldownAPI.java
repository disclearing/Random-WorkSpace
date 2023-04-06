package com.br.guilhermematthew.nowly.commons.bukkit.api.cooldown;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.api.actionbar.ActionBarAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.cooldown.types.Cooldown;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent.UpdateType;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownAPI {

    private static final Map<UUID, List<Cooldown>> map = new ConcurrentHashMap<>();
    private static boolean registred = false;

    private static void registerListener() {
        if (registred) return;

        registred = true;

        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onUpdate(BukkitUpdateEvent event) {
                if (event.getType() != UpdateType.TICK) return;
                if (event.getCurrentTick() % 2 > 0) return;

                for (UUID uuid : map.keySet()) {
                    Player player = Bukkit.getPlayer(uuid);

                    if (player != null) {
                        List<Cooldown> list = map.get(uuid);
                        Iterator<Cooldown> it = list.iterator();

                        Cooldown found = null;
                        while (it.hasNext()) {
                            Cooldown cooldown = it.next();
                            if (!cooldown.expired()) {
                                found = cooldown;
                                continue;
                            }
                            it.remove();
                            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1F, 1F);
                        }

                        if (found != null && found.isBarAPI()) {
                            display(player, found);
                        } else if (list.isEmpty()) {
                            ActionBarAPI.send(player, "");
                            map.remove(uuid);
                        }
                    }
                }
            }
        }, BukkitMain.getInstance());
    }

    private static void display(Player player, Cooldown cooldown) {
        StringBuilder bar = new StringBuilder();
        double percentage = cooldown.getPercentage();
        double remaining = cooldown.getRemaining();
        double count = 20 - Math.max(percentage > 0D ? 1 : 0, percentage / 5);

        for (int a = 0; a < count; a++)
            bar.append("§a" + ":");

        for (int a = 0; a < 20 - count; a++)
            bar.append("§c" + ":");

        String name = cooldown.getName();

        ActionBarAPI.send(player, name + " " + bar + "§f " + String.format(Locale.US, "%.1fs", remaining));
    }

    @SuppressWarnings("unused")
    public static void removeAllCooldowns(Player player) {
        if (map.containsKey(player.getUniqueId())) {
            List<Cooldown> list = map.get(player.getUniqueId());
            Iterator<Cooldown> it = list.iterator();
            while (it.hasNext()) {
                Cooldown cooldown = it.next();
                it.remove();
            }
        }
    }

    public static void sendMessage(Player player, String name) {
        if (map.containsKey(player.getUniqueId())) {
            List<Cooldown> list = map.get(player.getUniqueId());
            for (Cooldown cooldown : list)
                if (cooldown.getName().equals(name)) {
                    player.sendMessage(BukkitMessages.COOLDOWN_MESSAGE.replace("%tempo%", StringUtility.toMillis(cooldown.getRemaining())));
                }
        }
    }

    public static void addCooldown(Player player, Cooldown cooldown) {
        if (!registred) {
            registerListener();
        }

        List<Cooldown> list = map.computeIfAbsent(player.getUniqueId(), v -> new ArrayList<>());
        list.add(cooldown);
    }

    public static boolean removeCooldown(Player player, String name) {
        if (map.containsKey(player.getUniqueId())) {
            List<Cooldown> list = map.get(player.getUniqueId());
            Iterator<Cooldown> it = list.iterator();
            while (it.hasNext()) {
                Cooldown cooldown = it.next();
                if (cooldown.getName().equals(name)) {
                    it.remove();
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasCooldown(Player player, String name) {
        if (map.containsKey(player.getUniqueId())) {
            List<Cooldown> list = map.get(player.getUniqueId());
            for (Cooldown cooldown : list)
                if (cooldown.getName().equals(name)) {
                    return true;
                }
        }
        return false;
    }
}