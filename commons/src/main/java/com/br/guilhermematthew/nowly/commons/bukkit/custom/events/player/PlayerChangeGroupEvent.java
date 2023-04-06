package com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player;

import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@Setter
public class PlayerChangeGroupEvent extends PlayerEvent {

    private Groups group;

    public PlayerChangeGroupEvent(Player player, final Groups group) {
        super(player);
        setGroup(group);
    }
}