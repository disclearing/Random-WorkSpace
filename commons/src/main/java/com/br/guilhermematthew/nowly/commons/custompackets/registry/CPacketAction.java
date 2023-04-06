package com.br.guilhermematthew.nowly.commons.custompackets.registry;

import com.br.guilhermematthew.servercommunication.ServerCommunication;
import com.br.guilhermematthew.servercommunication.common.packet.CommonPacket;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.net.Socket;

@Getter
@Setter
public class CPacketAction extends CommonPacket {

    private String type, field, fieldValue, extraValue, extraValue2;

    public CPacketAction() {
    }

    public CPacketAction(final String serverName, final int serverID) {
        setServerName(serverName);
        setServerID(serverID);

        setJson(new JsonObject());

        write();
    }

    @Override
    public void read(final JsonObject json) {
        setJson(json);

        setServerName(json.get("serverName").getAsString());
        setServerID(json.get("serverID").getAsInt());

        if (json.has("type")) {
            this.type = json.get("type").getAsString();
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
    }

    public CPacketAction writeType(String value) {
        getJson().addProperty("type", value);
        return this;
    }

    public CPacketAction writeField(String value) {
        getJson().addProperty("field", value);
        return this;
    }

    public CPacketAction writeFieldValue(String value) {
        getJson().addProperty("fieldValue", value);
        return this;
    }

    public CPacketAction writeExtraValue(String value) {
        getJson().addProperty("extraValue", value);
        return this;
    }

    public CPacketAction writeExtraValue2(String value) {
        getJson().addProperty("extraValue2", value);
        return this;
    }

    @Override
    public void write() {
        getJson().addProperty("packetName", getPacketName());
        getJson().addProperty("serverID", getServerID());
        getJson().addProperty("serverName", getServerName());
        getJson().addProperty("timeStamp", System.currentTimeMillis());
    }

    @Override
    public void handle(final Socket socket) {
        ServerCommunication.getPacketHandler().handleCPacketAction(this, socket);
    }

    @Override
    public String getPacketName() {
        return getClass().getSimpleName();
    }

    @Override
    public String getJSONString() {
        return getJson().toString();
    }

    public String getValues() {
        StringBuilder stringBuilder = new StringBuilder();

        if (getType() != null) {
            stringBuilder.append("type: ").append(getType());
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

        return "(" + getPacketName() + ") has been received! (" + time + " ms) from: (" + from + ") with values: (" + getValues() + ")";
    }
}