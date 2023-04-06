package com.br.guilhermematthew.servercommunication.server;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectionInfo {

    private String address;
    private int connectionsPerSecond;

    public ConnectionInfo(String address) {
        this.address = address;
    }

    public void addConnection() {
        this.connectionsPerSecond++;
        if (this.connectionsPerSecond >= 6) {
            //Server.getInstance().getServerGeneral().adToBlackList(address);
        }
    }
}