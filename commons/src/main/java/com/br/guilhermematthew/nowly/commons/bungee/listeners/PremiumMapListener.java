package com.br.guilhermematthew.nowly.commons.bungee.listeners;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.bungee.manager.premium.AsyncPremiumCheck;
import com.br.guilhermematthew.nowly.commons.bungee.manager.premium.PremiumMapManager;
import lombok.val;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PremiumMapListener implements Listener {

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPreLogin(PreLoginEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!BungeeMain.isLoaded()) {
            event.setCancelled(true);
            event.setCancelReason(BungeeMessages.BUNGEECORD_CARREGANDO);
            return;
        }

        if (!CommonsGeneral.getServersManager().getNetworkServer("Lobby").isOnline()) {
            event.setCancelReason(BungeeMessages.ERROR_ON_CONNECT_TO_X_SERVER.replace("%servidor%", "Lobby"));
            event.setCancelled(true);
            return;
        }

        val name = event.getConnection().getName();
        if (name.length() < 3 || name.length() > 16 || !event.getConnection().getName().matches("^\\w*$")) {
            event.setCancelled(true);
            event.setCancelReason(BungeeMessages.INVALID_NICKNAME);
            return;
        }

        val data = PremiumMapManager.getPremiumMap(event.getConnection().getName());
        if (data != null) {
            event.getConnection().setOnlineMode(data.isPremium());
        } else {
            event.registerIntent(BungeeMain.getInstance());

            AsyncPremiumCheck asyncPremiumCheck = new AsyncPremiumCheck(event, event.getConnection());
            BungeeMain.runAsync(asyncPremiumCheck);
        }
    }
}