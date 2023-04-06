package com.br.guilhermematthew.nowly.commons.bukkit.api.npc.events;

import com.br.guilhermematthew.nowly.commons.bukkit.api.npc.api.NPC;
import com.br.guilhermematthew.nowly.commons.bukkit.api.npc.events.click.ClickType;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class NPCInteractEvent extends Event {

    private final Player player;
    private final ClickType clickType;
    private final NPC npc;

    private static final HandlerList handlers = new HandlerList();

    public NPCInteractEvent(Player player, ClickType clickType, NPC npc) {
        this.player = player;
        this.clickType = clickType;
        this.npc = npc;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}