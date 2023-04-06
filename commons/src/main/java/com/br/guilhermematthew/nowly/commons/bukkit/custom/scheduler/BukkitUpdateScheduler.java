package com.br.guilhermematthew.nowly.commons.bukkit.custom.scheduler;

import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent.UpdateType;
import org.bukkit.Bukkit;

public class BukkitUpdateScheduler implements Runnable {

    private static Integer currentTick = 0;

    public static Integer getCurrentTick() {
        return currentTick;
    }

    @Override
    public void run() {
        currentTick++;

        Bukkit.getPluginManager().callEvent(new BukkitUpdateEvent(UpdateType.TICK, currentTick));

        if (currentTick % 20 == 0) {
            Bukkit.getPluginManager().callEvent(new BukkitUpdateEvent(UpdateType.SEGUNDO, currentTick));
        }

        if (currentTick % 1200 == 0) {
            Bukkit.getPluginManager().callEvent(new BukkitUpdateEvent(UpdateType.MINUTO, currentTick));
        }
    }
}