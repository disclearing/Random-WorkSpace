package com.br.guilhermematthew.nowly.commons.bungee.manager.config;

import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import lombok.Getter;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
public class BungeeConfiguration {

    private final String configName;
    private Configuration configuration;
    private File file;

    public BungeeConfiguration(final String configName) {
        this.configName = configName;
    }

    public void load() {
        this.file = new File(BungeeMain.getInstance().getDataFolder(), this.configName + ".yml");

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (Exception ex) {
                return;
            }
        }

        try {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        BungeeMain.getManager().getConfigManager().refreshConfig(configName);
    }

    public void save() {
        if (this.file == null) {
            return;
        }

        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void unload() {
        if (this.file != null) {
            this.file = null;
            this.configuration = null;
        }
    }

    public boolean isLoaded() {
        return this.file != null;
    }
}