package com.br.guilhermematthew.nowly.commons.bukkit.api.npc.api;

import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.Hologram;
import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.HologramAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.npc.NPCManager;
import com.br.guilhermematthew.nowly.commons.bukkit.api.npc.api.wrapper.GameProfileWrapper;
import com.br.guilhermematthew.nowly.commons.common.utility.skin.Skin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class NPC implements PacketHandler {

    protected final UUID uuid = UUID.randomUUID();
    // Below was previously = (int) Math.ceil(Math.random() * 100000) + 100000 (new is experimental).
    protected final int entityId = Integer.MAX_VALUE - NPCManager.getAllNPCs().size();
    protected final Skin skin;
    private final Set<UUID> shown = new HashSet<>();
    protected int itemStackID = 0;
    protected final String customName;
    protected final String color;
    protected final String name;
    protected final JavaPlugin plugin;
    protected GameProfileWrapper gameProfile;
    protected Location location;

    //for players 1.8
    @Getter
    protected Hologram nameHologram;

    public NPC(JavaPlugin plugin, String customName, String color, String name, Skin skin, int itemStackID) {
        this.plugin = plugin;
        this.skin = skin;
        this.name = name;
        this.customName = customName;
        this.color = color;

        this.itemStackID = itemStackID;

        NPCManager.add(this);
    }

    protected GameProfileWrapper generateGameProfile(UUID uuid, String name) {
        GameProfileWrapper gameProfile = new GameProfileWrapper(uuid, name);

        if (skin != null) {
            gameProfile.addSkin(skin);
        }

        return gameProfile;
    }

    public String getCustomName() {
        return customName;
    }

    public void destroy() {
        NPCManager.remove(this);

        // Destroy NPC for every player that is still seeing it.
        for (UUID uuid : shown) {
            hide(Bukkit.getPlayer(uuid));
        }

        nameHologram.despawn();
    }

    public void hideAll() {
        for (UUID uuid : shown) {
            hide(Bukkit.getPlayer(uuid));
        }
    }

    public Set<UUID> getShown() {
        return shown;
    }

    public Location getLocation() {
        return location;
    }

    public int getEntityId() {
        return entityId;
    }

    public void create(Location location) {
        this.location = location;

        nameHologram = HologramAPI.createHologram("name",
                location.clone().add(0, 2.095, 0), null);
        nameHologram.spawn();

        createPackets();
    }

    public Location getLocationForHologram() {
        return location.clone().add(0, 2.05, 0);
    }

    public void show(Player player) {
        if (shown.contains(player.getUniqueId())) {
            return;
        }
        sendShowPackets(player);
    }

    public void hide(Player player) {
        if (!shown.contains(player.getUniqueId())) {
            return;
        }
        sendHidePackets(player, false);
    }
}