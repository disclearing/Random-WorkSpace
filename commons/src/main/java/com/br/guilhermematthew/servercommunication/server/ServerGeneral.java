package com.br.guilhermematthew.servercommunication.server;

import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketAction;
import com.br.guilhermematthew.servercommunication.ServerCommunication;
import com.br.guilhermematthew.servercommunication.common.connection.CommonClient;
import com.br.guilhermematthew.servercommunication.common.connection.IConnection;
import com.br.guilhermematthew.servercommunication.common.packet.CommonPacket;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class ServerGeneral extends Thread implements IConnection {

    private String hostName;
    private int port;

    private ServerSocket serverSocket;

    private ConcurrentHashMap<Socket, CommonClient> socketToRegister;
    private Map<String, CommonClient> clientMap;

    private boolean RUNNING;

    public ServerGeneral(final String hostName) throws Exception {
        setHostName(hostName);
        setPort(ServerCommunication.PORT);

        setServerSocket(new ServerSocket());
        getServerSocket().bind(new InetSocketAddress(hostName, getPort()));

        setSocketToRegister(new ConcurrentHashMap<>());
        setClientMap(new HashMap<>());

        debug("Connection to " + hostName + ":" + port + " established!");

        ServerCommunication.getUpdateListener().register(updateType -> {
            for (CommonClient connections : getSocketToRegister().values()) {
                if (!connections.isAuthenticated()) {
                    connections.incrementTimeout();

                    if (connections.getTime() == ServerCommunication.TIMEOUT_TIME + 3) {
                        connections.disconnect();

                        socketToRegister.remove(connections.getSocket());

                        if (ServerCommunication.DEBUG_CLIENT_DROPED) {
                            debug("Connection " + connections.getAddress() + " dropped!");
                        }
                    }

                    if (connections.getTime() == ServerCommunication.TIMEOUT_TIME) {
                        connections.sendPacket(new CPacketAction("Server", 0).writeType("TimedOut"));

                        if (ServerCommunication.DEBUG_CLIENT_DROPED) {
                            debug("Connection " + connections.getAddress() + " timeout!");
                        }
                    }
                }
            }
        });
    }

    @Override
    public void run() {
        RUNNING = true;

        while (RUNNING) {
            if (serverSocket == null) {
                RUNNING = false;
                return;
            }

            if (getServerSocket().isClosed()) {
                try {
                    getServerSocket().bind(new InetSocketAddress(getHostName(), getPort()));
                } catch (IOException e1) {
                    e1.printStackTrace();
                    break;
                }
            }

            try {
                Socket socket = serverSocket.accept();
                String clientHost = socket.getInetAddress().getHostName();
                int clientPort = socket.getPort();

                socketToRegister.put(socket, new CommonClient(socket));

                if (ServerCommunication.DEBUG_CLIENT_CONNECTED) {
                    debug("Connection from " + clientHost + ":" + clientPort + " accepted!");
                }

            } catch (SocketException ex) {
                if (!ex.getLocalizedMessage().equals("Socket closed")) ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        try {
            disconnect();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendPacket(final Socket socket, final CommonPacket packet) {
        CommonClient connection = getConnectionByPort(socket.getPort());

        if (connection != null) {
            connection.sendPacket(packet);
        }
    }

    public void sendPacket(final String text, final CommonPacket packet) {
        val name = text.replaceAll("\\d", "");
        val id = name.length() != text.length() ? Integer.parseInt(text.replaceAll("\\D+","")) : 1;

        sendPacket(name, id, packet);
    }

    public void sendPacket(final String name, final int id, final CommonPacket packet) {
        CommonClient connection = getConnectionListen(name, id);
        if (connection != null) {
            connection.sendPacket(packet);
        }
    }

    public CommonClient getConnectionListen(final String name, final int id) {
        CommonClient connection = getClientMap().getOrDefault(name.toLowerCase() + "," + id, null);

        if (connection != null) return connection;

        for (CommonClient connections : getClientMap().values()) {
            if ((connections.getClientName().equalsIgnoreCase(name)) && (connections.getClientID() == id)) {
                connection = connections;
                break;
            }
        }

        return connection;
    }

    public CommonClient getConnectionListenByPort(int port) {
        CommonClient connection = null;

        for (CommonClient connectionListen : socketToRegister.values()) {
            if (connectionListen.getSocket().getPort() == port) {
                connection = connectionListen;
                break;
            }
        }

        return connection;
    }

    public CommonClient getConnectionByPort(int port) {
        CommonClient connection = null;

        for (CommonClient connectionListen : getClientMap().values()) {
            if (connectionListen.getSocket().getPort() == port) {
                connection = connectionListen;
                break;
            }
        }

        return connection;
    }

    public Socket getSocketByPort(int port) {
        Socket socket = null;

        for (Socket sockets : socketToRegister.keySet()) {
            if (sockets.getPort() == port) {
                socket = sockets;
                break;
            }
        }

        return socket;
    }

    public void registerClient(final String clientName, final Integer clientID, final Socket socket) {
        CommonClient connection = getConnectionListenByPort(socket.getPort());

        if (connection != null) {
            connection.setClientName(clientName);
            connection.setClientID(clientID);

            clientMap.put(clientName + "," + clientID, connection);

            if (ServerCommunication.DEBUG_CLIENT_AUTHENTICATED) {
                debug(clientName + " (" + clientID + ") authenticated!");
            }
        }

        socketToRegister.remove(socket);
    }

    public void unregisterClient(Socket socket) {
        CommonClient connection = getConnectionListenByPort(socket.getPort());

        if (connection != null) {
            String clientName = connection.getClientName();

            int clientId = connection.getClientID();

            clientMap.remove(clientName.toLowerCase() + "," + clientId);

            if (ServerCommunication.DEBUG_CLIENT_AUTHENTICATED) {
                debug(clientName + " (" + clientId + ") loggout!");
            }

        }
    }

    public void debug(final String string) {
        ServerCommunication.debug("SERVER", string);
    }

    @Override
    public String getAddress() {
        return hostName + ":" + port;
    }

    @Override
    public void disconnect() throws IOException {
        RUNNING = false;

        if (getServerSocket() != null) {
            getServerSocket().close();
        }

        serverSocket = null;
    }

    @Override
    public Socket getSocket() {
        return null;
    }

    @Override
    public String getServerName() {
        // TODO Auto-generated method stub
        return "SERVER";
    }
}