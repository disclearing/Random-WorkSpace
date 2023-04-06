package com.br.guilhermematthew.nowly.commons.bungee;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.account.BungeePlayer;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandFramework;
import com.br.guilhermematthew.nowly.commons.bungee.manager.BungeeManager;
import com.br.guilhermematthew.nowly.commons.bungee.networking.BungeePacketsHandler;
import com.br.guilhermematthew.nowly.commons.bungee.scheduler.BungeeUpdateScheduler;
import com.br.guilhermematthew.nowly.commons.bungee.utility.BungeeListeners;
import com.br.guilhermematthew.nowly.commons.bungee.utility.logfilter.LogFilterBungee;
import com.br.guilhermematthew.nowly.commons.common.PluginInstance;
import com.br.guilhermematthew.servercommunication.ServerCommunication;
import com.br.guilhermematthew.servercommunication.server.Server;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BungeeMain extends Plugin {

    private List<String> messagesBroadcast = Arrays.asList(
            "§eParticipe da nossa §6comunidade §eem §bdiscord.gg/leaguemc_",
            "§eAcompanhe suas §6estatísticas §eusando o §b/stats",
            "§eCansado de usar sempre a mesma §6skin§e? Confira nosso sistema §b/skin",
            "§eCompre §6ranks e mais§e em nossa loja §bleaguemc.com.br/loja",
            "§eEncontrou um jogador usando §6trapaças§e? Utilize §b/report <jogador> <motivo>"
    );

    @Getter
    @Setter
    private static BungeeMain instance;

    @Getter
    @Setter
    private static BungeeMain api;

    public static BungeeMain getApi() {
        return api;
    }

    @Getter
    @Setter
    private static boolean loaded = false;

    @Getter
    @Setter
    private static BungeeManager manager;

    @Getter
    @Setter
    private static String socketServerHost;

    private int messagesIndex = 0;

    public static void shutdown() {
        ProxyServer.getInstance().stop();
    }

    @SuppressWarnings("deprecation")
    public static void console(String msg) {
        ProxyServer.getInstance().getConsole().sendMessage("[Commons] " + msg);
    }

    public static void runAsync(Runnable runnable) {
        ProxyServer.getInstance().getScheduler().runAsync(getInstance(), runnable);
    }

    public static void runLater(Runnable runnable) {
        ProxyServer.getInstance().getScheduler().schedule(getInstance(), runnable, 500, TimeUnit.MILLISECONDS);
    }

    public static void runLater(Runnable runnable, long ms) {
        ProxyServer.getInstance().getScheduler().schedule(getInstance(), runnable, ms, TimeUnit.MILLISECONDS);
    }

    public static void runLater(Runnable runnable, int tempo, TimeUnit timeUnit) {
        ProxyServer.getInstance().getScheduler().schedule(getInstance(), runnable, tempo, timeUnit);
    }

    @SuppressWarnings("deprecation")
    public static boolean registerServer(String serverName, String address, int port) {
        if (ProxyServer.getInstance().getServers().containsKey(serverName)) {
            console("[DynamicServers] " + serverName + " error on register this server! (" + address + ":" + port + ")");
            return false;
        }

        ProxyServer.getInstance().getServers().put(serverName, ProxyServer.getInstance().constructServerInfo(serverName,
                new InetSocketAddress(address, port), "Unknown MOTD", false));

        console("[DynamicServers] " + serverName + " registred! (" + address + ":" + port + ")");
        return true;
    }

    @SuppressWarnings("deprecation")
    public static void removeServer(String serverName) {
        if (!ProxyServer.getInstance().getServers().containsKey(serverName)) {
            return;
        }

        TextComponent reason = new TextComponent("You were kicked because " + "the server was removed.");

        ServerInfo info = ProxyServer.getInstance().getServerInfo(serverName);

        if (info == null) {
            return;
        }

        for (ProxiedPlayer player : info.getPlayers()) {
            player.disconnect(reason);
        }

        console("[DynamicServers] " + serverName + " removed! (" + info.getAddress().getAddress().getHostAddress() + ":"
                + info.getAddress().getPort() + ")");

        ProxyServer.getInstance().getServers().remove(info.getName());
    }

    public static boolean isValid(ProxiedPlayer proxiedPlayer) {
        if (proxiedPlayer == null)
            return false;
        if (proxiedPlayer.getServer() == null)
            return false;
        if (proxiedPlayer.getServer().getInfo().getName().equalsIgnoreCase("Login"))
            return false;

        return CommonsGeneral.getProfileManager().containsProfile(proxiedPlayer.getName());
    }

    public static BungeePlayer getBungeePlayer(final UUID uniqueId) {
        return (BungeePlayer) CommonsGeneral.getProfileManager().getGamingProfile(uniqueId);
    }

    public static BungeePlayer getBungeePlayer(final String name) {
        return (BungeePlayer) CommonsGeneral.getProfileManager().getGamingProfile(name);
    }

    public void onLoad() {
        setInstance(this);

        CommonsGeneral.setPluginInstance(PluginInstance.BUNGEECORD);

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        setManager(new BungeeManager());
        getManager().getConfigManager().getBungeeConfig().load();
        getManager().getConfigManager().getPermissionsConfig().load();

        CommonsGeneral.getMySQL().openConnection();
    }

    public void onEnable() {
        if (CommonsGeneral.correctlyStarted()) {
            LogFilterBungee.load(getInstance());

            CommonsGeneral.getMySQL().createTables();

            ServerCommunication.setPacketHandler(new BungeePacketsHandler());

            ServerCommunication.startServer(socketServerHost);

            CommonsGeneral.getServersManager().init();

            new BungeeCommandFramework(this)
                    .loadCommands(this, "com.br.guilhermematthew.nowly.commons.bungee.commands.register");

            BungeeListeners.loadListeners(getInstance(), "com.br.guilhermematthew.nowly.commons.bungee.listeners");

            getProxy().getScheduler().schedule(getInstance(), new BungeeUpdateScheduler(), 1, 1, TimeUnit.SECONDS);

            getProxy().getScheduler().schedule(this, () -> {
                if (messagesIndex >= messagesBroadcast.size())
                    messagesIndex = 0;
                getProxy().broadcast(TextComponent
                        .fromLegacyText("§2§lLEAGUE §7» " + messagesBroadcast.get(messagesIndex)));
                ++messagesIndex;
            }, 2, 2, TimeUnit.MINUTES);
            getManager().init();
        } else {
            shutdown();
        }
    }

    public void onDisable() {
        CommonsGeneral.getMySQL().closeConnection();

        if (Server.getInstance() != null) {
            try {
                Server.getInstance().getServerGeneral().disconnect();
            } catch (IOException e) {
            }
        }
    }
}