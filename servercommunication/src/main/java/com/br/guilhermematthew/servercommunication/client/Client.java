package com.br.guilhermematthew.servercommunication.client;

import com.br.guilhermematthew.servercommunication.ServerCommunication;
import com.br.guilhermematthew.servercommunication.ServerCommunicationInstance;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client {

    @Getter
    private static Client instance;

    private String clientName;
    private int clientID;

    private ClientConnection clientConnection;

    public Client(final String hostName, final String clientName, final int clientID) {
        instance = this;

        ServerCommunication.INSTANCE = ServerCommunicationInstance.CLIENT;

        setClientName(clientName);
        setClientID(clientID);

        setClientConnection(new ClientConnection(hostName));
    }

    public static boolean isInstanced() {
        return instance != null;
    }
}