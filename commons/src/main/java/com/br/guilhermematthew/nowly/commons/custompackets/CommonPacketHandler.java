package com.br.guilhermematthew.nowly.commons.custompackets;

import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketAction;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketCustomAction;

import java.net.Socket;

public abstract class CommonPacketHandler {

    public abstract void handleCPacketAction(final CPacketAction packet, final Socket socket);

    public abstract void handleCPacketPlayerAction(final CPacketCustomAction cPacketPlayerAction, final Socket socket);
}