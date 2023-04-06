package com.br.guilhermematthew.nowly.commons.bukkit.api.npc;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.api.npc.api.NPC;
import com.br.guilhermematthew.nowly.commons.bukkit.api.npc.listener.NPCListener;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

public final class NPCManager {

    private static Set<NPC> npcs;

    private static boolean registred = false;

    private NPCManager() {
        throw new SecurityException("You cannot initialize this class.");
    }

    public static void register() {
        if (registred) return;

        registred = true;
        npcs = new HashSet<>();

        Bukkit.getServer().getPluginManager().registerEvents(new NPCListener(), BukkitMain.getInstance());

        BukkitMain.console("§a[NPCS] has been registred!");
    }

    public static Set<NPC> getAllNPCs() {
        return npcs;
    }

    public static void add(NPC npc) {
        npcs.add(npc);
    }

    public static void remove(NPC npc) {
        npcs.remove(npc);
    }

    public static NPC getNPCByName(String name) {
        NPC finded = null;

        for (NPC npc : npcs) {
            if (npc.getCustomName().equalsIgnoreCase(name)) {
                finded = npc;
                break;
            }
        }

        return finded;
    }
}