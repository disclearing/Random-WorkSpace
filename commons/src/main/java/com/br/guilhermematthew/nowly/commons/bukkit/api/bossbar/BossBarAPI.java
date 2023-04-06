package com.br.guilhermematthew.nowly.commons.bukkit.api.bossbar;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent.UpdateType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BossBarAPI {

    private static final HashMap<UUID, BossBar> bossBars = new HashMap<>();
    private static boolean initialized = false;

    private static void init() {
        if (initialized) return;

        initialized = true;

        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                if (getBossBars().containsKey(event.getPlayer().getUniqueId())) {
                    getBossBars().get(event.getPlayer().getUniqueId()).destroy();
                    getBossBars().remove(event.getPlayer().getUniqueId());
                }
            }

            @EventHandler
            public void onDeath(PlayerDeathEvent event) {
                if (getBossBars().containsKey(event.getEntity().getUniqueId())) {
                    getBossBars().get(event.getEntity().getUniqueId()).destroy();
                    getBossBars().remove(event.getEntity().getUniqueId());
                }

                if (event.getEntity().getKiller() != null) {
                    if (getBossBars().containsKey(event.getEntity().getKiller().getUniqueId())) {
                        getBossBars().get(event.getEntity().getKiller().getUniqueId()).destroy();
                        getBossBars().remove(event.getEntity().getKiller().getUniqueId());
                    }
                }
            }

            @EventHandler
            public void onUpdate(BukkitUpdateEvent event) {
                if (event.getType() != UpdateType.SEGUNDO) return;

                if (bossBars.size() > 0) {
                    List<UUID> toRemove = new ArrayList<>();

                    for (BossBar boss : bossBars.values()) {
                        if (boss.isCancelar()) {
                            boss.destroy();
                            toRemove.add(boss.getPlayer().getUniqueId());
                        } else {
                            boss.onSecond();
                        }
                    }

                    toRemove.forEach(uuids -> bossBars.remove(uuids));

                    toRemove.clear();
                    toRemove = null;
                }
            }

        }, BukkitMain.getInstance());
    }

    public static void send(Player player, String name, int tempo) {
        if (!initialized) {
            init();
            send(player, name, tempo);
            return;
        }

        if (getBossBars().containsKey(player.getUniqueId())) {
            getBossBars().get(player.getUniqueId()).destroy();
        }

        BossBar bossBar = new BossBar(player, name, true);
        bossBar.setSegundos(tempo);

        getBossBars().put(player.getUniqueId(), bossBar);
    }

    public static void send(Player player, String name) {
        if (!initialized) {
            init();
            send(player, name);
            return;
        }

        if (getBossBars().containsKey(player.getUniqueId())) {
            getBossBars().get(player.getUniqueId()).destroy();
        }

        BossBar bossBar = new BossBar(player, name, false);
        getBossBars().put(player.getUniqueId(), bossBar);
    }

    public static HashMap<UUID, BossBar> getBossBars() {
        return bossBars;
    }
}