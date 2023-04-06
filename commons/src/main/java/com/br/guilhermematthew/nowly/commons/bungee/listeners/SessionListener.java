package com.br.guilhermematthew.nowly.commons.bungee.listeners;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.bungee.account.BungeePlayer;
import com.br.guilhermematthew.nowly.commons.bungee.commands.register.EventoCommand;
import com.br.guilhermematthew.nowly.commons.bungee.events.BungeeUpdateEvent;
import com.br.guilhermematthew.nowly.commons.bungee.events.BungeeUpdateEvent.BungeeUpdateType;
import com.br.guilhermematthew.nowly.commons.common.profile.GamingProfile;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import lombok.val;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SessionListener implements Listener {

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerConnect(ServerConnectEvent event) {
        val proxiedPlayer = event.getPlayer();

        if(event.getTarget().getName().equals(ServerType.EVENTO.getName())) {
            val bp = BungeeMain.getBungeePlayer(proxiedPlayer.getName());

            if (!EventoCommand.hasEvento && !bp.isStaffer()) {
                event.setCancelled(true);
                BungeeMain.runLater(() -> proxiedPlayer.sendMessage(BungeeMessages.EVENTO_OFFLINE), 200);
                return;
            }
        }

        if (event.getReason() != ServerConnectEvent.Reason.JOIN_PROXY) return;
        String serverToConnect = "Login";

        String address = proxiedPlayer.getAddress().getAddress().getHostAddress();
        UUID uniqueId = CommonsGeneral.getUUIDFetcher().getOfflineUUID(proxiedPlayer.getName());
        BungeePlayer profile;

        if (CommonsGeneral.getProfileManager().containsProfile(uniqueId)) {
            profile = BungeeMain.getBungeePlayer(proxiedPlayer.getName());

            if (!proxiedPlayer.getPendingConnection().isOnlineMode()) {
                if (profile.isValidSession() && profile.getAddress().equals(address)) {
                    serverToConnect = profile.getLoginOnServer();
                }
            } else {
                serverToConnect = profile.getLoginOnServer();
            }
        }

        if (!CommonsGeneral.getServersManager().getNetworkServer(serverToConnect).isOnline() &&
                !serverToConnect.equalsIgnoreCase("Lobby") && !serverToConnect.equalsIgnoreCase("Login")) {
            serverToConnect = "Lobby";
        }

        if (CommonsGeneral.getServersManager().getNetworkServer(serverToConnect).isOnline()) {
            if (!event.getTarget().getName().equalsIgnoreCase(serverToConnect)) {
                event.setTarget(BungeeMain.getInstance().getProxy().getServerInfo(serverToConnect));
            }
        } else {
            event.setCancelled(true);

            proxiedPlayer.disconnect(BungeeMessages.ERROR_ON_CONNECT_TO_X_SERVER.replace("%servidor%", serverToConnect));
        }
    }

    @EventHandler
    public void onUpdate(BungeeUpdateEvent event) {
        if (event.getType() != BungeeUpdateType.HORA) {
            return;
        }

        List<UUID> profilesToRemove = new ArrayList<>();

        for (GamingProfile profile : CommonsGeneral.getProfileManager().getGamingProfiles()) {
            BungeePlayer bungeePlayer = (BungeePlayer) profile;

            if (bungeePlayer != null) {
                if (!bungeePlayer.isValidSession()) {
                    if (!bungeePlayer.isValidPlayer()) {
                        profilesToRemove.add(profile.getUniqueId());
                    }
                }

                bungeePlayer = null;
            } else {
                BungeeMain.console("Um BungeePlayer foi detectado como nulo.");
            }

            profile = null;
        }

        for (UUID uuid : profilesToRemove) {
            CommonsGeneral.getProfileManager().removeGamingProfile(uuid);
        }

        if (profilesToRemove.size() != 0) {
            BungeeMain.console("[Cache] Foram removidas " + profilesToRemove.size() + " contas do cache");
        }

        profilesToRemove.clear();
    }
}