package com.br.guilhermematthew.nowly.commons.bukkit.listeners;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitSettings;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.common.profile.GamingProfile;
import com.br.guilhermematthew.nowly.commons.common.profile.token.AcessToken;
import com.br.guilhermematthew.nowly.commons.custompackets.PacketType;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketCustomAction;
import com.br.guilhermematthew.servercommunication.client.Client;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class LoginListener implements Listener {

    public final static HashMap<UUID, GamingProfile> connectionQueue = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLoad(AsyncPlayerPreLoginEvent event) {
        if (!isRunning()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, BukkitMessages.THE_SERVER_IS_LOADING);
            return;
        }

        UUID uniqueId = event.getUniqueId();

        if (Bukkit.getPlayer(uniqueId) == null) {
            GamingProfile profile;

            connectionQueue.put(uniqueId, profile = new BukkitPlayer(event.getName(), "", uniqueId));

            profile.setTokenListener(accessToken -> {
                profile.setAcessToken(accessToken);

                synchronized (profile) {
                    profile.notifyAll();
                }
            });

            profile.sendPacket(new CPacketCustomAction(event.getName(), uniqueId).
                    type(PacketType.BUKKIT_REQUEST_ACCOUNT_TO_BUNGEECORD));

            synchronized (profile) {
                try {
                    profile.wait(6000);
                } catch (InterruptedException e) {
                    connectionQueue.remove(uniqueId);

                    event.setKickMessage("§cFalha no login: " + e);
                    event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);

                    e.printStackTrace();
                }
            }

            if (profile.getAcessToken() != null) {
                if (profile.getAcessToken() == AcessToken.REJECTED) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cSeu acesso foi invalidado pelo servidor.");
                } else {
                    CommonsGeneral.getProfileManager().addGamingProfile(uniqueId, profile);
                }
            } else {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        String.format(BukkitMessages.THE_SERVER_NOT_RECEIVED_PLAYER_DATA, BukkitMain.getServerType().getName()));
            }

            connectionQueue.remove(uniqueId);
        } else {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cVocê já está conectado ao servidor.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRemoveAccount(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.KICK_OTHER) {
            return;
        }

        if (!CommonsGeneral.getProfileManager().containsProfile(event.getUniqueId())) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    String.format(BukkitMessages.THE_SERVER_NOT_RECEIVED_PLAYER_DATA, BukkitMain.getServerType().getName()));

            CommonsGeneral.getProfileManager().removeGamingProfile(event.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(PlayerQuitEvent event) {
        CommonsGeneral.getProfileManager().removeGamingProfile(event.getPlayer().getUniqueId());
    }

    private boolean isRunning() {
        if (!BukkitMain.isLoaded()) return false;
        if (!BukkitSettings.LOGIN_OPTION) return false;

        return Client.getInstance().getClientConnection().isConnected();
    }
}