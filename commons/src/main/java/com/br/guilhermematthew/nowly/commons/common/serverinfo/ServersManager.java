package com.br.guilhermematthew.nowly.commons.common.serverinfo;

import com.br.guilhermematthew.nowly.commons.common.serverinfo.types.GameServer;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.types.NetworkServer;
import com.br.guilhermematthew.nowly.commons.custompackets.PacketType;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketCustomAction;
import com.br.guilhermematthew.servercommunication.client.Client;
import com.google.gson.JsonObject;

import java.util.*;

public class ServersManager {

    private final int HARDCOREGAMES_SERVIDORES = 6;
    private Map<String, NetworkServer> serverMap;

    public void init() {
        this.serverMap = new HashMap<>();

        registerServers(
                new NetworkServer(ServerType.LOBBY),
                new NetworkServer(ServerType.LOBBY_DUELS),
                new NetworkServer(ServerType.LOBBY_PVP),
                new NetworkServer(ServerType.LOBBY_HARDCOREGAMES),
                new NetworkServer(ServerType.LOGIN),

                new NetworkServer(ServerType.BUNGEECORD),

                new NetworkServer(ServerType.DUELS_SIMULATOR),
                new NetworkServer(ServerType.DUELS_GLADIATOR),

                new NetworkServer(ServerType.PVP_ARENA),
                new NetworkServer(ServerType.PVP_FPS),
                new NetworkServer(ServerType.PVP_LAVACHALLENGE),
                new NetworkServer(ServerType.GLADIATOR));

        for (int i = 1; i <= HARDCOREGAMES_SERVIDORES; i++) {
            registerServer(new GameServer(ServerType.HARDCORE_GAMES, i));
        }

        registerServer(new GameServer(ServerType.CHAMPIONS, 1));
        registerServer(new GameServer(ServerType.EVENTO, 1));
    }

    public NetworkServer getServerByType(final ServerType serverType) {
        return getServerByType(serverType, 1);
    }

    public NetworkServer getServerByType(final ServerType serverType, int serverId) {
        NetworkServer server = serverMap.getOrDefault(serverType.getName().toLowerCase() + "-" + serverId, null);

        if (server == null) {
            for (NetworkServer servers : serverMap.values()) {
                if (servers.getServerType() == serverType && servers.getServerId() == serverId) {
                    server = servers;
                    break;
                }
            }
        }

        return server;
    }

    public void registerServers(final NetworkServer... servers) {
        for (NetworkServer server : servers)
            registerServer(server);
    }

    public NetworkServer getNetworkServer(final String name) {
        return getNetworkServer(name, 1);
    }

    public NetworkServer getNetworkServer(final String name, final int id) {
        return serverMap.get(name.toLowerCase() + "-" + id);
    }

    public NetworkServer getNetworkServer(final ServerType type, final int id) {
        return getNetworkServer(type.getName(), id);
    }

    public void registerServer(final NetworkServer server) {
        serverMap.put(server.getServerType().getName().toLowerCase() + "-" + server.getServerId(), server);
    }

    public NetworkServer getServer(final String serverId) {
        return serverMap.getOrDefault(serverId.toLowerCase(), null);
    }

    public List<NetworkServer> getServersList(ServerType serverType) {
        List<NetworkServer> servers = new ArrayList<>();

        for (NetworkServer networkServer : serverMap.values()) {
            if (networkServer.getServerType() == serverType) {
                servers.add(networkServer);
            }
        }

        return servers;
    }

    public Collection<NetworkServer> getServers() {
        return serverMap.values();
    }

    public int getAmountOnNetwork() {
        return getNetworkServer("bungeecord").getOnlines();
    }

    public void sendRequireUpdate() {
        Client.getInstance().getClientConnection().sendPacket(new CPacketCustomAction().
                type(PacketType.BUKKIT_SEND_INFO).field("require-servers-info"));
    }

    public void updateServerData(JsonObject json) {
        ServerType serverType = ServerType.getServer(json.get("serverType").getAsString());
        int serverId = json.get("serverID").getAsInt();
        updateServerData(serverType, serverId, json);
    }

    public void updateServerData(ServerType serverType, int serverId, JsonObject json) {
        if (serverType != ServerType.UNKNOWN) {
            if (serverType.isHardcoreGames(false)) {
                getHardcoreGamesServer(serverType.getName(), serverId).updateData(json);
            } else if (serverType == ServerType.BEDWARS) {
                // getNetworkServer(serverType.getName(), serverId).update(json);
            } else {
                getNetworkServer(serverType.getName(), serverId).update(json);
            }
        }
    }

    public int getAmountOnlinePvP(boolean getLobby) {
        int onlines = 0;

        onlines += getNetworkServer("arena").getOnlines();
        onlines += getNetworkServer("fps").getOnlines();
        onlines += getNetworkServer("lavachallenge").getOnlines();

        if (getLobby) {
            onlines += getNetworkServer("lobbypvp").getOnlines();
        }
        return onlines;
    }

    public int getAmountOnlineDuels(boolean getLobby) {
        int onlines = 0;

        onlines += getNetworkServer("gladiator").getOnlines();
        onlines += getNetworkServer("simulator").getOnlines();
        if (getLobby) {
            onlines += getNetworkServer("LobbyDuels").getOnlines();
        }
        return onlines;
    }

    public int getAmountOnlineHardcoreGames(boolean getLobby) {
        int onlines = 0;

        for (GameServer server : getHardcoreGamesServers()) {
            onlines += server.getOnlines();
        }

        if (getLobby) {
            onlines += getNetworkServer("lobbyhardcoregames").getOnlines();
        }
        return onlines;
    }

    public GameServer getGameServer(String text) {
        return (GameServer) getServer(text);
    }

    public List<GameServer> getHardcoreGamesServers() {
        List<GameServer> list = new ArrayList<>();

        for (int i = 1; i <= HARDCOREGAMES_SERVIDORES; i++) {
            list.add(getHardcoreGamesServer(i));
        }

        return list;
    }

    public GameServer getGameServer(ServerType serverType, int serverId) {
        return (GameServer) getServer(serverType.getName().toLowerCase() + "-" + serverId);
    }

    public GameServer getHardcoreGamesServer(int id) {
        return (GameServer) getServer("hardcoregames" + "-" + id);
    }

    public GameServer getHardcoreGamesServer(String name, int id) {
        return (GameServer) getServer(name + "-" + id);
    }
}