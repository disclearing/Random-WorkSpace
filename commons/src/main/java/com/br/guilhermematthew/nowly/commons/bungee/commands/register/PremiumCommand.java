package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.bungee.manager.premium.PremiumMapManager;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.utility.mojang.UUIDFetcher.UUIDFetcherException;

public class PremiumCommand implements CommandClass {

    @Command(name = "premium", aliases = {"checkpremium", "pmap", "premiummap"}, groupsToUse = {Groups.ADMIN})
    public void premium(BungeeCommandSender commandSender, String label, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage("");
            commandSender.sendMessage("Total de PremiumMap: §a" + PremiumMapManager.getPremiumMaps());
            commandSender.sendMessage("Jogadores Piratas: §c" + PremiumMapManager.getCrackedAmount());
            commandSender.sendMessage("Jogadores Originais: §a" + PremiumMapManager.getPremiumAmount());
            commandSender.sendMessage("");
            return;
        }
        if (args.length == 1) {
            String nick = args[0];

            try {
                PremiumMapManager.load(nick);
            } catch (UUIDFetcherException ex) {
                commandSender.sendMessage("§cOcorreu um erro ao obter a UUID do jogador.");
                return;
            }

            if (!PremiumMapManager.containsMap(nick)) {
                commandSender.sendMessage("§cConta ainda não carregada...");
                return;
            }

            commandSender.sendMessage("Jogador: " +
                    (PremiumMapManager.getPremiumMap(nick).isPremium() ? "§aOriginal" : "§cPirata"));
        }
    }
}