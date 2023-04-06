package io.github.guilhermematthew.anticheat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AnticheatMain extends Constant {

    @Override
    public void enable() {

        Bukkit.getConsoleSender().sendMessage(Fields.ENABLED);

    }

    @Override
    public void disable() {

        Bukkit.getConsoleSender().sendMessage(Fields.DISABLED);

    }

    @Override
    public void load() {

    }
}
