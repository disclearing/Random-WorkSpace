package com.br.guilhermematthew.nowly.commons.bukkit.utility.loader;

import com.br.guilhermematthew.nowly.commons.common.utility.ClassGetter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class BukkitListeners {

    public static void loadListeners(Object instance, String packageName) {
        for (Class<?> classes : ClassGetter.getClassesForPackage(instance, packageName)) {
            try {
                if (Listener.class.isAssignableFrom(classes)) {
                    Bukkit.getPluginManager().registerEvents((Listener) classes.newInstance(), (Plugin) instance);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}

