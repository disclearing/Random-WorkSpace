package com.br.guilhermematthew.nowly.commons.bukkit.listeners;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitSettings;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.common.clan.Clan;
import com.br.guilhermematthew.nowly.commons.common.clan.ClanManager;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.profile.addons.League;
import com.br.guilhermematthew.nowly.commons.common.profile.addons.Medals;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.help.HelpTopic;
import org.bukkit.metadata.FixedMetadataValue;

public class ChatListener implements Listener {

    private final String CHAT_COOLDOWN_TAG = "chat-cooldown";
    private final String DELAY_COMMAND_TAG = "command-cooldown";
    private final Long CHAT_COOLDOWN_TIME = 2000L;
    private final Long COMMAND_COOLDOWN_TIME = 1000L;

    private final String[] cmdsBlockeds = {"/?", "/bukkit:", "/say", "/kill", "/msg", "/me", "/w", "/help",
            "minecraft:", "/calc", "//calc", "calc"};

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        BukkitPlayer profile = (BukkitPlayer) CommonsGeneral.getProfileManager().getGamingProfile(player.getUniqueId());

        if (!BukkitSettings.CHAT_OPTION && (profile.getGroup().getLevel() < Groups.TRIAL.getLevel())) {
            event.setCancelled(true);
            player.sendMessage(BukkitMessages.CHAT_ESTA_DESATIVADO);
            return;
        }

        if (profile.getGroup().getLevel() < Groups.TRIAL.getLevel()) { // MOD
            event.setCancelled(inChatCooldown(player));
        }

        if (event.isCancelled()) {
            return;
        }

        event.setCancelled(true);

        String message = event.getMessage();

        if (message.contains("%")) {
            message = message.replaceAll("%", "%%");
        }

        if (message.contains("&")) {
            if (profile.getGroup().getLevel() > Groups.MEMBRO.getLevel()) {
                message = message.replaceAll("&", "§");
            }
        }

        String prefix = "";

        Medals medal = Medals.getMedalById(profile.getInt(DataType.MEDAL));
        if (medal != null) prefix = medal.getColor() + medal.getSymbol() + " ";

        if (!profile.getString(DataType.CLAN).equalsIgnoreCase("Nenhum")) {
            Clan clan = ClanManager.getClan(profile.getString(DataType.CLAN));
            prefix = prefix + "§7[" + (clan.isPremium() ? "§6" : "") + clan.getTag() + "] ";
        }

        League league = League.getRanking(profile.getInt(DataType.XP));

        if (profile.getActualTag().getLevel() != Groups.MEMBRO.getLevel()) {
            prefix = prefix + profile.getActualTag().getColor() + "§l" + profile.getActualTag().getPrefix()
                    + profile.getActualTag().getColor() + " " + player.getName();
        } else {
            prefix = prefix + "§7" + player.getName();
        }
        if (BukkitMain.getServerType() == ServerType.LOBBY || BukkitMain.getServerType() == ServerType.LOBBY_PVP || BukkitMain.getServerType() == ServerType.LOBBY_HARDCOREGAMES || BukkitMain.getServerType() == ServerType.LOBBY_DUELS) {
            prefix = prefix + "";

            String formattedMessage = prefix + "§7 » §f" + message;

            for (Player onlines : event.getRecipients()) {
                onlines.sendMessage(formattedMessage);
            }
        } else {
            prefix = "§7[" + league.getColor() + league.getSymbol() + "§7] " + prefix;

            String formattedMessage = prefix + "§7 » §f" + message;

            for (Player onlines : event.getRecipients()) {
                onlines.sendMessage(formattedMessage);
            }
        }
    }

    private boolean inChatCooldown(Player player) {
        boolean cooldown = false;

        if (player.hasMetadata(CHAT_COOLDOWN_TAG)) {
            if (player.getMetadata(CHAT_COOLDOWN_TAG).get(0).asLong() + CHAT_COOLDOWN_TIME > System
                    .currentTimeMillis()) {
                player.sendMessage(BukkitMessages.CHAT_COOLDOWN);
                cooldown = true;
            } else {
                player.setMetadata(CHAT_COOLDOWN_TAG,
                        new FixedMetadataValue(BukkitMain.getInstance(), System.currentTimeMillis()));
            }
        } else {
            player.setMetadata(CHAT_COOLDOWN_TAG,
                    new FixedMetadataValue(BukkitMain.getInstance(), System.currentTimeMillis()));
        }

        return cooldown;
    }

    private boolean inCommandDelay(Player player) {
        boolean cooldown = false;

        if (player.hasMetadata(DELAY_COMMAND_TAG)) {
            if (player.getMetadata(DELAY_COMMAND_TAG).get(0).asLong() + COMMAND_COOLDOWN_TIME > System
                    .currentTimeMillis()) {
                player.sendMessage(BukkitMessages.VOCE_ESTA_TENTANDO_EXECUTAR_COMANDOS_MUITO_RAPIDO);
                cooldown = true;
            } else {
                player.setMetadata(DELAY_COMMAND_TAG,
                        new FixedMetadataValue(BukkitMain.getInstance(), System.currentTimeMillis()));
            }
        } else {
            player.setMetadata(DELAY_COMMAND_TAG,
                    new FixedMetadataValue(BukkitMain.getInstance(), System.currentTimeMillis()));
        }

        return cooldown;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (event.getPlayer().hasMetadata(CHAT_COOLDOWN_TAG)) {
            event.getPlayer().removeMetadata(CHAT_COOLDOWN_TAG, BukkitMain.getInstance());
        }

        if (event.getPlayer().hasMetadata(DELAY_COMMAND_TAG)) {
            event.getPlayer().removeMetadata(DELAY_COMMAND_TAG, BukkitMain.getInstance());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!event.isCancelled()) {
            if (!event.getPlayer().isOp() && inCommandDelay(event.getPlayer())) {
                event.setCancelled(true);
                return;
            }

            String cmd = event.getMessage().split(" ")[0];

            HelpTopic topic = Bukkit.getServer().getHelpMap().getHelpTopic(cmd);

            if (topic == null) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(BukkitMessages.COMANDO_INEXISTENTE);
            }
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess2(PlayerCommandPreprocessEvent event) {
        final String message = event.getMessage().toLowerCase();

        if (message.equals("stop")) {
            event.setCancelled(true);
            event.getPlayer().chat("/parar");
            return;
        }

        if ((message.equals("/pl")) || (message.equals("/plugin")) || (message.startsWith("ver"))
                || (message.startsWith("icanhasbukkit")) || (message.equals("/plugins"))
                || (message.startsWith("/help")) || (message.startsWith("/bukkit:"))) {
            event.setCancelled(true);
           //event.getPlayer().chat("/criador");
            return;
        }

        boolean block = false;

        for (String cmds : cmdsBlockeds) {
            if (message.startsWith(cmds)) {
                if ((cmds.equalsIgnoreCase("/w")) || (message.equalsIgnoreCase("/killmobs"))
                        || (message.equalsIgnoreCase("/whitelist")) || (message.contains("medal"))
                        || (message.contains("memory")) || (message.equalsIgnoreCase("/wand")))
                    continue;
                block = true;
                break;
            }
        }

        if (block) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(BukkitMessages.COMANDO_BLOQUEADO);
        }
    }
}