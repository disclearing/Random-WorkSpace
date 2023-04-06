package com.br.guilhermematthew.nowly.commons.custompackets.registry;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.common.PluginInstance;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import com.br.guilhermematthew.nowly.commons.custompackets.PacketType;
import com.br.guilhermematthew.servercommunication.ServerCommunication;
import com.br.guilhermematthew.servercommunication.common.packet.CommonPacket;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.net.Socket;
import java.util.UUID;

@Getter
@Setter
public class CPacketCustomAction extends CommonPacket {

    private UUID uniqueId;
    private String nick, field, fieldValue, extraValue, extraValue2;

    private PacketType packetType;
    private ServerType serverType;

    public CPacketCustomAction() {
    }

    public CPacketCustomAction(String nick, final UUID uniqueId) {
        this.nick = nick;
        this.uniqueId = uniqueId;

        setJson(new JsonObject());

        getJson().addProperty("uniqueId", uniqueId == null ? "INVALID UUID" : uniqueId.toString());
        getJson().addProperty("nick", nick == null ? "INVALID NICK" : nick);

        write();
    }

    public CPacketCustomAction(String nick) {
        this(nick, null);
    }

    public CPacketCustomAction(UUID uniqueId) {
        this(null, uniqueId);
    }

    public CPacketCustomAction(ServerType type, int serverId) {
        this.serverType = type;
        setServerID(serverId);
        setServerName(type.getName());
        setJson(new JsonObject());

        getJson().addProperty("uniqueId", uniqueId == null ? "INVALID UUID" : uniqueId.toString());
        getJson().addProperty("nick", nick == null ? "INVALID NICK" : nick);

        write();
    }

    @Override
    public void read(final JsonObject json) {
        setJson(json);

        this.uniqueId = null;
        this.nick = null;

        setServerName(json.get("serverName").getAsString());
        setServerID(json.get("serverID").getAsInt());

        if (json.has("nick")) {
            if (!json.get("nick").getAsString().equalsIgnoreCase("INVALID NICK")) {
                this.nick = json.get("nick").getAsString();
            }
        }

        if (json.has("uniqueId")) {
            String uuidString = json.get("uniqueId").getAsString();
            if (!uuidString.equalsIgnoreCase("INVALID UUID")) {
                this.uniqueId = UUID.fromString(uuidString);
            }
        }

        if (json.has("packetType")) {
            this.packetType = PacketType.getType(json.get("packetType").getAsString());
        }

        if (json.has("field")) {
            this.field = json.get("field").getAsString();
        }

        if (json.has("fieldValue")) {
            this.fieldValue = json.get("fieldValue").getAsString();
        }

        if (json.has("extraValue")) {
            this.extraValue = json.get("extraValue").getAsString();
        }

        if (json.has("extraValue2")) {
            this.extraValue2 = json.get("extraValue2").getAsString();
        }

        if (json.has("timeStamp")) {
            setTimestamp(json.get("timeStamp").getAsLong());
        }

        if (json.has("serverType")) {
            this.serverType = ServerType.getServer(json.get("serverType").getAsString());
        }
    }

    public CPacketCustomAction type(PacketType type) {
        check();

        getJson().addProperty("packetType", type.name().toUpperCase());
        return this;
    }

    private CPacketCustomAction check() {
        if (getJson() == null) {
            setJson(new JsonObject());
        }

        if (this.serverType == null) {
            if (CommonsGeneral.getPluginInstance() == PluginInstance.BUNGEECORD) {
                setServerID(1);
                this.serverType = ServerType.BUNGEECORD;
            } else {
                this.serverType = com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain.getServerType();
                setServerID(com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain.getServerID());
            }
        }

        setServerName(getServerType().getName());

        if (!getJson().has("serverName")) {
            getJson().addProperty("serverName", getServerName());
        }

        if (!getJson().has("serverID")) {
            getJson().addProperty("serverID", getServerID());
        }

        if (!getJson().has("packetName")) {
            getJson().addProperty("packetName", getPacketName());
        }
        if (!getJson().has("timeStamp")) {
            getJson().addProperty("timeStamp", System.currentTimeMillis());
        }

        if (!getJson().has("serverType")) {
            getJson().addProperty("serverType", getServerType().getName());
        }

        return this;
    }

    public CPacketCustomAction field(String value) {
        check();

        getJson().addProperty("field", value);
        return this;
    }

    public CPacketCustomAction fieldValue(String value) {
        getJson().addProperty("fieldValue", value);
        return this;
    }

    public CPacketCustomAction extraValue(String value) {
        getJson().addProperty("extraValue", value);
        return this;
    }

    public CPacketCustomAction extraValue2(String value) {
        getJson().addProperty("extraValue2", value);
        return this;
    }

    @Override
    public void write() {
        check();
    }

    @Override
    public void handle(final Socket socket) {
        ServerCommunication.getPacketHandler().handleCPacketPlayerAction(this, socket);
    }

    @Override
    public String getPacketName() {
        return "CPacketCustomAction";
    }

    @Override
    public String getJSONString() {
        return getJson().toString();
    }

    public String getValues() {
        StringBuilder stringBuilder = new StringBuilder();

        if (getPacketType() != null) {
            stringBuilder.append("packetType: ").append(getPacketType().name().toUpperCase());
        }

        if (getField() != null) {
            stringBuilder.append(", field: ").append(getField());
        }

        if (getFieldValue() != null) {
            stringBuilder.append(", fieldValue: ").append(getFieldValue());
        }

        if (getExtraValue() != null) {
            stringBuilder.append(", extraValue: ").append(getExtraValue());
        }

        if (getExtraValue2() != null) {
            stringBuilder.append(", extraValue2: ").append(getExtraValue2());
        }

        return stringBuilder.toString();
    }

    @Override
    public String getDebugReceived() {
        final String from = getServerName() + "-" + getServerID();

        final long time = (System.currentTimeMillis() - getTimestamp());

        return "(" + getPacketName() + ") has been received! (" + time + " ms) from: (" + from + ") with values: ("
                + getValues() + ")";
    }
}