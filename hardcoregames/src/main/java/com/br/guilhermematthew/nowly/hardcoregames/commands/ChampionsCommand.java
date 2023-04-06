package com.br.guilhermematthew.nowly.hardcoregames.commands;

import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.hardcoregames.listeners.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ChampionsCommand implements CommandClass {

    private final List<String> types = Arrays.asList(
            "lava", "minicopa", "ultrafast", "gladiator",
            "arenafeast", "arenadamage", "arenapvp", "arenaold", "champions", "minichampions", "championstraining");
    private final List<String> modes = Arrays.asList("solo", "dupla");

    @CommandFramework.Command(name = "explainevent", aliases = {"explicarevento", "eventoexplicar"}, groupsToUse= {Groups.MOD})
    public void explainevent(BukkitCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        if (args.length == 0) {
            sendHelp(commandSender);
            return;
        }

        Player player = commandSender.getPlayer();

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("champions")) {

                for (Player allUsers : Bukkit.getOnlinePlayers()) {
                    allUsers.sendMessage("§6§lCHAMPIONS §7» §fSejam bem-vindos(as) a §eChampions§f!");
                    allUsers.sendMessage("§f");
                    allUsers.sendMessage("§6§lCHAMPIONS §7» §fTodos irão iniciar o jogo com um kit de itens.");
                    allUsers.sendMessage("§f");
                    allUsers.sendMessage("§6§lCHAMPIONS §7» §fVocês terão §e1 minuto §fde invencibilidade para se\n§forganizarem.");
                    allUsers.sendMessage("§f");
                    allUsers.sendMessage("§6§lCHAMPIONS §7» §fÉ §cproibido §finterferir na luta de jogadores que não fazem parte de seu time.");
                    allUsers.sendMessage("§6§lCHAMPIONS §7» §fÉ §cproibido §faguardar que uma luta seja finalizada com intuito de atacar os vencedores.");
                    allUsers.sendMessage("§6§lCHAMPIONS §7» §fNão é permitido fazer spawn traps.");
                    allUsers.sendMessage("§f");
                    allUsers.sendMessage("§6§lCHAMPIONS §7» §fAos §e30 minutos §fde partida, vocês serão teleportados para uma Arena Final.");
                    allUsers.sendMessage("§6§lCHAMPIONS §7» §fAlguns kits estarão desativados durante todo o evento.");
                    allUsers.sendMessage("§6§lCHAMPIONS §7» §fDúvidas, reclamações e revisões de kick devem ser solicitados em nosso §9Discord§f!");
                    allUsers.sendMessage("§f");
                    allUsers.sendMessage("§6§lCHAMPIONS §7» §fIniciaremos em alguns instantes! Bom jogo e boa sorte.");
                }
            } else {
                sendHelp(commandSender);
            }
             if (args.length == 1) {
                if (args[0].equalsIgnoreCase("arenapvp")) {

                    for (Player allUsers : Bukkit.getOnlinePlayers()) {
                        allUsers.sendMessage("§6§lCHAMPIONS §7» §fSejam bem-vindos(as) a §eChampions§f!");
                        allUsers.sendMessage("§f");
                        allUsers.sendMessage("§6§lCHAMPIONS §7» §fTodos irão iniciar o jogo com um kit de itens.");
                        allUsers.sendMessage("§f");
                        allUsers.sendMessage("§6§lCHAMPIONS §7» §fVocês terão §e1 minuto §fde invencibilidade para se\n§forganizarem.");
                        allUsers.sendMessage("§f");
                        allUsers.sendMessage("§6§lCHAMPIONS §7» §fÉ §cproibido §finterferir na luta de jogadores que não fazem parte de seu time.");
                        allUsers.sendMessage("§6§lCHAMPIONS §7» §fÉ §cproibido §faguardar que uma luta seja finalizada com intuito de atacar os vencedores.");
                        allUsers.sendMessage("§6§lCHAMPIONS §7» §fNão é permitido fazer spawn traps.");
                        allUsers.sendMessage("§f");
                        allUsers.sendMessage("§6§lCHAMPIONS §7» §fAos §e30 minutos §fde partida, vocês serão teleportados para uma Arena Final.");
                        allUsers.sendMessage("§6§lCHAMPIONS §7» §fAlguns kits estarão desativados durante todo o evento.");
                        allUsers.sendMessage("§6§lCHAMPIONS §7» §fDúvidas, reclamações e revisões de kick devem ser solicitados em nosso §9Discord§f!");
                        allUsers.sendMessage("§f");
                        allUsers.sendMessage("§6§lCHAMPIONS §7» §fIniciaremos em alguns instantes! Bom jogo e boa sorte.");
                    }
                } else {
                    sendHelp(commandSender);
                }
            }
        }

        player = null;
    }

    private void sendHelp(BukkitCommandSender commandSender) {
        commandSender.sendMessage("");
        commandSender.sendMessage("§cUse: /explicarevento <" + StringUtils.join(types, "§c, ") + ">");
        commandSender.sendMessage(" §cExplicação do comando:");
        commandSender.sendMessage(" §cEle anunciará todas as informações e instruções do evento");
        commandSender.sendMessage(" §cpara os jogadores presentes na sala com clareza e bem");
        commandSender.sendMessage(" §ctabelado para intuição.");
        commandSender.sendMessage("");
    }

}
