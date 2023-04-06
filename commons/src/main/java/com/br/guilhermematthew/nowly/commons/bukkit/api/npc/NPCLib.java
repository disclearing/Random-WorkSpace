package com.br.guilhermematthew.nowly.commons.bukkit.api.npc;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.api.npc.api.NPC;
import com.br.guilhermematthew.nowly.commons.bukkit.api.npc.packets.NPC_v1_8_R3;
import com.br.guilhermematthew.nowly.commons.common.utility.skin.Skin;
import org.bukkit.ChatColor;

public class NPCLib {

    public static NPC createNPC(String customName, String color, String name, Skin skin, int itemStackID) {
        return new NPC_v1_8_R3(BukkitMain.getInstance(), customName, color, name, skin, itemStackID);
    }

    public static NPC createNPC(String customName, String color, String name, Skin skin) {
        return createNPC(customName, color, name, skin, 20);
    }

    public static NPC createNPC(String customName, String color, String name) {
        return createNPC(customName, color, name, null, 20);
    }
}
