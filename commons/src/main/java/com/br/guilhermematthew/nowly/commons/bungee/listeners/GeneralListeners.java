package com.br.guilhermematthew.nowly.commons.bungee.listeners;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.commands.register.ClearAccountCommand;
import com.br.guilhermematthew.nowly.commons.bungee.events.BungeeUpdateEvent;
import com.br.guilhermematthew.nowly.commons.bungee.events.BungeeUpdateEvent.BungeeUpdateType;
import com.br.guilhermematthew.nowly.commons.bungee.manager.premium.PremiumMapManager;
import com.br.guilhermematthew.nowly.commons.bungee.skins.SkinStorage;
import com.br.guilhermematthew.nowly.commons.common.punishment.PunishmentManager;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.types.NetworkServer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GeneralListeners implements Listener {

    private int MINUTES = 0;

    @EventHandler
    public void onSecond(BungeeUpdateEvent event) {
        if (event.getType() != BungeeUpdateType.SEGUNDO) {
            return;
        }

        if (event.getSeconds() % 2 == 0) {
            NetworkServer bungee = CommonsGeneral.getServersManager().getNetworkServer("bungeecord");
            bungee.setOnline(true);
            bungee.setLastUpdate(System.currentTimeMillis());
            bungee.setOnlines(ProxyServer.getInstance().getOnlineCount());
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onMinute(BungeeUpdateEvent event) {
        if (event.getType() != BungeeUpdateType.MINUTO) return;

        MINUTES++;

        if (MINUTES % BungeeMain.getManager().getMinutos() == 0) {
            if (BungeeMain.getManager().getMessages().size() == 0) {
                return;
            }

            int actualID = BungeeMain.getManager().getActualMessageID();

            if (actualID >= BungeeMain.getManager().getMessages().size()) {
                actualID = 0;
            }

            final String message = BungeeMain.getManager().getMessages().get(actualID);

            ProxyServer.getInstance().getPlayers().forEach(onlines -> onlines.sendMessage(message));
            BungeeMain.getManager().setActualMessageID(actualID + 1);
        } else if (MINUTES % 30 == 0) {
            CommonsGeneral.getUUIDFetcher().checkCache(() -> SkinStorage.checkCache(() -> PremiumMapManager.checkCache(PunishmentManager::checkCache)));
        }
    }

    @EventHandler
    public void onHour(BungeeUpdateEvent event) {
        if (event.getType() != BungeeUpdateType.HORA) {
            return;
        }

        BungeeMain.runAsync(() -> {
            List<String> toRemove = new ArrayList<>();

            try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM accounts_to_delete");
                ResultSet result = preparedStatement.executeQuery();

                while (result.next()) {
                    String time = result.getString("timestamp");

                    long timestamp = 0L;

                    try {
                        timestamp = Long.parseLong(time);
                    } catch (Exception ex) {
                        toRemove.add(result.getString("nick"));
                    }

                    if (timestamp + TimeUnit.DAYS.toMillis(7) < System.currentTimeMillis()) {
                        toRemove.add(result.getString("nick"));
                    }
                }

                result.close();
                preparedStatement.close();

            } catch (SQLException e) {
                BungeeMain.console("§cOcorreu um erro ao tentar checar a lista de jogadores banidos para serem removidos -> " + e.getLocalizedMessage());
                e.printStackTrace();
            }

            if (toRemove.size() != 0) {
                for (String nick : toRemove) {
                    ClearAccountCommand.clearAccount(nick);
                }
            }

            toRemove.clear();
        });
    }
}