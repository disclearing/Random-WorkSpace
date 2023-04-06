package com.br.guilhermematthew.nowly.commons.bukkit.api.hologram;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.reflection.NMSClass;
import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.reflection.minecraft.DataWatcher;
import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.reflection.minecraft.Minecraft;
import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.reflection.resolver.FieldResolver;
import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.reflection.util.AccessUtil;
import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.touch.TouchAction;
import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.touch.TouchHandler;
import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.view.ViewHandler;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.injector.PacketObject;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.injector.listener.PacketListener;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.injector.listener.PacketListenerAPI;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class HologramInjector {

    private static final FieldResolver DataWatcherFieldResolver = new FieldResolver(NMSClass.DataWatcher);

    private static final String DELAY_TAG = "delay.hologram";

    public static void inject(Plugin plugin) {
        PacketListenerAPI.addListener(new PacketListener() {

            public void onPacketReceiving(PacketObject packetObject) {
                Object packet = packetObject.getPacket();

                if (packet instanceof PacketPlayInUseEntity) {
                    int id = (int) getPacketValue("a", packet);
                    Object useAction = getPacketValue("action", packet);
                    TouchAction action = TouchAction.fromUseAction(useAction);
                    if (action == TouchAction.UNKNOWN) {
                        return;// UNKNOWN means an invalid packet, so just ignore it
                    }

                    if (hasDelay(packetObject.getPlayer())) {
                        packetObject.setCancelled(true);
                        return;
                    }
                    addDelay(packetObject.getPlayer());

                    for (Hologram h : HologramAPI.getHolograms()) {
                        if (((DefaultHologram) h).matchesTouchID(id)) {
                            for (TouchHandler t : h.getTouchHandlers()) {
                                t.onTouch(h, packetObject.getPlayer(), action);
                            }
                        }
                    }
                }
            }

            public void onPacketSending(PacketObject packetObject) {
                Object packet = packetObject.getPacket();

                int type = -1;

                if (packet instanceof PacketPlayOutSpawnEntityLiving) {
                    type = 0;

                }
                if (packet instanceof PacketPlayOutEntityMetadata) {
                    type = 1;
                }

                if (type == 0 || type == 1) {
                    int a = (int) getPacketValue("a", packet);
                    Object dataWatcher =
                            type == 0 ?
                                    (Minecraft.VERSION.olderThan(Minecraft.Version.v1_9_R1) ?
                                            getPacketValue("l", packet) : getPacketValue("m", packet)) : null;

                    if (dataWatcher != null) {
                        try {
                            dataWatcher = cloneDataWatcher(dataWatcher);// Clone the DataWatcher, we don't want to change the name values permanently
                            AccessUtil.setAccessible(Minecraft.VERSION.olderThan(Minecraft.Version.v1_9_R1) ? NMSClass.DataWatcher.getDeclaredField("a") : NMSClass.DataWatcher.getDeclaredField("b")).set(dataWatcher, null);
                            if (Minecraft.VERSION.olderThan(Minecraft.Version.v1_9_R1)) {
                                setPacketValue("l", dataWatcher, packet);
                            } else {
                                setPacketValue("m", dataWatcher, packet);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;// Allowing further changes here would mess up the packet values
                        }
                    }

                    List list = (List) (type == 1 ? getPacketValue("b", packet) : null);
                    int listIndex = -1;

                    String text = null;
                    try {
                        if (type == 0) {
                            //					text = (String) ClassBuilder.getWatchableObjectValue(ClassBuilder.getDataWatcherValue(dataWatcher, 2));
                            if (Minecraft.VERSION.olderThan(Minecraft.Version.v1_9_R1)) {
                                text = (String) DataWatcher.V1_8.getWatchableObjectValue(DataWatcher.V1_8.getValue(dataWatcher, 2));
                            } else {
                                Field dField = AccessUtil.setAccessible(NMSClass.DataWatcher.getDeclaredField("d"));
                                Object dValue = dField.get(dataWatcher);
                                if (dValue == null) {
                                    return;
                                }
                                if (Map.class.isAssignableFrom(dValue.getClass())) {
                                    if (((Map) dValue).isEmpty()) {
                                        return;
                                    }
                                }
                                text = (String) DataWatcher.V1_9.getValue(dataWatcher, DataWatcher.V1_9.ValueType.ENTITY_NAME);
                            }
                        } else if (type == 1) {
                            if (list != null) {
                                if (Minecraft.VERSION.olderThan(Minecraft.Version.v1_9_R1)) {
                                    for (int i = 0; i < list.size(); i++) {
                                        //								int index = ClassBuilder.getWatchableObjectIndex(list.get(i));
                                        int index = DataWatcher.V1_8.getWatchableObjectIndex(list.get(i));
                                        if (index == 2) {
                                            //									if (ClassBuilder.getWatchableObjectType(list.get(i)) == 4) {//Check if it is a string
                                            if (DataWatcher.V1_8.getWatchableObjectType(list.get(i)) == 4) {//Check if it is a string
                                                //										text = (String) ClassBuilder.getWatchableObjectValue(list.get(i));
                                                text = (String) DataWatcher.V1_8.getWatchableObjectValue(list.get(i));
                                                listIndex = i;
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    if (list.size() > 2) {
                                        if (DataWatcher.V1_9.getItemType(list.get(2)) == String.class) {
                                            text = (String) DataWatcher.V1_9.getItemValue(list.get(2));
                                            listIndex = 2;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        if (!HologramAPI.useProtocolSupport) {
                            e.printStackTrace();//Ignore the exception(s) when using protocol support
                        }
                    }

                    if (text == null) {
                        return;// The text will (or should) never be null
                    }

                    for (Hologram h : HologramAPI.getHolograms()) {
                        if (((CraftHologram) h).matchesHologramID(a)) {
                            for (ViewHandler v : h.getViewHandlers()) {
                                text = v.onView(h, packetObject.getPlayer(), text);
                            }
                        }
                    }

                    if (text == null) {
                        packetObject.setCancelled(true);//Cancel the packet if the text is null after calling the view handlers
                        return;
                    }

                    try {
                        if (type == 0) {
                            //					ClassBuilder.setDataWatcherValue(dataWatcher, 2, text);
                            DataWatcher.setValue(dataWatcher, 2, DataWatcher.V1_9.ValueType.ENTITY_NAME, text);
                        } else if (type == 1) {
                            if (list == null || listIndex == -1) {
                                return;
                            }
                            //					Object object = ClassBuilder.buildWatchableObject(2, text);
                            Object object = Minecraft.VERSION.olderThan(Minecraft.Version.v1_9_R1) ? DataWatcher.V1_8.newWatchableObject(2, text) : DataWatcher.V1_9.newDataWatcherItem(DataWatcher.V1_9.ValueType.ENTITY_NAME.getType(), text);
                            list.set(listIndex, object);
                            setPacketValue("b", list, packet);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void setPacketValue(String field, Object value, Object packet) {
        FieldResolver fieldResolver = new FieldResolver(packet.getClass());
        try {
            fieldResolver.resolve(field).set(packet, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getPacketValue(String field, Object packet) {
        FieldResolver fieldResolver = new FieldResolver(packet.getClass());
        try {
            return fieldResolver.resolve(field).get(packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object cloneDataWatcher(Object original) throws Exception {
        if (original == null) {
            return null;
        }
        // Object clone = NMSClass.DataWatcher.getConstructor(new Class[] {
        // NMSClass.Entity }).newInstance(new Object[] { null });
        Object clone = DataWatcher.newDataWatcher(null);
        int index = 0;
        Object current = null;
        if (Minecraft.VERSION.olderThan(Minecraft.Version.v1_9_R1)) {
            // while ((current = ClassBuilder.getDataWatcherValue(original, index++)) !=
            // null) {
            // ClassBuilder.setDataWatcherValue(clone,
            // ClassBuilder.getWatchableObjectIndex(current),
            // ClassBuilder.getWatchableObjectValue(current));
            // }
            while ((current = DataWatcher.V1_8.getValue(original, index++)) != null) {
                DataWatcher.V1_8.setValue(clone, DataWatcher.V1_8.getWatchableObjectIndex(current),
                        DataWatcher.V1_8.getWatchableObjectValue(current));
            }
        } else {
            Field mapField = DataWatcherFieldResolver.resolve("c");
            mapField.set(clone, mapField.get(original));
            // while ((current = DataWatcher.V1_9.getItem(original, index++)) != null) {
            // DataWatcher.V1_9.setItem(clone, index++, current);
            // }

        }
        return clone;
    }

    private static boolean hasDelay(Player player) {
        if (!player.hasMetadata(DELAY_TAG)) return false;
        return player.getMetadata(DELAY_TAG).get(0).asLong() > System.currentTimeMillis();
    }

    private static void addDelay(Player player) {
        player.setMetadata(DELAY_TAG, new FixedMetadataValue(BukkitMain.getInstance(), System.currentTimeMillis() + 600));
    }
}