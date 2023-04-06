package com.br.guilhermematthew.nowly.commons.bungee.manager.premium;

import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.common.utility.mojang.UUIDFetcher.UUIDFetcherException;
import lombok.val;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PreLoginEvent;

public class AsyncPremiumCheck implements Runnable {

    private final PendingConnection connection;
    private final PreLoginEvent preLoginEvent;

    public AsyncPremiumCheck(PreLoginEvent event, PendingConnection connection) {
        this.preLoginEvent = event;
        this.connection = connection;
    }

    @SuppressWarnings("deprecation")
    public void run() {
        try {
            if (!connection.isConnected()) return;

            try {
                PremiumMapManager.load(connection.getName());
            } catch (UUIDFetcherException ex) {
                preLoginEvent.setCancelled(true);
                preLoginEvent.setCancelReason(BungeeMessages.ERROR_ON_LOAD_PREMIUM_MAP);
            }

            if (preLoginEvent.isCancelled()) return;
            val data = PremiumMapManager.getPremiumMap(connection.getName());

            if (data != null) {
                connection.setOnlineMode(data.isPremium());
            }

            if (PremiumMapManager.getChangedNicks().contains(connection.getName())) {
                PremiumMapManager.getChangedNicks().remove(connection.getName());

                preLoginEvent.setCancelled(true);
                preLoginEvent.setCancelReason(BungeeMessages.PREMIUM_MAP_DETECT_NICK_CHANGE);
            }
        } finally {
            preLoginEvent.completeIntent(BungeeMain.getInstance());
        }
    }
}