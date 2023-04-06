package com.br.guilhermematthew.servercommunication.common.connection;

import com.br.guilhermematthew.servercommunication.common.connection.handle.InputHandler;
import com.br.guilhermematthew.servercommunication.common.connection.handle.OutputHandler;
import com.br.guilhermematthew.servercommunication.common.packet.CommonPacket;
import lombok.Getter;
import lombok.Setter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@Getter
@Setter
public class CommonClient implements IConnection {

    private Socket socket;

    private InputHandler inputHandler;
    private OutputHandler outputHandler;

    private String clientName;
    private int clientID;

    //NEWS
    private int time;

    public CommonClient(final Socket socket) throws IOException {
        setSocket(socket);

        setInputHandler(new InputHandler(this, new DataInputStream(socket.getInputStream())));
        setOutputHandler(new OutputHandler(new DataOutputStream(socket.getOutputStream())));

        getInputHandler().start();
        getOutputHandler().start();
    }

    public void sendPacket(final CommonPacket packet) {
        getOutputHandler().sendPacket(packet);
    }

    public void disconnect() {
        if (!socket.isClosed()) {
            try {
                inputHandler.close();
                outputHandler.close();
                socket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        inputHandler.stopThread();
        outputHandler.stopThread();
    }

    public String getAddress() {
        return socket.getInetAddress().getHostName() + ":" + socket.getPort();
    }

    public boolean isAuthenticated() {
        return getClientName() != null;
    }

    public void incrementTimeout() {
        time++;
    }

    @Override
    public String getServerName() {
        return clientName + "-" + clientID;
    }
}