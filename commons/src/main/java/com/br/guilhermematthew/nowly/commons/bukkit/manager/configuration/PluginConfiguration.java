package com.br.guilhermematthew.nowly.commons.bukkit.manager.configuration;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class PluginConfiguration {

    public static final HashMap<String, Location> CACHE_LOCATIONS = new HashMap<>();

    public static Location setLocation(Plugin instance, String nome, Player player) {
        instance.getConfig().set("locs." + nome + ".world", player.getLocation().getWorld().getName());
        instance.getConfig().set("locs." + nome + ".x", player.getLocation().getBlockX() + 0.5);
        instance.getConfig().set("locs." + nome + ".y", Double.valueOf(player.getLocation().getY()));
        instance.getConfig().set("locs." + nome + ".z", player.getLocation().getBlockZ() + 0.5);
        instance.getConfig().set("locs." + nome + ".yaw", Float.valueOf(player.getLocation().getYaw()));
        instance.getConfig().set("locs." + nome + ".pitch", Float.valueOf(player.getLocation().getPitch()));
        instance.saveConfig();

        CACHE_LOCATIONS.remove(nome);

        return getLocation(instance, nome);
    }

    public static Location getLocation(Plugin instance, String nome) {
        if (CACHE_LOCATIONS.containsKey(nome)) {
            return CACHE_LOCATIONS.get(nome);
        }

        if (!instance.getConfig().contains("locs." + nome + ".world")) {
            BukkitMain.console("Local inválido.");
            return null;
        }

        double x = instance.getConfig().getDouble("locs." + nome + ".x"),
                y = instance.getConfig().getDouble("locs." + nome + ".y"),
                z = instance.getConfig().getDouble("locs." + nome + ".z");

        Location loc = new Location(Bukkit.getWorld(instance.getConfig().getString("locs." + nome + ".world")), x, y, z,
                (float) instance.getConfig().getLong("locs." + nome + ".yaw"),
                (float) instance.getConfig().getLong("locs." + nome + ".pitch"));
        CACHE_LOCATIONS.put(nome, loc);

        return loc;
    }

    public static Location createLocation(Plugin instance, String nome) {
        return createLocation(instance, nome, "world");
    }

    public static void createLocations(Plugin instance, String... nomes) {
        for (String name : nomes) {
            createLocation(instance, name, "world");
        }
    }

    public static Location createLocation(Plugin instance, String nome, String world) {
        if (!instance.getConfig().contains("locs." + nome + ".world")) {
            instance.getConfig().set("locs." + nome + ".world", world);
            instance.getConfig().set("locs." + nome + ".x", 0.5);
            instance.getConfig().set("locs." + nome + ".y", 80.5);
            instance.getConfig().set("locs." + nome + ".z", 0.5);
            instance.getConfig().set("locs." + nome + ".yaw", 0);
            instance.getConfig().set("locs." + nome + ".pitch", 0);
            instance.saveConfig();
        }

        return getLocation(instance, nome);
    }
}