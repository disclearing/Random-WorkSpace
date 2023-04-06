package com.br.guilhermematthew.servercommunication.client;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketAction;
import com.br.guilhermematthew.servercommunication.ServerCommunication;
import com.br.guilhermematthew.servercommunication.common.connection.CommonClient;
import com.br.guilhermematthew.servercommunication.common.connection.IConnection;
import com.br.guilhermematthew.servercommunication.common.packet.CommonPacket;
import com.br.guilhermematthew.servercommunication.common.packet.listener.PacketListener;
import com.br.guilhermematthew.servercommunication.common.update.UpdateListener.UpdateEvent;
import com.br.guilhermematthew.servercommunication.common.update.UpdateListener.UpdateType;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.Socket;

@Getter
@Setter
public class ClientConnection implements IConnection {

    private String hostName;
    private int port;

    private Socket socket;
    private CommonClient connectionListen;

    public ClientConnection(final String hostName) {
        setHostName(hostName);
        setPort(ServerCommunication.PORT);
    }

    public void connect() throws IOException {
        socket = new Socket(getHostName(), ServerCommunication.PORT);

        setConnectionListen(new CommonClient(socket));
        ServerCommunication.getPacketListener().register(new PacketListener());
        debug("Connected to " + hostName + ":" + port + "!");

        sendPacket(new CPacketAction(Client.getInstance().getClientName(), Client.getInstance().getClientID()).
                writeType("HandShake").writeField(Client.getInstance().getClientName()).writeFieldValue("" + Client.getInstance().getClientID()));

        if (CommonsGeneral.getPluginInstance().isBukkit() && BukkitServerAPI.isRegisteredServer())
            BukkitServerAPI.registerServer();

        ServerCommunication.getUpdateListener().register(new UpdateEvent() {

            int seconds = 0;

            @Override
            public void onUpdate(UpdateType updateType) {
                if (updateType != UpdateType.SECOND) return;

                seconds++;

                if (seconds % 10 == 0) {
                    sendPacket(new CPacketAction(Client.getInstance().getClientName(), Client.getInstance().getClientID()).
                            writeType("KeepAlive"));
                }
            }
        });
    }

    public void sendPacket(final CommonPacket packet) {
        connectionListen.sendPacket(packet);
    }

    public void debug(String string) {
        ServerCommunication.debug("CLIENT - " + Client.getInstance().getClientName() + ":" + Client.getInstance().getClientID(), string);
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    @Override
    public String getAddress() {
        return hostName + ":" + port;
    }

    @Override
    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket = null;
        }
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public String getServerName() {
        // TODO Auto-generated method stub
        return null;
    }

    public void reconnect(Runnable callback) {
        if (socket != null) disconnect();
        if (connectionListen != null) connectionListen.disconnect();

        try {
            connect();
        } catch (Exception ex) {
            debug("Failed to reconnect, retrying... (" + ex.getLocalizedMessage() + ").");

            try {
                Thread.sleep(1000 * 5);
            } catch (Exception ignored) {
            }

            reconnect(callback);
            return;
        }

        if (callback != null) callback.run();
    }
}