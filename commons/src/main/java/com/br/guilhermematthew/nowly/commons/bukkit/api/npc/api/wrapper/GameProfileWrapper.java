package com.br.guilhermematthew.nowly.commons.bukkit.api.npc.api.wrapper;

import com.br.guilhermematthew.nowly.commons.bukkit.utility.Reflection;
import com.br.guilhermematthew.nowly.commons.common.utility.skin.Skin;
import com.google.common.collect.ForwardingMultimap;
import org.bukkit.Bukkit;

import java.util.UUID;

public class GameProfileWrapper {

    // Written because of issue#10 (https://github.com/JitseB/NPCLib/issues/10).
    // This class acts as an NMS reflection wrapper for the GameProfileWrapper class.

    // TODO: As of 1.4.2 1.7 support was removed, refactor this class.

    // TODO: This doesn't seem to work well with modified versions of Spigot (see issue #12).
    private final boolean is1_7 = Bukkit.getBukkitVersion().contains("1.7");
    private final Class<?> gameProfileClazz = Reflection.getClass((is1_7 ? "net.minecraft.util." : "") + "com.mojang.authlib.GameProfile");

    final Object gameProfile;

    public GameProfileWrapper(UUID uuid, String name) {
        // Only need to check if the version is 1.7, as NPCLib doesn't support any version below this version.
        this.gameProfile = Reflection.getConstructor(gameProfileClazz, UUID.class, String.class).invoke(uuid, name);
    }

    @SuppressWarnings("rawtypes")
    public void addSkin(Skin skin) {
        // Create a new property with the skin data.
        Class<?> propertyClazz = Reflection.getClass((is1_7 ? "net.minecraft.util." : "") + "com.mojang.authlib.properties.Property");
        Object property = Reflection.getConstructor(propertyClazz,
                String.class, String.class, String.class).invoke("textures", skin.getValue(), skin.getSignature());

        // Get the property map from the GameProfileWrapper object.
        Class<?> propertyMapClazz = Reflection.getClass((is1_7 ? "net.minecraft.util." : "") + "com.mojang.authlib.properties.PropertyMap");
        Reflection.FieldAccessor propertyMapGetter = Reflection.getField(gameProfileClazz, "properties",
                propertyMapClazz);
        Object propertyMap = propertyMapGetter.get(gameProfile);

        // TODO: Won't work on 1.7.10 (as Guava also changed package location).
        // Add our new property to the property map.
        Reflection.getMethod(ForwardingMultimap.class, "put", Object.class, Object.class)
                .invoke(propertyMap, "textures", property);

        // Finally set the property map back in the GameProfileWrapper object.
        propertyMapGetter.set(gameProfile, propertyMap);
    }

    public Object getGameProfile() {
        return gameProfile;
    }
}