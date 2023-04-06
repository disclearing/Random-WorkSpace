package com.br.guilhermematthew.nowly.commons.bukkit.custom.events.server;

import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Arrays;
import java.util.List;

@Getter
public class ServerStatusUpdateEvent extends Event {

    private static final JsonObject CACHED_JSON = new JsonObject();
    private static final HandlerList handlers = new HandlerList();
    private final List<String> LIST = Arrays.asList("onlines", "maxPlayers", "online", "whiteList", "lastUpdate", "membersSlots", "mapName",
            "playersGaming", "tempo", "stage");

    public ServerStatusUpdateEvent(final int onlines,
                                   final boolean online) {

        for (String name : LIST) {
            if (CACHED_JSON.has(name)) CACHED_JSON.remove(name);
        }

        CACHED_JSON.addProperty("onlines", onlines);
        CACHED_JSON.addProperty("maxPlayers", Bukkit.getMaxPlayers());

        CACHED_JSON.addProperty("online", online);
        CACHED_JSON.addProperty("whiteList", Bukkit.getServer().hasWhitelist());
        CACHED_JSON.addProperty("lastUpdate", System.currentTimeMillis());
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public void writeMemberSlots(final int membersSlots) {
        CACHED_JSON.addProperty("membersSlots", membersSlots);
    }

    public void writeSkyWars(final int playersGaming, final int tempo, final String stage, final String mapName, final String skyWarType) {
        CACHED_JSON.addProperty("mapName", mapName);
        CACHED_JSON.addProperty("playersGaming", playersGaming);
        CACHED_JSON.addProperty("tempo", tempo);
        CACHED_JSON.addProperty("stage", stage);
    }

    public void writeHungerGames(final int playersGaming, final int tempo, final String stage) {
        CACHED_JSON.addProperty("playersGaming", playersGaming);
        CACHED_JSON.addProperty("tempo", tempo);
        CACHED_JSON.addProperty("stage", stage);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public JsonObject getJson() {
        return CACHED_JSON;
    }
}