package com.br.guilhermematthew.nowly.commons.bungee.commands;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.common.command.CommandSender;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.UUID;

@RequiredArgsConstructor
public class BungeeCommandSender implements CommandSender, net.md_5.bungee.api.CommandSender {

    @NonNull
    private net.md_5.bungee.api.CommandSender sender;

    @Override
    public void addGroups(String... arg0) {
        sender.addGroups(arg0);
    }

    @Override
    public Collection<String> getGroups() {
        return sender.getGroups();
    }

    @Override
    public String getName() {
        return sender.getName();
    }

    @Override
    public Collection<String> getPermissions() {
        return sender.getPermissions();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasPermission(String arg0) {
        if (sender.hasPermission(CommonsConst.PERMISSION_PREFIX + ".cmd.all")) {
            return true;
        }
        if (sender.hasPermission(CommonsConst.PERMISSION_PREFIX + ".cmd." + arg0)) {
            return true;
        }
        sender.sendMessage(BungeeMessages.VOCE_NAO_TEM_PERMISSãO_PARA_USAR_ESTE_COMANDO);
        return false;
    }

    @Override
    public void removeGroups(String... arg0) {
        sender.removeGroups(arg0);
    }

    @Override
    public void sendMessage(String arg0) {
        sender.sendMessage(TextComponent.fromLegacyText(arg0));
    }

    @Override
    public void sendMessage(BaseComponent... arg0) {
        sender.sendMessage(arg0);
    }

    @Override
    public void sendMessage(BaseComponent arg0) {
        sender.sendMessage(arg0);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void sendMessages(String... arg0) {
        sender.sendMessages(arg0);
    }

    @Override
    public void setPermission(String arg0, boolean arg1) {
        sender.setPermission(arg0, arg1);
    }

    @Override
    public UUID getUniqueId() {
        return sender instanceof ProxiedPlayer ? ((ProxiedPlayer) sender).getUniqueId() : UUID.randomUUID();
    }

    public String getNick() {
        if (sender instanceof ProxiedPlayer) {
            return sender.getName();
        }
        return "CONSOLE";
    }

    @SuppressWarnings("deprecation")
    public boolean isPlayer() {
        if (sender instanceof ProxiedPlayer) {
            return true;
        }
        sender.sendMessage("§cComando disponível apenas para Jogadores.");
        return false;
    }

    public ProxiedPlayer getPlayer() {
        return (ProxiedPlayer) sender;
    }
}