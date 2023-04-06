package com.br.guilhermematthew.nowly.commons.bukkit.queue.player;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PlayerQueue {

    public Player player;
    public String server;

    public PlayerQueue(Player player, String server) {
        this.player = player;
        this.server = server;
    }

    public PlayerQueue(Player player) {
        this(player, "");
    }

    public void destroy() {
        this.player = null;
        this.server = null;
    }
}