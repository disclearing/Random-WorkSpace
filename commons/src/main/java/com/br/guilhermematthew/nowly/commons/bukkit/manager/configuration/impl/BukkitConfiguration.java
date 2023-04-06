package com.br.guilhermematthew.nowly.commons.bukkit.manager.configuration.impl;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.common.utility.system.Machine;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
@Setter
public class BukkitConfiguration {

    private String configName;
    private File file, dir;
    private FileConfiguration configuration;

    public BukkitConfiguration(final String configName, File dir, final boolean createDefaultDir) {
        this.configName = configName;

        if (createDefaultDir) {
            createDir();
        }

        if (dir != null) {
            this.dir = dir;
        }
    }

    public BukkitConfiguration(final String configName, final boolean createDefaultDir) {
        this(configName, null, createDefaultDir);
    }

    public void load() {
        if (file != null) {
            console("§c" + this.configName + " has already loaded!");
            return;
        }

        this.file = dir == null ? new File(Machine.getDiretorio(), configName + ".yml") :
                new File(dir, configName + ".yml");

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                console("§cAn error ocurred on create Configuration ''" + this.configName + "' > " + ex.getLocalizedMessage());
            }
        }

        this.configuration = Utf8YamlConfiguration.loadConfiguration(file);

        BukkitMain.getManager().getConfigurationManager().refreshConfig(configName);
    }

    public void save() {
        if (file == null) {
            console("§c" + this.configName + " has not loaded!");
            return;
        }

        try {
            configuration.save(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void createDir() {
        File dir = new File(Machine.getDiretorio());

        if (!dir.exists()) {
            dir.mkdir();
        }

        dir = null;
    }

    public void unload() {
        if (this.file != null) {

            this.file = null;
            this.configuration = null;
        }
    }

    private void console(final String message) {
        BukkitMain.console("[BukkitConfiguration] " + message);
    }
}