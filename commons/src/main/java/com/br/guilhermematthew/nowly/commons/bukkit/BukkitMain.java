package com.br.guilhermematthew.nowly.commons.bukkit;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ActionItemListener;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.MenuListener;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandFramework;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.server.ServerStatusUpdateEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.scheduler.BukkitUpdateScheduler;
import com.br.guilhermematthew.nowly.commons.bukkit.listeners.CoreListener;
import com.br.guilhermematthew.nowly.commons.bukkit.listeners.WorldDListener;
import com.br.guilhermematthew.nowly.commons.bukkit.manager.BukkitManager;
import com.br.guilhermematthew.nowly.commons.bukkit.networking.BukkitPacketsHandler;
import com.br.guilhermematthew.nowly.commons.bukkit.utility.loader.BukkitListeners;
import com.br.guilhermematthew.nowly.commons.common.PluginInstance;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import com.br.guilhermematthew.nowly.commons.custompackets.PacketType;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketCustomAction;
import com.br.guilhermematthew.servercommunication.ServerCommunication;
import com.br.guilhermematthew.servercommunication.client.Client;
import com.br.guilhermematthew.servercommunication.server.Server;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.UUID;

public class BukkitMain extends JavaPlugin {

    @Getter
    @Setter
    private static BukkitMain instance;

    @Getter
    @Setter
    private static int serverID;

    @Getter
    @Setter
    private static ServerType serverType;

    @Getter
    @Setter
    private static boolean loaded;

    @Getter
    @Setter
    private static BukkitManager manager;

    public static void console(String msg) {
        Bukkit.getConsoleSender().sendMessage("[Commons] " + msg);
    }

    public static void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(getInstance(), runnable);
    }

    public static void runLater(Runnable runnable) {
        runLater(runnable, 5);
    }

    public static void runLater(Runnable runnable, long ticks) {
        Bukkit.getScheduler().runTaskLater(getInstance(), runnable, ticks);
    }

    public static void runSync(Runnable runnable) {
        if (getInstance().isEnabled() && !getInstance().getServer().isPrimaryThread()) {
            getInstance().getServer().getScheduler().runTask(getInstance(), runnable);
        } else {
            runnable.run();
        }
    }

    public static BukkitPlayer getBukkitPlayer(final UUID uniqueId) {
        return (BukkitPlayer) CommonsGeneral.getProfileManager().getGamingProfile(uniqueId);
    }

    public static void shutdown() {
        Bukkit.shutdown();
    }

    public void onLoad() {
        saveDefaultConfig();

        setLoaded(false);

        setInstance(this);
        setManager(new BukkitManager());

        // new BukkitLogFilter().registerFilter();

        CommonsGeneral.setPluginInstance(PluginInstance.BUKKIT);

        getManager().getConfigurationManager().init();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "WDL|CONTROL");
        getServer().getMessenger().registerIncomingPluginChannel(this, "WDL|INIT", new WorldDListener());
        setServerType(ServerType.getServer(getConfig().getString("Servidor")));
        setServerID(getConfig().getInt("ServidorID"));

        CommonsGeneral.getMySQL().openConnection();
    }

    public void onEnable() {
        if (getServerType() != ServerType.UNKNOWN) {
            if (CommonsGeneral.correctlyStarted()) {
                CommonsGeneral.getMySQL().createTables();

                ServerCommunication.startClient(getServerType().getName(), getServerID(), getConfig().getString("Socket.Host"));
                ServerCommunication.setPacketHandler(new BukkitPacketsHandler());

                BukkitServerAPI.unregisterCommands("op", "deop", "kill", "about", "ver", "?", "scoreboard", "me", "say", "pl",
                        "plugins", "reload", "rl", "stop", "ban", "ban-ip", "msg", "ban-list", "help", "pardon",
                        "pardon-ip", "tp", "xp", "gamemode", "minecraft");

                BukkitCommandFramework.INSTANCE.loadCommands(this, "com.br.guilhermematthew.nowly.commons.bukkit.commands.register");
                BukkitListeners.loadListeners(getInstance(), "com.br.guilhermematthew.nowly.commons.bukkit.listeners");

                getServer().getMessenger().registerOutgoingPluginChannel(getInstance(), "BungeeCord");

                if (getServerType().useMenuListener()) {
                    MenuListener.registerListeners();
                }

                if (getServerType().useActionItem()) {
                    Bukkit.getServer().getPluginManager().registerEvents(new ActionItemListener(), getInstance());
                }

                CoreListener.init();
                Bukkit.getServer().getScheduler().runTaskTimer(getInstance(), new BukkitUpdateScheduler(), 1, 1);

                initialize();
            } else {
                Bukkit.shutdown();
            }
        } else {
            console("§cTipo de servidor nao encontrado, mude na configuracao!");
            Bukkit.shutdown();
        }
    }

    public void onDisable() {
        Client.getInstance().getClientConnection().sendPacket(new CPacketCustomAction(BukkitMain.getServerType(), BukkitMain.getServerID())
                .type(PacketType.BUKKIT_SEND_INFO).field("bukkit-server-turn-off"));

        CommonsGeneral.getMySQL().closeConnection();

        if (Server.getInstance() != null) {
            try {
                Server.getInstance().getServerGeneral().disconnect();
            } catch (IOException e) {
            }
        }
    }

    public void initialize() {
        getServer().getScheduler().runTaskTimer(getInstance(), () -> {
            ServerStatusUpdateEvent event = new ServerStatusUpdateEvent(getServer().getOnlinePlayers().size(), true);

            getServer().getPluginManager().callEvent(event);
        }, 0, 20L * getServerType().getSecondsUpdateStatus());
    }
}