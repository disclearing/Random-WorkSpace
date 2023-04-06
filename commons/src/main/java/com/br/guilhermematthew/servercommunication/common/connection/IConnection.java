package com.br.guilhermematthew.servercommunication.common.connection;

import java.io.IOException;
import java.net.Socket;

public interface IConnection {

    String getAddress();

    void disconnect() throws IOException;

    Socket getSocket();

    String getServerName();
}