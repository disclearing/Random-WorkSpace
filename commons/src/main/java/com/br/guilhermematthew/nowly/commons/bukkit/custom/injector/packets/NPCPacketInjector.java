package com.br.guilhermematthew.nowly.commons.bukkit.custom.injector.packets;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.api.npc.NPCManager;
import com.br.guilhermematthew.nowly.commons.bukkit.api.npc.api.NPC;
import com.br.guilhermematthew.nowly.commons.bukkit.api.npc.events.NPCInteractEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.api.npc.events.click.ClickType;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.injector.PacketObject;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.injector.listener.PacketListener;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.injector.listener.PacketListenerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.utility.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class NPCPacketInjector {

    private static final Class<?> packetPlayInUseEntityClazz = Reflection.getMinecraftClass("PacketPlayInUseEntity");

    @SuppressWarnings("rawtypes")
    private static final Reflection.FieldAccessor entityIdField = Reflection.getField(packetPlayInUseEntityClazz, "a", int.class);

    @SuppressWarnings("rawtypes")
    private static final Reflection.FieldAccessor actionField = Reflection.getField(packetPlayInUseEntityClazz, "action", Object.class);

    private static final String DELAY_TIME_TAG = "DELAY.NPC";

    public static void inject(Plugin plugin) {
        PacketListenerAPI.addListener(new PacketListener() {

            public void onPacketReceiving(PacketObject packetObject) {
                if (packetPlayInUseEntityClazz.isInstance(packetObject.getPacket())) {

                    NPC npc = NPCManager.getAllNPCs().stream().filter(
                                    check -> check.getEntityId() == (int) entityIdField.get(packetObject.getPacket()))
                            .findFirst().orElse(null);

                    if (npc == null) {
                        return;
                    }

                    if (hasDelay(packetObject.getPlayer())) {
                        return;
                    }

                    addDelay(packetObject.getPlayer());

                    ClickType clickType = actionField.get(packetObject.getPacket()).toString()
                            .equals("ATTACK") ? ClickType.LEFT_CLICK : ClickType.RIGHT_CLICK;

                    Bukkit.getPluginManager().callEvent(new NPCInteractEvent(packetObject.getPlayer(), clickType, npc));
                }
            }

            public void onPacketSending(PacketObject packetObject) {
            }
        });
    }

    private static void addDelay(Player player) {
        player.setMetadata(DELAY_TIME_TAG, new FixedMetadataValue(BukkitMain.getInstance(), System.currentTimeMillis() + 700));
    }

    private static boolean hasDelay(Player player) {
        if (!player.hasMetadata(DELAY_TIME_TAG)) return false;
        return player.getMetadata(DELAY_TIME_TAG).get(0).asLong() > System.currentTimeMillis();
    }
}