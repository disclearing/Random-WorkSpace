package com.br.guilhermematthew.nowly.commons.bukkit.api.npc.listener;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.api.npc.NPCManager;
import com.br.guilhermematthew.nowly.commons.bukkit.api.npc.api.NPC;
import com.br.guilhermematthew.nowly.commons.bukkit.utility.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class NPCListener implements Listener {

    private static final String LOCKED_TAG = "LOCKED.NPCS.TIME";

    public static void lock(Player player) {
        lock(player, 2000L);
    }

    public static void lock(Player player, Long time) {
        player.setMetadata(LOCKED_TAG, new FixedMetadataValue(BukkitMain.getInstance(), System.currentTimeMillis() + time));
    }

    public static void removeLock(Player player) {
        player.removeMetadata(LOCKED_TAG, BukkitMain.getInstance());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        handleNPC(event.getPlayer(), event.getPlayer().getLocation());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        for (NPC npc : NPCManager.getAllNPCs()) {
            npc.getShown().remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onRealMovement(PlayerMoveEvent event) {
        if(!LocationUtil.isRealMovement(event.getFrom(), event.getTo())) return;

        Player player = event.getPlayer();

        if (isLocked(player)) return;

        handleNPC(player, event.getTo());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();

        if (isLocked(player)) return;
        handleNPC(player, e.getTo());
    }

    public void handleNPC(Player player, Location to) {
        lock(player);

        for (NPC npc : NPCManager.getAllNPCs()) {
            Location location = npc.getLocation();

            if (location.getWorld() != to.getWorld()) continue;

            double distancia = location.distance(to);

            if (distancia <= 80) {
                if (!npc.getShown().contains(player.getUniqueId())) {
                    npc.show(player);
                    npc.getShown().add(player.getUniqueId());
                }
            } else {
                if (npc.getShown().contains(player.getUniqueId())) {
                    npc.hide(player);
                    npc.getShown().remove(player.getUniqueId());
                }
            }
        }

        removeLock(player);
    }

    private boolean isLocked(Player player) {
        if (!player.hasMetadata(LOCKED_TAG)) return false;
        return player.getMetadata(LOCKED_TAG).get(0).asLong() > System.currentTimeMillis();
    }
}