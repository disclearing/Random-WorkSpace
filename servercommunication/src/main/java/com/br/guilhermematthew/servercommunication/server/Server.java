package com.br.guilhermematthew.servercommunication.server;

import com.br.guilhermematthew.servercommunication.ServerCommunication;
import com.br.guilhermematthew.servercommunication.ServerCommunicationInstance;
import com.br.guilhermematthew.servercommunication.common.packet.CommonPacket;
import com.br.guilhermematthew.servercommunication.common.packet.listener.PacketListener;
import lombok.Getter;
import lombok.Setter;

import java.net.Socket;

public class Server {

    @Getter
    @Setter
    private static Server instance;

    @Getter
    private ServerGeneral serverGeneral;

    public Server(final String hostName) {
        setInstance(this);

        ServerCommunication.debug("[ServerCommunication] The server has started on: " + hostName);

        ServerCommunication.INSTANCE = ServerCommunicationInstance.SERVER;

        ServerCommunication.getPacketListener().register(new PacketListener());

        try {
            serverGeneral = new ServerGeneral(hostName);
            serverGeneral.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    public void sendPacket(final String text, final CommonPacket packet) {
        getServerGeneral().sendPacket(text, packet);
    }

    public void sendPacket(final String name, final int id, final CommonPacket packet) {
        getServerGeneral().sendPacket(name, id, packet);
    }

    public void sendPacket(final Socket socket, final CommonPacket packet) {
        getServerGeneral().sendPacket(socket, packet);
    }
}