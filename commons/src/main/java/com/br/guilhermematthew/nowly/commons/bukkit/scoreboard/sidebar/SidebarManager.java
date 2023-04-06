package com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.sidebar;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class SidebarManager {

    private static final HashMap<UUID, Sidebar> sidebars = new HashMap<>();

    public static void handleJoin(final Player player) {
        sidebars.put(player.getUniqueId(), new Sidebar(player.getScoreboard()));
    }

    public static Sidebar getSidebar(final Player player) {
        return sidebars.get(player.getUniqueId());
    }

    public static Sidebar getSidebar(final UUID uniqueId) {
        return sidebars.get(uniqueId);
    }

    public static void handleQuit(UUID uniqueId) {
        sidebars.remove(uniqueId);
    }
}