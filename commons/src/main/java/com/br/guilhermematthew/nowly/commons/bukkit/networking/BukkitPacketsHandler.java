package com.br.guilhermematthew.nowly.commons.bukkit.networking;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.fake.FakeAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.vanish.VanishAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerChangeGroupEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.listeners.LoginListener;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.profile.GamingProfile;
import com.br.guilhermematthew.nowly.commons.common.profile.token.AcessToken;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.types.GameServer;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.types.NetworkServer;
import com.br.guilhermematthew.nowly.commons.custompackets.CommonPacketHandler;
import com.br.guilhermematthew.nowly.commons.custompackets.PacketType;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketAction;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketCustomAction;
import com.br.guilhermematthew.servercommunication.client.Client;
import com.google.gson.JsonObject;
import com.mojang.authlib.properties.Property;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.net.Socket;

public class BukkitPacketsHandler extends CommonPacketHandler {

    @Override
    public void handleCPacketAction(CPacketAction packet, final Socket socket) {
        if (packet.getType().equalsIgnoreCase("TimedOut")) {
            Client.getInstance().getClientConnection().debug("Connection timed out!");
        }
    }

    @Override
    public void handleCPacketPlayerAction(CPacketCustomAction packet, Socket socket) {
        PacketType type = packet.getPacketType();

        if (type != null) {
            switch (type) {
                case BUKKIT_RECEIVE_ACCOUNT_FROM_BUNGEECORD:
                    handleAccountReceive(packet);
                    break;
                case BUKKIT_PLAYER_RESPAWN:
                    handleRespawnPlayer(packet);
                    break;
                case BUKKIT_RECEIVE_SKIN_DATA:
                    handleSkinData(packet);
                    break;
                case BUKKIT_GO:
                    handleGo(packet);
                    break;
                case BUNGEE_SEND_UPDATED_STATS:
                    handleBungeeSendStats(packet);
                    break;
                case BUNGEE_SEND_KICK:
                    handleKick(packet);
                    break;
                case BUNGEE_SEND_PLAYER_ACTION:
                    handleBungeeSendPlayerAction(packet);
                    break;
                case BUNGEE_SEND_INFO:
                    handleReceiveInfo(packet);
                    break;
                default:
                    break;
            }
        }
    }

    private void handleGo(CPacketCustomAction packet) {
        val staff = Bukkit.getPlayerExact(packet.getField());

        if(staff != null) {
            if (!VanishAPI.inAdmin(staff)) VanishAPI.changeAdmin(staff);
            staff.teleport(Bukkit.getPlayer(packet.getUniqueId()));
        }
    }

    private void handleReceiveInfo(CPacketCustomAction packet) {
        if (packet.getField().equalsIgnoreCase("bukkit-server-turn-on")) {
            for (Player onlines : Bukkit.getOnlinePlayers()) {
                onlines.sendMessage(packet.getFieldValue());
                onlines.playSound(onlines.getLocation(), Sound.ARROW_HIT, 1, 1);
            }
        } else if (packet.getField().equalsIgnoreCase("teleport-player-from-report")) {
            Player target = BukkitServerAPI.getExactPlayerByNick(packet.getNick());

            if (target != null) {
                Player reportado = BukkitServerAPI.getExactPlayerByNick(packet.getField());

                if (reportado == null) {
                    target.sendMessage("§cJogador offline.");
                    return;
                }

                if (!VanishAPI.inAdmin(target)) {
                    VanishAPI.changeAdmin(target, true);
                }

                BukkitMain.runLater(() -> target.teleport(reportado), 12);
            }
        } else if (packet.getField().equalsIgnoreCase("bungee-send-servers-info")) {

            for (NetworkServer networkServers : CommonsGeneral.getServersManager().getServers()) {
                String prefix = networkServers.getServerType().getName().toLowerCase() + "-" + networkServers.getServerId();

                if (!packet.getJson().has(prefix)) {
                    CommonsGeneral.error("Erro ao tentar processar informaçőes de um servidor -> " + networkServers.getServerType().getName() + "-" + networkServers.getServerId());
                    continue;
                }

                JsonObject INFO;

                try {
                    INFO = CommonsConst.PARSER.parse(packet.getJson().get(prefix).getAsString()).getAsJsonObject();

                    if (networkServers instanceof GameServer) {
                        CommonsGeneral.getServersManager().getGameServer(networkServers.getServerType(), networkServers.getServerId()).updateData(INFO);
                    } else {
                        CommonsGeneral.getServersManager().updateServerData(networkServers.getServerType(), networkServers.getServerId(), INFO);
                    }
                } catch (Exception ex) {
                    CommonsGeneral.error("Error on update server data! -> " + ex.getLocalizedMessage());
                }
            }

        }
    }

    private void handleKick(CPacketCustomAction packet) {
        Player target = BukkitServerAPI.getExactPlayerByNick(packet.getNick());

        BukkitMain.runSync(() -> {
            if (target != null && target.isOnline()) {
                target.kickPlayer(packet.getField());
            }
        });
    }

    private void handleRespawnPlayer(final CPacketCustomAction packet) {
        Player target = BukkitServerAPI.getExactPlayerByNick(packet.getNick());

        if (target != null && target.isOnline()) FakeAPI.respawnPlayer(target);
    }

    private void handleSkinData(final CPacketCustomAction packet) {
        val player = BukkitMain.getBukkitPlayer(packet.getUniqueId());
        if (player == null) return;

        player.setLastSkin(new Property(packet.getFieldValue(), packet.getExtraValue(), packet.getExtraValue2()));
        if (packet.getField() != null && packet.getField().equals("sendPacket")) {
            if (player.getPlayer() != null) FakeAPI.respawnPlayer(player.getPlayer());
        }
    }

    private void handleAccountReceive(final CPacketCustomAction packet) {
        GamingProfile profile = LoginListener.connectionQueue.get(packet.getUniqueId());

        if (profile != null) {
            for (int i = 1; i < 10; i++) {
                if (packet.getJson().has("dataCategory-" + i)) {
                    JsonObject json = CommonsConst.PARSER.parse(packet.getJson().get("dataCategory-" + i).getAsString())
                            .getAsJsonObject();

                    profile.getDataHandler().loadFromJSON(
                            DataCategory.getCategoryByName(json.get("dataCategory-name").getAsString()), json);
                }
            }

            profile.getTokenListener().onAcessToken(AcessToken.ACCEPTED);
        }
    }

    private void handleBungeeSendPlayerAction(final CPacketCustomAction packet) {
        Player player = BukkitServerAPI.getExactPlayerByNick(packet.getNick());
        if (player == null) {
            BukkitMain.console(packet.getNick() + " recebeu uma atualizaçao de categoria e nao possui profile");
            return;
        }

        BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());

        if (bukkitPlayer == null) {
            BukkitMain.console(packet.getNick() + " recebeu uma atualizaçao e nao possui profile");
            return;
        }

        if (packet.getField().equals("update-data")) {
            if (packet.getFieldValue().equals("clan")) {
                bukkitPlayer.set(DataType.CLAN, packet.getExtraValue());
                bukkitPlayer.updateTag(bukkitPlayer.getPlayer(), bukkitPlayer.getActualTag(), true);

                BukkitMain.runAsync(() -> bukkitPlayer.getDataHandler().saveCategory(DataCategory.ACCOUNT));
            }
        }
    }

    private void handleBungeeSendStats(final CPacketCustomAction packet) {
        Player player = BukkitServerAPI.getExactPlayerByNick(packet.getNick());
        if (player == null) {
            BukkitMain.console(packet.getNick() + " recebeu uma atualizaçao de categoria e nao possui profile");
            return;
        }

        BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());

        if (bukkitPlayer == null) {
            BukkitMain.console(packet.getNick() + " recebeu uma atualizaçao de categoria e nao possui profile");
            return;
        }

        JsonObject json = CommonsConst.PARSER.parse(packet.getJson().get("dataCategory-1").getAsString())
                .getAsJsonObject();

        bukkitPlayer.getDataHandler()
                .loadFromJSON(DataCategory.getCategoryByName(json.get("dataCategory-name").getAsString()), json);

        if (!packet.getField().isEmpty()) {
            if (packet.getField().equalsIgnoreCase("group")) {
                Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeGroupEvent(player, bukkitPlayer.getGroup()));
                bukkitPlayer.injectPermissions(player);
            } else if (packet.getField().equalsIgnoreCase("add-perm")) {
                final String permission = packet.getFieldValue();

                bukkitPlayer.getPlayerAttachment().addPermission(permission);
                bukkitPlayer.getData(DataType.PERMISSIONS).getList().add(permission);
                BukkitMain.runAsync(() -> bukkitPlayer.getDataHandler().saveCategory(DataCategory.ACCOUNT));
            }
        }
    }
}