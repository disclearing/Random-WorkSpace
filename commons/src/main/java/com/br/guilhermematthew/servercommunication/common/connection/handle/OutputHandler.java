package com.br.guilhermematthew.servercommunication.common.connection.handle;

import com.br.guilhermematthew.servercommunication.ServerCommunication;
import com.br.guilhermematthew.servercommunication.client.Client;
import com.br.guilhermematthew.servercommunication.common.packet.CommonPacket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class OutputHandler extends Thread {

    private final Object LOCK = new Object();
    private final List<CommonPacket> packetQueue = new ArrayList<>();
    private final DataOutputStream data;
    private boolean running;
    private int serverOutOfReach;

    public OutputHandler(DataOutputStream data) {
        this.data = data;
    }

    @Override
    public void run() {
        running = true;

        while (running) {
            try {
                synchronized (packetQueue) {
                    while (packetQueue.size() > 0) {
                        CommonPacket packet = packetQueue.get(0);

                        if (packet != null) {
                            try {
                                data.writeUTF(packet.getJSONString());
                            } catch (SocketException ex) {
                                serverOutOfReach++;

                                if (serverOutOfReach == 1) {
                                    ServerCommunication.debug("PACKET SEND", "Server is offline!");
                                }

                                if (serverOutOfReach % 5 == 0) {
                                    Client.getInstance().getClientConnection().reconnect(null);
                                }

                                if (serverOutOfReach >= 30) {
                                    stopThread();
                                    close();
                                    serverOutOfReach = 0;
                                }
                            }

                            packet = null;
                        }

                        packetQueue.remove(0);
                    }
                }
                synchronized (LOCK) {
                    LOCK.wait(3250);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                packetQueue.remove(0);
                e.printStackTrace();
            }
        }
    }

    public void sendPacket(final CommonPacket packet) {
        synchronized (packetQueue) {
            packet.write();

            packetQueue.add(packet);
        }

        synchronized (LOCK) {
            LOCK.notifyAll();
        }
    }

    public void stopThread() {
        running = false;
    }

    public void close() throws IOException {
        data.close();
    }
}