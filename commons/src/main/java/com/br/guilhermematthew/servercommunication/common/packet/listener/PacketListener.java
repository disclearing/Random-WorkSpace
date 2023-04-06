package com.br.guilhermematthew.servercommunication.common.packet.listener;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.custompackets.PacketsManager;
import com.br.guilhermematthew.servercommunication.ServerCommunication;
import com.br.guilhermematthew.servercommunication.common.packet.CommonPacket;
import com.br.guilhermematthew.servercommunication.common.packet.PacketListenerManager.PacketEvent;
import com.google.gson.JsonObject;

import java.net.Socket;

public class PacketListener implements PacketEvent {

    @Override
    public void onPacketReceive(final String packetString, final Socket socket) {
        JsonObject json = null;

        try {
            json = CommonsConst.PARSER.parse(packetString).getAsJsonObject();
        } catch (Exception ex) {
            ServerCommunication.debug("PACKET PARSER", "An error ocurred while trying to parse packet! (" + ex.getLocalizedMessage() + ")");
        } finally {
            if (json != null) {
                if (json.has("packetName")) {
                    String packetName = json.get("packetName").getAsString();

                    CommonPacket packet = PacketsManager.getPacket(packetName);

                    if (packet != null) {
                        packet.read(json);

                        if (ServerCommunication.DEBUG_PACKET_RECEIVED) {
                            ServerCommunication.debug("PACKET RECEIVE", packet.getDebugReceived());
                        }

                        try {
                            packet.handle(socket);
                        } catch (Exception ex) {
                            ServerCommunication.debug("PACKET HANDLER", "An error ocurred while trying to handle packet! (" + ex.getLocalizedMessage() + ")");
                            ex.printStackTrace();
                        }

                        packet = null;
                    }
                    packetName = null;
                } else {
                    ServerCommunication.debug("I received a Invalid Packet! (#1)");
                }
                json = null;
            }
        }
    }
}