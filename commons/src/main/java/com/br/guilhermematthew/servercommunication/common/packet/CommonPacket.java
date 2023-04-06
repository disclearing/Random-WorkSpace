package com.br.guilhermematthew.servercommunication.common.packet;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.net.Socket;

@Getter
@Setter
public abstract class CommonPacket {

    private JsonObject json;

    private String serverName;
    private int serverID;
    private Long timestamp;

    public abstract void read(final JsonObject json);

    public abstract void write();

    public abstract void handle(final Socket socket);

    public abstract String getJSONString();

    public abstract String getPacketName();

    public abstract String getDebugReceived();
}