package com.br.guilhermematthew.nowly.commons.common.clan;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.common.PluginInstance;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Clan {

    private String name, tag, owner;
    private boolean premium;
    private int maxParticipants;

    private List<String> memberList, adminList;
    private List<Object> onlines;

    public Clan(String clanName, String tag) {
        this.name = clanName;
        this.tag = tag;

        this.memberList = new ArrayList<>();
        this.adminList = new ArrayList<>();
        this.onlines = new ArrayList<>();
    }

    public void addMember(String name) {
        if (!memberList.contains(name)) {
            memberList.add(name);
        }
    }

    public void removePlayer(String name) {
        adminList.remove(name);

        memberList.remove(name);
    }

    public void promote(String name) {
        if (!adminList.contains(name)) {
            adminList.add(name);
        }
    }

    public void unpromote(String name) {
        adminList.remove(name);
    }

    public boolean isAdmin(String name) {
        if (getOwner().equalsIgnoreCase(name)) return true;
        return adminList.contains(name);
    }

    public void removeOnline(Object object) {
        getOnlines().remove(object);
    }

    public void addOnline(Object object) {
        if (!getOnlines().contains(object)) {
            getOnlines().add(object);
        }
    }

    public JsonObject getJSON() {
        JsonObject json = new JsonObject();

        json.addProperty("dono", getOwner());
        json.addProperty("tag", getTag());
        json.addProperty("premium", isPremium());

        json.addProperty("maxParticipants", getMaxParticipants());

        json.addProperty("admins", StringUtility.formatStringToArrayWithoutSpace(getAdminList()));
        json.addProperty("membros", StringUtility.formatStringToArrayWithoutSpace(getMemberList()));
        return json;
    }

    @SuppressWarnings("deprecation")
    public void message(String message) {
        if (CommonsGeneral.getPluginInstance() == PluginInstance.BUKKIT) {
            for (Object players : onlines) {
                org.bukkit.entity.Player player = (org.bukkit.entity.Player) players;
                player.sendMessage(message);
                player = null;
            }
        } else {
            for (Object players : onlines) {
                net.md_5.bungee.api.connection.ProxiedPlayer player = (net.md_5.bungee.api.connection.ProxiedPlayer) players;
                player.sendMessage(message);
                player = null;
            }
        }
    }
}