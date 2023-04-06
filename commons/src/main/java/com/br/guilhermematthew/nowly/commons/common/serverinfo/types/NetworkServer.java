package com.br.guilhermematthew.nowly.commons.common.serverinfo.types;

import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NetworkServer {

    private ServerType serverType;
    private int maxPlayers, onlines, membersSlots, serverId, sequence;
    private boolean online, whiteList;
    private Long lastUpdate;

    public NetworkServer(ServerType serverType, int serverId) {
        this.serverId = serverId;
        this.serverType = serverType;

        this.maxPlayers = 80;
        this.onlines = 0;
        this.sequence = 0;

        this.membersSlots = 80;

        this.online = false;
        this.whiteList = false;

        this.lastUpdate = 0L;
    }

    public NetworkServer(ServerType serverType) {
        this(serverType, 1);
    }

    public void update(final JsonObject json) {
        setOnlines(json.has("onlines") ? json.get("onlines").getAsInt() : 0);
        setMembersSlots(json.has("memberSlots") ? json.get("memberSlots").getAsInt() : 80);
        setMaxPlayers(json.has("maxPlayers") ? json.get("maxPlayers").getAsInt() : 80);
        setWhiteList(json.has("whiteList") && json.get("whiteList").getAsBoolean());
        setOnline(json.has("online") && json.get("online").getAsBoolean());

        if (json.has("lastUpdate") && isOnline()) {
            final Long now = json.get("lastUpdate").getAsLong();

            if (now == -1L) {
                setOnline(false);
                setOnlines(0);
                setSequence(10);
            }

            if (now.equals(getLastUpdate())) {
                setSequence(getSequence() + 1);
                if (getSequence() > 8) {
                    setOnline(false);
                }
            } else {
                setLastUpdate(now);
                setSequence(0);
                setOnline(true);
            }
        }

        if (!isOnline()) {
            setOnlines(0);
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("onlines", getOnlines());
        json.addProperty("maxPlayers", getMaxPlayers());

        json.addProperty("online", isOnline());
        json.addProperty("whiteList", isWhiteList());
        json.addProperty("lastUpdate", getLastUpdate());

        json.addProperty("membersSlots", getMembersSlots());

        return json;
    }
}