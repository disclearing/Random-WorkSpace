package com.br.guilhermematthew.nowly.commons;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.common.PluginInstance;
import com.br.guilhermematthew.nowly.commons.common.connections.mysql.MySQL;
import com.br.guilhermematthew.nowly.commons.common.profile.GamingProfile;
import com.br.guilhermematthew.nowly.commons.common.profile.ProfileManager;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServersManager;
import com.br.guilhermematthew.nowly.commons.common.utility.mojang.UUIDFetcher;

public class CommonsGeneral {

    private static final ProfileManager profileManager = new ProfileManager();
    private static final UUIDFetcher uuidFetcher = new UUIDFetcher();
    private static final ServersManager serversManager = new ServersManager();
    private static final MySQL mySQL = new MySQL();

    private static PluginInstance pluginInstance = PluginInstance.UNKNOWN;

    public static boolean correctlyStarted() {
        if (!getMySQL().isConnected()) {
            error("A conexao com o MySQL nao teve exito, fechando servidor.");
            return false;
        }
        return true;
    }

    public static ProfileManager getProfileManager() {
        return profileManager;
    }

    public static UUIDFetcher getUUIDFetcher() {
        return uuidFetcher;
    }

    public static PluginInstance getPluginInstance() {
        return pluginInstance;
    }

    public static void setPluginInstance(PluginInstance pluginInstance) {
        CommonsGeneral.pluginInstance = pluginInstance;
    }

    public static ServersManager getServersManager() {
        return serversManager;
    }

    public static void shutdown() {
        if (pluginInstance == PluginInstance.BUNGEECORD) {
            BungeeMain.shutdown();
        } else {
            BukkitMain.shutdown();
        }
    }

    public static void error(String message) {
        console("§c[ERRO] " + message);
    }

    public static void console(String message) {
        if (pluginInstance == PluginInstance.BUKKIT) {
            BukkitMain.console(message);
        } else {
            BungeeMain.console(message);
        }
    }

    public static void runAsync(Runnable runnable) {
        if (pluginInstance == PluginInstance.BUKKIT) {
            BukkitMain.runAsync(runnable);
        } else {
            BungeeMain.runAsync(runnable);
        }
    }

    public static MySQL getMySQL() {
        return mySQL;
    }
}