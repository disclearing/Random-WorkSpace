package com.br.guilhermematthew.nowly.commons.bukkit.worldedit.schematic.object;

import com.br.guilhermematthew.nowly.commons.bukkit.worldedit.schematic.utils.Vector;

public class SchematicLocation {

    private final String key;

    private final Vector location;

    public SchematicLocation(String key, Vector location) {
        this.key = key;
        this.location = location;
    }

    public SchematicLocation(String string) {
        String[] data = string.split(";");
        this.key = data[0];
        this.location = new Vector(data[1]);
    }

    public String getKey() {
        return key;
    }

    public Vector getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return key + ";" + location.toString();
    }
}