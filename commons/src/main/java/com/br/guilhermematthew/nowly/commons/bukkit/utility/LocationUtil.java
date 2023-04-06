package com.br.guilhermematthew.nowly.commons.bukkit.utility;

import org.bukkit.Location;

public class LocationUtil {

    public static boolean isRealMovement(Location from, Location to) {
        return !(from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ());
    }

}
