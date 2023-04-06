package com.br.guilhermematthew.nowly.commons.bukkit.account.permission;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class PlayerAttachment {

    private final PermissionAttachment attachment;

    public PlayerAttachment(Player player, Plugin plugin) {
        this.attachment = player.addAttachment(plugin);
    }

    public void addPermission(String permission) {
        this.attachment.setPermission(permission, true);
    }

    public void addPermissions(List<String> permissions) {
        if (permissions == null) {
            return;
        }

        for (String permission : permissions) {
            this.attachment.setPermission(permission, true);
        }
    }

    public void removePermissions(List<String> permissions) {
        if (permissions == null) return;

        for (String permission : permissions) this.attachment.setPermission(permission, false);
    }

    public void resetPermissions() {
        if (getPermissions().size() != 0) {
            for (String permissions : getPermissions()) {
                this.attachment.setPermission(permissions, false);
            }
        }
    }

    public void removePermission(String permission) {
        this.attachment.setPermission(permission, false);
    }

    public List<String> getPermissions() {
        return new ArrayList<>(attachment.getPermissions().keySet());
    }
}