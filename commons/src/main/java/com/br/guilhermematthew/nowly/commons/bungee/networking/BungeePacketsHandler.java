package com.br.guilhermematthew.nowly.commons.bungee.networking;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.bungee.account.BungeePlayer;
import com.br.guilhermematthew.nowly.commons.bungee.skins.MojangAPI.SkinRequestException;
import com.br.guilhermematthew.nowly.commons.bungee.skins.SkinApplier;
import com.br.guilhermematthew.nowly.commons.bungee.skins.SkinStorage;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.types.GameServer;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.types.NetworkServer;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import com.br.guilhermematthew.nowly.commons.custompackets.CommonPacketHandler;
import com.br.guilhermematthew.nowly.commons.custompackets.PacketType;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketAction;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketCustomAction;
import com.br.guilhermematthew.servercommunication.server.Server;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BungeePacketsHandler extends CommonPacketHandler {

    /**
     * CACHE RESPONSE SERVERS INFO
     */
    private Long lastUpdate = 0L;
    private CPacketCustomAction lastResponse;

    @Override
    public void handleCPacketAction(CPacketAction packet, final Socket socket) {
        if (packet.getType().equalsIgnoreCase("HandShake")) {
            Server.getInstance().getServerGeneral().registerClient(packet.getField(),
                    Integer.valueOf(packet.getFieldValue()), socket);
        }

        if (packet.getType().equalsIgnoreCase("Loggout")) {
            Server.getInstance().getServerGeneral().unregisterClient(socket);
        }
    }

    @Override
    public void handleCPacketPlayerAction(CPacketCustomAction packet, Socket socket) {
        PacketType type = packet.getPacketType();

        if (type != null) {
            switch (type) {
                case BUKKIT_REQUEST_ACCOUNT_TO_BUNGEECORD:
                    handleBukkitAccountRequest(packet, socket);
                    break;
                case BUKKIT_SEND_UPDATED_DATA:
                    handleBukkitSendDatas(packet);
                    break;
                case BUKKIT_SEND_SERVER_DATA:
                    handleReceivedServerData(packet);
                    break;
                case BUNGEE_SET_FAST_SKIN:
                    handleSetFastSkin(packet, socket);
                    break;
                case BUNGEE_SET_RANDOM_SKIN:
                    handleSetRandomSkin(packet);
                    break;
                case BUNGEE_SET_SKIN:
                    handleSetSkin(packet);
                    break;
                case BUNGEE_UPDATE_SKIN:
                    handleUpdateSkin(packet, socket);
                    break;
                case BUKKIT_SEND_INFO:
                    handleReceiveInfo(packet, socket);
                    break;
                default:
                    break;
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void handleReceiveInfo(CPacketCustomAction packet, Socket socket) {
        if (packet.getField().equalsIgnoreCase("bukkit-register-server")) {
            if (StringUtility.isInteger(packet.getExtraValue())) {
                boolean useId = packet.getServerType() == ServerType.HARDCORE_GAMES;

                BungeeMain.registerServer(packet.getServerType().getName() + (useId ? packet.getServerID() : ""),
                        packet.getFieldValue(), Integer.parseInt(packet.getExtraValue()));
            }
        } else if (packet.getField().equalsIgnoreCase("bukkit-server-turn-on")) {

            ServerType serverType = packet.getServerType();

            if (serverType.isPvP(false)) {
                Server.getInstance().sendPacket(ServerType.LOBBY_PVP.getName(),
                        new CPacketCustomAction(ServerType.BUNGEECORD, 1).type(PacketType.BUNGEE_SEND_INFO)
                                .field(packet.getField())
                                .fieldValue(BungeeMessages.ROOM_STARTED.replace("%prefix%", "§6§lPVP").replace("%sala%",
                                        serverType.getName().toUpperCase())));
            } else if (serverType.isHardcoreGames(false) && serverType != ServerType.CHAMPIONS && serverType != ServerType.EVENTO) {
                Server.getInstance().sendPacket(ServerType.LOBBY_HARDCOREGAMES.getName(),
                        new CPacketCustomAction(ServerType.BUNGEECORD, 1).type(PacketType.BUNGEE_SEND_INFO)
                                .field(packet.getField())
                                .fieldValue(BungeeMessages.ROOM_STARTED.replace("%prefix%", "§6§lHG").replace("%sala%",
                                        serverType.getName().toUpperCase())));
            }
        } else if (packet.getField().equalsIgnoreCase("bukkit-server-turn-off")) {
            ServerType serverType = packet.getServerType();
            int id = packet.getServerID();

            lastUpdate = 0L;

            NetworkServer networkServer = CommonsGeneral.getServersManager().getServerByType(serverType, id);
            if (networkServer != null) {
                networkServer.setLastUpdate(-1L);
                networkServer.setOnline(false);
                networkServer.setOnlines(0);
            }
        } else if (packet.getField().equalsIgnoreCase("add-perm")) {
            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(packet.getNick());

            if (proxiedPlayer == null)
                return;

            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(),
                    "addperm " + proxiedPlayer.getName() + " " + packet.getFieldValue());
        } else if (packet.getField().equalsIgnoreCase("player-authenticated")) {
            ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(packet.getNick());

            if (proxiedPlayer == null)
                return;

            BungeePlayer profile = (BungeePlayer) CommonsGeneral.getProfileManager().getGamingProfile(packet.getNick());

            if (profile != null) {
                BungeeMain.runAsync(() -> {
                    try {
                        profile.getDataHandler().load(DataCategory.ACCOUNT, DataCategory.PREFERENCES);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

                String address = proxiedPlayer.getAddress().getAddress().getHostAddress();

                profile.set(DataType.LAST_IP, address);
                profile.setAddress(address);

                proxiedPlayer.sendMessage(BungeeMessages.AGORA_VOCE_TEM_UMA_SESSãO_NO_SERVIDOR);
            }
        } else if (packet.getField().equalsIgnoreCase("require-servers-info")) {
            CPacketCustomAction RESPONSE = lastResponse;

            if ((lastUpdate < System.currentTimeMillis()) || (RESPONSE == null)) {
                RESPONSE = new CPacketCustomAction().type(PacketType.BUNGEE_SEND_INFO)
                        .field("bungee-send-servers-info");

                for (NetworkServer networkServers : CommonsGeneral.getServersManager().getServers()) {
                    String prefix = networkServers.getServerType().getName().toLowerCase() + "-"
                            + networkServers.getServerId();
                    if (networkServers instanceof GameServer) {
                        RESPONSE.getJson().addProperty(prefix, ((GameServer) networkServers).toJsonGame().toString());
                    } else {
                        RESPONSE.getJson().addProperty(prefix, networkServers.toJson().toString());
                    }
                }

                lastUpdate = System.currentTimeMillis() + 200;
                lastResponse = RESPONSE;
            } else {
                RESPONSE.setTimestamp(System.currentTimeMillis());
            }

            Server.getInstance().sendPacket(socket, RESPONSE);
        }
    }

    @SuppressWarnings("deprecation")
    private void handleUpdateSkin(CPacketCustomAction packet, final Socket socket) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(packet.getNick());

        if (proxiedPlayer == null)
            return;

        BungeeMain.runAsync(() -> {
            try {
                SkinStorage.createSkin(packet.getField());

                SkinStorage.getOrCreateSkinForPlayer(packet.getField());
                SkinStorage.setPlayerSkin(packet.getNick(), packet.getField());

                BungeeMain.runLater(() -> {
                    SkinApplier.fastApply(proxiedPlayer, packet.getField(), false);

                    Server.getInstance().sendPacket(socket,
                            new CPacketCustomAction(proxiedPlayer.getName()).type(PacketType.BUKKIT_PLAYER_RESPAWN));

                    proxiedPlayer.sendMessage(BungeeMessages.SKIN_ATUALIZADA);
                });
            } catch (SkinRequestException e) {
                proxiedPlayer.sendMessage(BungeeMessages.ERROR_ON_UPDATE_SKIN);
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void handleSetSkin(CPacketCustomAction packet) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(packet.getNick());

        if (proxiedPlayer == null)
            return;

        BungeeMain.runAsync(() -> {
            try {
                SkinStorage.getOrCreateSkinForPlayer(packet.getField());
                SkinStorage.setPlayerSkin(packet.getNick(), packet.getField());

                SkinApplier.applySkin(proxiedPlayer, packet.getField());
                BungeeMain.runLater(() -> proxiedPlayer.sendMessage(BungeeMessages.SKIN_ATUALIZADA));
            } catch (SkinRequestException e) {
                proxiedPlayer.sendMessage(BungeeMessages.ERROR_ON_CHANGE_SKIN);
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void handleSetRandomSkin(CPacketCustomAction packet) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(packet.getNick());

        if (proxiedPlayer == null)
            return;

        BungeeMain.runAsync(() -> {
            try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
                PreparedStatement preparedStatement = connection
                        .prepareStatement("SELECT nick FROM skins ORDER BY RAND() LIMIT 1");
                ResultSet result = preparedStatement.executeQuery();

                if (result.next()) {
                    SkinApplier.fastApply(proxiedPlayer, result.getString("nick"));
                }

                BungeeMain.runLater(() -> proxiedPlayer.sendMessage(BungeeMessages.SKIN_ATUALIZADA));

                result.close();
                preparedStatement.close();
            } catch (SQLException ex) {
                proxiedPlayer.sendMessage(BungeeMessages.ERROR_ON_CHANGE_SKIN);
            }
        });
    }

    private void handleSetFastSkin(CPacketCustomAction packet, Socket socket) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(packet.getNick());

        if (proxiedPlayer == null)
            return;

        SkinApplier.fastApply(proxiedPlayer, packet.getField(), false);

        Server.getInstance().sendPacket(socket,
                new CPacketCustomAction(proxiedPlayer.getName()).type(PacketType.BUKKIT_PLAYER_RESPAWN));
    }

    private void handleReceivedServerData(final CPacketCustomAction packet) {
        CommonsGeneral.getServersManager().updateServerData(packet.getJson());
    }

    private void handleBukkitAccountRequest(final CPacketCustomAction packet, final Socket socket) {
        BungeePlayer profile = (BungeePlayer) CommonsGeneral.getProfileManager().getGamingProfile(packet.getNick());

        if (profile == null) {
            BungeeMain.console(packet.getNick()
                    + " requisitou que sua conta fosse carregada, e o mesmo nao se encontra no servidor.");
            return;
        }

        CPacketCustomAction PACKET = new CPacketCustomAction(packet.getNick(), packet.getUniqueId())
                .type(PacketType.BUKKIT_RECEIVE_ACCOUNT_FROM_BUNGEECORD);

        List<DataCategory> datasToSend = new ArrayList<>();

        datasToSend.add(DataCategory.ACCOUNT);
        datasToSend.add(DataCategory.PREFERENCES);

        if (packet.getServerType().isPvP()) {
            datasToSend.add(DataCategory.KITPVP);
        }

        if (packet.getServerType().isHardcoreGames(true)) {
            datasToSend.add(DataCategory.HARDCORE_GAMES);
        }

        if (packet.getServerType() == ServerType.GLADIATOR) {
            datasToSend.add(DataCategory.GLADIATOR);
        }

        if (packet.getServerType() == ServerType.LOGIN) {
            datasToSend.add(DataCategory.REGISTER);
        }

        int categoryID = 1;

        for (DataCategory datas : datasToSend) {
            PACKET.getJson().addProperty("dataCategory-" + categoryID,
                    profile.getDataHandler().buildJSON(datas, profile.isValidSession()).toString());
            categoryID++;
        }

        Server.getInstance().getServerGeneral().sendPacket(socket, PACKET);

        datasToSend.clear();
    }

    private void handleBukkitSendDatas(final CPacketCustomAction packet) {
        BungeePlayer profile = (BungeePlayer) CommonsGeneral.getProfileManager().getGamingProfile(packet.getNick());

        if (profile == null) {
            BungeeMain.console(packet.getNick() + " recebeu uma atualizaçao de categoria e nao possui profile");
            return;
        }

        JsonObject json = CommonsConst.PARSER.parse(packet.getJson().get("dataCategory-1").getAsString())
                .getAsJsonObject();

        profile.getDataHandler().loadFromJSON(DataCategory.getCategoryByName(json.get("dataCategory-name").getAsString()), json);
    }
}