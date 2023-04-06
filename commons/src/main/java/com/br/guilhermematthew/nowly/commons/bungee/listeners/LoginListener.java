package com.br.guilhermematthew.nowly.commons.bungee.listeners;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.bungee.account.BungeePlayer;
import com.br.guilhermematthew.nowly.commons.bungee.account.permission.BungeePlayerPermissions;
import com.br.guilhermematthew.nowly.commons.bungee.manager.premium.PremiumMapManager;
import com.br.guilhermematthew.nowly.commons.bungee.skins.SkinApplier;
import com.br.guilhermematthew.nowly.commons.bungee.skins.SkinStorage;
import com.br.guilhermematthew.nowly.commons.common.clan.ClanManager;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.punishment.PunishmentManager;
import com.br.guilhermematthew.nowly.commons.common.punishment.types.Ban;
import com.br.guilhermematthew.nowly.commons.common.utility.system.DateUtils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LoginListener implements Listener {

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onLogin(final LoginEvent event) {
        if (event.isCancelled()) {
            return;
        }

        String nick = event.getConnection().getName(),
                address = event.getConnection().getAddress().getAddress().getHostAddress();

        if (!PremiumMapManager.containsMap(nick)) {
            event.setCancelled(true);
            event.setCancelReason(BungeeMessages.PREMIUM_MAP_NOT_LOADED);
            return;
        }

        if (BungeeMain.getManager().isGlobalWhitelist()) {
            if (!BungeeMain.getManager().getWhitelistPlayers().contains(nick.toLowerCase())) {
                event.setCancelled(true);
                event.setCancelReason(BungeeMessages.SERVIDOR_EM_MANUTENÇãO);
                return;
            }
        }

        event.registerIntent(BungeeMain.getInstance());

        BungeeMain.runAsync(() -> {
            UUID uniqueId = CommonsGeneral.getUUIDFetcher().getOfflineUUID(event.getConnection().getName());
            BungeePlayer profile = (BungeePlayer) CommonsGeneral.getProfileManager().getGamingProfile(uniqueId);

            if (profile != null && !profile.isAvailableToJoin()) {
                event.setCancelled(true);
                event.setCancelReason(BungeeMessages.SUA_CONTA_ESTA_SENDO_DESCARREGADA);
                event.completeIntent(BungeeMain.getInstance());
                return;
            }

            if (event.isCancelled()) {
                event.completeIntent(BungeeMain.getInstance());
                return;
            }

            if (profile == null) profile = new BungeePlayer(event.getConnection().getName(), address, uniqueId);

            Ban ban = PunishmentManager.getBanCache(nick);

            if (ban == null) {
                try {
                    profile.getPunishmentHistoric().loadBans();
                } catch (SQLException ex) {
                    CommonsGeneral.error("onLogin() : loadBans() : LoginListener.Java -> " + ex.getLocalizedMessage());
                    event.setCancelled(true);
                    event.completeIntent(BungeeMain.getInstance());
                    return;
                }
                ban = profile.getPunishmentHistoric().getActualBan();
                if (ban != null) {
                    PunishmentManager.addCache(nick, ban);
                }
            }

            if (ban != null && ban.isPunished()) {
                if (!ban.isExpired()) {
                    event.setCancelled(true);

                    if (ban.isPermanent()) {
                        event.setCancelReason(TextComponent.fromLegacyText(BungeeMessages.VOCE_ESTA_PERMANENTEMENTE_BANIDO
                                .replace("%motivo%", ban.getMotive()).replace("%baniu%", ban.getBannedBy())));
                    } else {
                        event.setCancelReason(TextComponent.fromLegacyText(BungeeMessages.VOCE_ESTA_TEMPORARIAMENTE_BANIDO
                                .replace("%motivo%", ban.getMotive()).replace("%baniu%", ban.getBannedBy())
                                .replace("%tempo%", DateUtils.formatDifference(ban.getPunishmentTime()))));
                    }
                } else {
                    ban.unban("CONSOLE - BAN EXPIRADO");
                    PunishmentManager.removeCache(nick);
                }
            }

            if (event.isCancelled()) {
                event.completeIntent(BungeeMain.getInstance());
                return;
            }


            try {
                SkinApplier.handleSkin(event.getConnection());
            } catch (Exception ex) {
                event.setCancelled(true);
                event.setCancelReason(BungeeMessages.ERROR_ON_APPLY_SKIN);

                BungeeMain.console("Ocorreu um erro ao tentar setar a skin de um jogador -> " + ex.getLocalizedMessage());

                ex.printStackTrace();

                event.completeIntent(BungeeMain.getInstance());
                return;
            }

            if (availableToLoad(profile, uniqueId, address, event.getConnection().isOnlineMode())) {
                try {
                    profile.getDataHandler().load(DataCategory.ACCOUNT, DataCategory.PREFERENCES);
                } catch (SQLException ex) {
                    BungeeMain.console("§cOcorreu um erro ao tentar carregar as categorias principais de um player -> "
                            + ex.getLocalizedMessage());
                    event.setCancelled(true);
                    event.setCancelReason(BungeeMessages.ERROR_ON_LOAD_ACCOUNT);
                    event.completeIntent(BungeeMain.getInstance());
                    return;
                }

                profile.getDataHandler().getData(DataType.LAST_IP).setValue(address);
                profile.getDataHandler().getData(DataType.LAST_LOGGED_IN).setValue(System.currentTimeMillis());
                profile.setAddress(address);

                if (!profile.getDataHandler().getString(DataType.CLAN).equalsIgnoreCase("Nenhum")) {
                    ClanManager.load(profile.getString(DataType.CLAN));
                }
            } else {
                profile.getDataHandler().loadDefault(DataCategory.ACCOUNT);
            }

            CommonsGeneral.getProfileManager().addGamingProfile(uniqueId, profile);

            event.completeIntent(BungeeMain.getInstance());
        });
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer proxiedPlayer = event.getPlayer();

        BungeeMain.runAsync(() -> BungeeMain.runLater(() -> {
            if (proxiedPlayer == null || proxiedPlayer.getServer() == null) {
                return;
            }

            if (BungeeMain.getBungeePlayer(proxiedPlayer.getName()) != null) {
                BungeeMain.getBungeePlayer(proxiedPlayer.getName()).handleLogin(proxiedPlayer);
            }
        }, 1, TimeUnit.SECONDS));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDisconnect(final PlayerDisconnectEvent event) {
        BungeeMain.runAsync(() -> {
            ProxiedPlayer proxiedPlayer = event.getPlayer();

            SkinStorage.removeFromHash(proxiedPlayer.getName());

            if (proxiedPlayer.getServer() != null) {
                BungeePlayerPermissions.clearPermissions(proxiedPlayer);

                BungeePlayer profile = (BungeePlayer) CommonsGeneral.getProfileManager()
                        .getGamingProfile(proxiedPlayer.getName());

                if (profile != null) {
                    profile.handleQuit();

                    final String serverName = proxiedPlayer.getServer().getInfo().getName().toLowerCase();
                    if (serverName.equals("login")) {
                        return;
                    }

                    if(profile.getGroup() != null && profile.getGroup().getLevel() > Groups.MEMBRO.getLevel() || !serverName.equalsIgnoreCase("lobby")) {
                        if (serverName.contains("hg") || serverName.equals("evento") || serverName.contains("hardcoregames")) {
                            profile.setLoginOnServer("LobbyHardcoreGames");
                        } else if (serverName.contains("sw")) {
                            profile.setLoginOnServer("LobbySW");
                        } else if (serverName.contains("tb")) {
                            profile.setLoginOnServer("LobbyTB");
                        } else if (serverName.contains("bw")) {
                            profile.setLoginOnServer("LobbyBW");
                        } else {
                            profile.setLoginOnServer("LobbyPvP");
                        }
                    } else {
                        profile.setLoginOnServer("Lobby");
                    }
                }
            }
        });
    }

    private boolean availableToLoad(final BungeePlayer profile, final UUID uniqueId, final String address, boolean premium) {
        if (premium) return true;
        if (!CommonsGeneral.getProfileManager().containsProfile(uniqueId)) return false;

        return profile.isValidSession() && profile.getAddress().equals(address);
    }
}