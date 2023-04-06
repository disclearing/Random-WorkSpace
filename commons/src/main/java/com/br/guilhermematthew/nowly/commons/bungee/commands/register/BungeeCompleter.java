package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Completer;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BungeeCompleter implements CommandClass {

    @Completer(name = "ban", aliases = {"mute", "find", "kick", "premium", "setgroup", "group", "unmute", "report", "tempban", "go", "addcoins", "addxp", "addperm"})
    public List<String> multiCompleter(ProxiedPlayer p, String label, String[] args) {
        if(args.length < 1) return BungeeCord.getInstance().getPlayers().stream().map(CommandSender::getName).collect(Collectors.toList());
        if(args.length > 1) return Collections.emptyList();

        return BungeeCord.getInstance().getPlayers()
                .stream()
                .map(CommandSender::getName)
                .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
    }
}