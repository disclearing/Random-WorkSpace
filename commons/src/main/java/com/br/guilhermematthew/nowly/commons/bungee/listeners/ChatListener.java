package com.br.guilhermematthew.nowly.commons.bungee.listeners;


import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.bungee.account.BungeePlayer;
import com.br.guilhermematthew.nowly.commons.common.clan.Clan;
import com.br.guilhermematthew.nowly.commons.common.clan.ClanManager;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.punishment.types.Mute;
import com.br.guilhermematthew.nowly.commons.common.utility.system.DateUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatListener implements Listener {

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onChatMessage(ChatEvent event) {
        if (event.isCommand() && !event.getMessage().toLowerCase().startsWith("/tell")) return;
        if (event.isCancelled()) return;

        ProxiedPlayer proxiedPlayer = (ProxiedPlayer) event.getSender();

        if (!CommonsGeneral.getProfileManager().containsProfile(proxiedPlayer.getName())) {
            proxiedPlayer.disconnect(TextComponent.fromLegacyText("§4§lERRO\n\n§fVocê não possuí uma sessão no servidor."));
            event.setCancelled(true);
            return;
        }

        BungeePlayer bungeePlayer = BungeeMain.getBungeePlayer(proxiedPlayer.getName());

        Mute mute = bungeePlayer.getPunishmentHistoric().getActualMute();

        if (mute != null) {
            if (mute.isExpired()) {
                mute.unmute("CONSOLE > Punição expirada");
            } else {
                if (mute.isPermanent()) {
                    event.setCancelled(true);
                    proxiedPlayer.sendMessage(BungeeMessages.MUTADO_PERMANENTEMENTE);
                } else {
                    event.setCancelled(true);
                    proxiedPlayer.sendMessage(BungeeMessages.MUTADO_TEMPORARIAMENTE.replace("%tempo%",
                            DateUtils.formatDifference(mute.getPunishmentTime())));
                }
            }
        }

        if (!event.isCancelled() && !event.getMessage().toLowerCase().startsWith("/tell")) {
            String message = event.getMessage();

            if (message.contains("%")) {
                message = message.replaceAll("%", "%%");
            }

            Groups playerGroup = bungeePlayer.getGroup();

            if (!event.isCancelled()) {

                if (message.contains("&")) {
                    if (playerGroup.getLevel() > Groups.MEMBRO.getLevel()) {
                        message = message.replaceAll("&", "§");
                    }
                }

                if (bungeePlayer.isInClanChat()) {
                    event.setCancelled(true);

                    Clan clan = ClanManager.getClan(bungeePlayer.getString(DataType.CLAN));

                    String colorNick = (clan.getOwner().equals(proxiedPlayer.getName()) ? "§4" :
                            clan.getAdminList().contains(proxiedPlayer.getName()) ? "§c" : "§7");

                    String formatado = BungeeMessages.CLAN_CHAT_PREFIX + " " + colorNick + proxiedPlayer.getName() + ": §f" + message;

                    for (Object object : clan.getOnlines()) {
                        ProxiedPlayer onlines = (ProxiedPlayer) object;
                        onlines.sendMessage(formatado);
                    }
                } else if (bungeePlayer.isInStaffChat()) {
                    event.setCancelled(true);

                    if (!bungeePlayer.getBoolean(DataType.RECEIVE_STAFFCHAT_MESSAGES)) {
                        event.setCancelled(true);
                        proxiedPlayer.sendMessage(BungeeMessages.VOCE_ESTA_TENTANDO_FALAR_NO_STAFFCHAT_COM_ELE_DESATIVADO);
                    } else {
                        final String formatado =
                                BungeeMessages.STAFFCHAT_PREFIX + " " + playerGroup.getColor() + "§l" + playerGroup.getName().toUpperCase() + " " + playerGroup.getColor() +
                                        proxiedPlayer.getName() + ": §f" + message;

                        for (ProxiedPlayer proxiedPlayers : ProxyServer.getInstance().getPlayers()) {
                            if (bungeePlayer.getGroup().getLevel() >= Groups.TRIAL.getLevel()) {
                                if (BungeeMain.isValid(proxiedPlayers)) {
                                    if (bungeePlayer.getBoolean(DataType.RECEIVE_STAFFCHAT_MESSAGES)) {
                                        proxiedPlayer.sendMessage(formatado);
                                    }
                                }
                            }
                            return;
                        }
                    }
                }
            }
        }
    }
}