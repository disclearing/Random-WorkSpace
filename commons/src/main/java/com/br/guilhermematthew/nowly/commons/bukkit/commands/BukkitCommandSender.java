package com.br.guilhermematthew.nowly.commons.bukkit.commands;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.common.command.CommandSender;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class BukkitCommandSender implements CommandSender, org.bukkit.command.CommandSender {

    @NonNull
    private org.bukkit.command.CommandSender commandSender;

    @Override
    public UUID getUniqueId() {
        if (commandSender instanceof Player)
            return ((Player) commandSender).getUniqueId();
        return UUID.randomUUID();
    }

    public Player getPlayer() {
        return (Player) commandSender;
    }

    public String getNick() {
        if (commandSender instanceof Player) {
            return commandSender.getName();
        }
        return "CONSOLE";
    }

    @Override
    public boolean isPlayer() {
        if (commandSender instanceof Player) {
            return true;
        }
        commandSender.sendMessage("§cComando disponível apenas para Jogadores.");
        return false;
    }

    public String getRealNick() {
        if (commandSender instanceof Player) {
            return CommonsGeneral.getProfileManager().getGamingProfile(((Player) commandSender).getUniqueId()).getNick();
        }
        return "CONSOLE";
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0) {
        return commandSender.addAttachment(arg0);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
        return commandSender.addAttachment(arg0);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2) {
        return commandSender.addAttachment(arg0, arg1, arg2);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2, int arg3) {
        return commandSender.addAttachment(arg0, arg1, arg2, arg3);
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return commandSender.getEffectivePermissions();
    }

    @Override
    public boolean hasPermission(String arg0) {
        if (commandSender.hasPermission(CommonsConst.PERMISSION_PREFIX + ".cmd.all")) {
            return true;
        }
        if (commandSender.hasPermission(CommonsConst.PERMISSION_PREFIX + ".cmd." + arg0)) {
            return true;
        }
        commandSender.sendMessage(BukkitMessages.VOCE_NAO_TEM_PERMISSãO_PARA_USAR_ESTE_COMANDO);
        return false;
    }

    @Override
    public boolean hasPermission(Permission arg0) {
        return commandSender.hasPermission(arg0);
    }

    @Override
    public boolean isPermissionSet(String arg0) {
        return commandSender.isPermissionSet(arg0);
    }

    @Override
    public boolean isPermissionSet(Permission arg0) {
        return commandSender.isPermissionSet(arg0);
    }

    @Override
    public void recalculatePermissions() {
        commandSender.recalculatePermissions();
    }

    @Override
    public void removeAttachment(PermissionAttachment arg0) {
        commandSender.removeAttachment(arg0);
    }

    @Override
    public boolean isOp() {
        return commandSender.isOp();
    }

    @Override
    public void setOp(boolean arg0) {
        commandSender.setOp(arg0);
    }

    @Override
    public String getName() {
        return commandSender.getName();
    }

    @Override
    public Server getServer() {
        return commandSender.getServer();
    }

    @Override
    public void sendMessage(String arg0) {
        commandSender.sendMessage(arg0);
    }

    @Override
    public void sendMessage(String[] arg0) {
        commandSender.sendMessage(arg0);
    }

    public String getArgs(String[] args, int começo) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = começo; i < args.length; i++) {
            stringBuilder.append(args[i]).append(" ");
        }

        return stringBuilder.toString();
    }
}