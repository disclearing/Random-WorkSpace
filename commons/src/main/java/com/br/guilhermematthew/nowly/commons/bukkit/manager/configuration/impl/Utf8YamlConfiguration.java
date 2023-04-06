package com.br.guilhermematthew.nowly.commons.bukkit.manager.configuration.impl;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public class Utf8YamlConfiguration extends YamlConfiguration {

    @Override @SuppressWarnings("UnstableApiUsage")
    public void save(File file) throws IOException {
        Validate.notNull(file, "File cannot be null");
        Files.createParentDirs(file);
        String data = this.saveToString();
        Writer writer = new OutputStreamWriter(java.nio.file.Files.newOutputStream(file.toPath()), Charsets.UTF_8);
        try {
            writer.write(data);
        } finally {
            writer.close();
        }
    }

    @Override
    public void load(File file) throws IOException, InvalidConfigurationException {
        Validate.notNull(file, "File cannot be null");
        this.load(new InputStreamReader(java.nio.file.Files.newInputStream(file.toPath()), Charsets.UTF_8));
    }

    @Override
    @Deprecated
    public void load(InputStream stream) throws IOException, InvalidConfigurationException {
        Validate.notNull(stream, "Stream cannot be null");
        this.load(new InputStreamReader(stream, Charsets.UTF_8));
    }
}