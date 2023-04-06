package com.br.guilhermematthew.nowly.commons.bukkit.listeners;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerChangeTagEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.sidebar.SidebarManager;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.tag.TagManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        SidebarManager.handleJoin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        Scoreboard board = event.getPlayer().getScoreboard();

        if (board != null) {
            for (Team t : board.getTeams()) t.unregister();
            for (Objective ob : board.getObjectives()) ob.unregister();
        }

        SidebarManager.handleQuit(event.getPlayer().getUniqueId());

        event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

        TagManager.removePlayerTag(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerChangeTagCancelEvent(PlayerChangeTagEvent event) {
        if (!TagManager.USE_TAGS) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();

        if (event.isCancelled()) {
            player.sendMessage(BukkitMessages.VOCE_NAO_PODE_TROCAR_A_TAG);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChangeTag(PlayerChangeTagEvent event) {
        Player player = event.getPlayer();

        if (player == null) return;

        if (!event.isForced()) {
            player.sendMessage(BukkitMessages.TAG_SELECIONADA.
                    replace("%tag%", event.getNewTag().getColor() + event.getNewTag().getName()));
        }

        TagManager.setTag(player, event.getNewTag());
    }
}