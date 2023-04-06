package com.br.guilhermematthew.nowly.commons.bungee.account;

import com.br.guilhermematthew.nowly.commons.bungee.account.permission.BungeePlayerPermissions;
import com.br.guilhermematthew.nowly.commons.common.clan.ClanManager;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.profile.GamingProfile;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class BungeePlayer extends GamingProfile {

    private boolean inStaffChat = false,
            inClanChat = false;

    private long lastReport;
    private long lastLoggedOut;

    private String loginOnServer;

    public BungeePlayer(String nick, String address, UUID uniqueId) {
        super(nick, address, uniqueId);
        setLastReport(0L);
        setLastLoggedOut(0L);
        setLoginOnServer("Lobby");
    }

    public void handleLogin(final ProxiedPlayer proxiedPlayer) {
        BungeePlayerPermissions.injectPermissions(proxiedPlayer, getGroup().getName());

        if (!getString(DataType.CLAN).equalsIgnoreCase("Nenhum")) {
            ClanManager.getClan(getString(DataType.CLAN)).addOnline(proxiedPlayer);
        }

        try {
            getPunishmentHistoric().loadMutes();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleQuit() {
        set(DataType.LAST_LOGGED_OUT, System.currentTimeMillis());
        setLastLoggedOut(System.currentTimeMillis());
    }

    public boolean isValidSession() {
        return System.currentTimeMillis() < getLong(DataType.LAST_LOGGED_IN) + TimeUnit.HOURS.toMillis(4);
    }

    public boolean isAvailableToJoin() {
        return System.currentTimeMillis() > getLastLoggedOut() + TimeUnit.SECONDS.toMillis(4);
    }

    public boolean podeReportar() {
        return getLastReport() + TimeUnit.SECONDS.toMillis(30) < System.currentTimeMillis();
    }

    public boolean isValidPlayer() {
        return getPlayer() != null && getPlayer().getServer() != null;
    }

    public ProxiedPlayer getPlayer() {
        return ProxyServer.getInstance().getPlayer(getUniqueId());
    }
}