package com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player;

import com.br.guilhermematthew.nowly.commons.common.tag.Tag;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PlayerChangeTagEvent extends PlayerCancellableEvent {

    private final Tag oldTag;
    private final Tag newTag;
    private final boolean forced;

    public PlayerChangeTagEvent(Player p, Tag oldTag, Tag newTag, boolean forced) {
        super(p);
        this.oldTag = oldTag;
        this.newTag = newTag;
        this.forced = forced;
    }
}