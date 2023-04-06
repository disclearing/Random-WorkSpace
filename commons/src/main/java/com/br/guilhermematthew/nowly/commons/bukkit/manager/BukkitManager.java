package com.br.guilhermematthew.nowly.commons.bukkit.manager;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.HologramAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.HologramInjector;
import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.HologramListeners;
import com.br.guilhermematthew.nowly.commons.bukkit.api.npc.NPCManager;
import com.br.guilhermematthew.nowly.commons.bukkit.api.npc.listener.NPCListener;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.injector.packets.NPCPacketInjector;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.injector.packets.ServerPacketInjector;
import com.br.guilhermematthew.nowly.commons.bukkit.manager.configuration.BukkitConfigurationManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@Getter
public class BukkitManager {

    private final BukkitConfigurationManager configurationManager;

    private boolean NPC_EVENTS = false,
            PACKET_INJECTOR = false,
            HOLOGRAM_EVENTS = false;

    public BukkitManager() {
        this.configurationManager = new BukkitConfigurationManager();
    }

    public void disable() {
        if (NPC_EVENTS) {

        }

        if (HOLOGRAM_EVENTS) {
            HologramAPI.getHolograms().forEach(holograms -> HologramAPI.removeHologram(holograms));
        }
    }

    public void enablePacketInjector(Plugin plugin) {
        if (PACKET_INJECTOR) return;

        PACKET_INJECTOR = true;

        ServerPacketInjector.inject(plugin);

        BukkitMain.console("§a[BukkitManager] ServerPacketInjector has been enabled!");
    }

    public void enableNPC(Plugin plugin) {
        if (!NPC_EVENTS) {
            NPC_EVENTS = true;
            NPCPacketInjector.inject(plugin);
            NPCManager.register();

            Bukkit.getServer().getPluginManager().registerEvents(new NPCListener(), plugin);
        }
    }

    public void enableHologram(Plugin plugin) {
        if (HOLOGRAM_EVENTS) return;

        HOLOGRAM_EVENTS = true;

        HologramAPI.packetsEnabled = true;
        HologramListeners.registerListeners();
        HologramInjector.inject(plugin);

        BukkitMain.console("§a[BukkitManager] HologramAPI has been enabled!");
    }
}