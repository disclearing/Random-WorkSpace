package io.github.guilhermematthew.anticheat;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Constant extends JavaPlugin implements Listener
{

    //PLUGINS GETTERS
    private PluginManager pm = Bukkit.getPluginManager();

    //PLUGIN GETTING SYSTEM
    private AnticheatMain plugin;
    public AnticheatMain getPlugin() {
        return plugin;
    }

    //SYSTEM ABSTRACT LOG MAIN
    public abstract void enable();
    public abstract void disable();
    public abstract void load();
    @Override
    public void onEnable() {
        this.enable();
    }
    @Override
    public void onDisable() {
        this.disable();
    }
    @Override
    public void onLoad() {
        this.load();
    }
}
