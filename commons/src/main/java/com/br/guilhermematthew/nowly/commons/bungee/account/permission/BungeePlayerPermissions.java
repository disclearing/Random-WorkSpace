package com.br.guilhermematthew.nowly.commons.bungee.account.permission;

import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import lombok.val;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;

public class BungeePlayerPermissions {

    public static void addPermission(final ProxiedPlayer proxiedPlayer, final String permission) {
        proxiedPlayer.setPermission(permission, true);
    }

    public static void injectPermissions(final ProxiedPlayer proxiedPlayer, final String grupo) {
        List<String> permissions = Groups.getGroup(grupo).getPermissions();

        permissions.forEach(permission -> proxiedPlayer.setPermission(permission, true));
    }

    public static void clearPermissions(ProxiedPlayer proxiedPlayer) {
        val clone = new ArrayList<>(proxiedPlayer.getPermissions());

        clone.forEach(permission -> proxiedPlayer.setPermission(permission, false));
    }

}