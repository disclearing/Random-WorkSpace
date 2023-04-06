package com.br.guilhermematthew.nowly.commons.bungee.scheduler;

import com.br.guilhermematthew.nowly.commons.bungee.events.BungeeUpdateEvent;
import com.br.guilhermematthew.nowly.commons.bungee.events.BungeeUpdateEvent.BungeeUpdateType;
import net.md_5.bungee.BungeeCord;

public class BungeeUpdateScheduler implements Runnable {

    private long seconds;

    @Override
    public void run() {
        BungeeCord.getInstance().getPluginManager().callEvent(new BungeeUpdateEvent(BungeeUpdateType.SEGUNDO, seconds));

        if (seconds % 60 == 0) {
            BungeeCord.getInstance().getPluginManager().callEvent(new BungeeUpdateEvent(BungeeUpdateType.MINUTO, seconds));
        }

        if (seconds % 3600 == 0) {
            BungeeCord.getInstance().getPluginManager().callEvent(new BungeeUpdateEvent(BungeeUpdateType.HORA, seconds));
        }

        seconds++;
    }
}
