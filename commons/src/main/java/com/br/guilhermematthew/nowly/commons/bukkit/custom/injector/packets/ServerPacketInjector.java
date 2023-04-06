package com.br.guilhermematthew.nowly.commons.bukkit.custom.injector.packets;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.injector.PacketObject;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.injector.listener.PacketListener;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.injector.listener.PacketListenerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.injector.protocol.CommonProtocol;
import io.netty.channel.Channel;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Iterator;
import java.util.List;

public class ServerPacketInjector {

    public static void inject(final Plugin plugin) {
        BukkitMain.console("§a[ServerPacketInjector] has been injected!");

        new CommonProtocol(plugin) {

            @SuppressWarnings({"unchecked", "rawtypes"})
            @Override
            public Object onPacketOutAsync(final Player reciever, Channel channel, Object packet) {
                if (!(packet instanceof Packet))
                    return super.onPacketOutAsync(reciever, channel, packet);

                if (channel == null) {
                    return super.onPacketOutAsync(reciever, channel, packet);
                }

                PacketObject object = new PacketObject(reciever, channel, (Packet) packet);

                Iterator<PacketListener> iterator = ((List<PacketListener>) PacketListenerAPI.getListeners().clone()).iterator();
                while (iterator.hasNext()) {
                    iterator.next().onPacketSending(object);
                }

                if (object.isCancelled())
                    return null;
                return object.getPacket();
            }

            @SuppressWarnings({"unchecked", "rawtypes"})
            @Override
            public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
                if (!(packet instanceof Packet))
                    return super.onPacketInAsync(sender, channel, packet);

                if (channel == null) {
                    return super.onPacketInAsync(sender, channel, packet);
                }

                PacketObject object = new PacketObject(sender, channel, (Packet) packet);

                Iterator<PacketListener> iterator = ((List<PacketListener>) PacketListenerAPI.getListeners().clone()).iterator();
                while (iterator.hasNext()) {
                    iterator.next().onPacketReceiving(object);
                }

                if (object.isCancelled())
                    return null;
                return object.getPacket();
            }
        };
    }
}