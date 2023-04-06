package com.br.guilhermematthew.nowly.commons.bukkit.custom.injector;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

@Getter
@Setter
public class PacketObject {

    private boolean cancelled;
    private Player player;
    private Channel channel;

    @SuppressWarnings("rawtypes")
    private Packet packet;

    @SuppressWarnings("rawtypes")
    public PacketObject(Player player, Channel channel, Packet packet) {
        this.player = player;
        this.channel = channel;
        this.packet = packet;
        this.cancelled = false;
    }
}