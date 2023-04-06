package com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player;

import com.br.guilhermematthew.nowly.commons.bukkit.queue.QueueType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@Setter
public class PlayerQueueEvent extends PlayerEvent {

    private QueueType queueType;
    private String response;

    public PlayerQueueEvent(Player player, QueueType queueType) {
        this(player, queueType, "");
    }

    public PlayerQueueEvent(Player player, QueueType queueType, String response) {
        super(player);
        this.queueType = queueType;
        this.response = response;
    }
}