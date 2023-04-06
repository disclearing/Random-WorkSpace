package com.br.guilhermematthew.nowly.commons.bukkit.commands.register;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Completer;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class SpyCommand implements CommandClass {

    public static final HashMap<UUID, List<String>> spying = new HashMap<>();
    private final String COMMAND_USAGE = "§a§lSPY §fUtilize: /spy <Nick/All>",
            SPYING_ALL = "§a§lSPY §fVocê está espiando todos os §a§lTELLS!",
            NOT_SPYING_ALL = "§a§lSPY §fVocê não está espionando mais!",
            NOT_SPYING_PLAYER = "§a§lSPY §fVocê não está espiando o jogador §a%nick%",
            SPYING_PLAYER = "§a§lSPY §fVocê está espiando o jogador §a%nick%";

    public static List<Player> getSpys() {
        List<Player> list = new ArrayList<>();

        for (UUID uuids : spying.keySet()) {
            Player target = Bukkit.getPlayer(uuids);
            if (target != null && target.isOnline()) {
                list.add(target);
            }
        }

        return list;
    }

    public static boolean isSpyingPlayer(Player player, String nick) {
        if (!spying.containsKey(player.getUniqueId())) {
            return false;
        }

        return spying.get(player.getUniqueId()).contains(nick);
    }

    public static boolean isSpyingAll(Player player) {
        if (!spying.containsKey(player.getUniqueId())) {
            return false;
        }

        return spying.get(player.getUniqueId()).contains("all");
    }

    @Command(name = "spy", aliases = {"espiar"}, groupsToUse = {Groups.MOD})
    public void spy(BukkitCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        Player player = commandSender.getPlayer();

        if (args.length != 1) {
            player.sendMessage(COMMAND_USAGE);
            return;
        }

        String nick = args[0];

        if (isAll(nick)) {
            if (!spying.containsKey(player.getUniqueId())) {
                spying.put(player.getUniqueId(), Collections.singletonList("all"));
                player.sendMessage(SPYING_ALL);
            } else {
                if (!spying.get(player.getUniqueId()).contains("all")) {
                    spying.put(player.getUniqueId(), Collections.singletonList("all"));
                    player.sendMessage(SPYING_ALL);
                } else {
                    spying.remove(player.getUniqueId());
                    player.sendMessage(NOT_SPYING_ALL);
                }
            }

        } else {
            Player target = Bukkit.getPlayer(nick);

            if (target != null) {

                final String realNick = BukkitMain.getBukkitPlayer(target.getUniqueId()).getNick();

                if (!spying.containsKey(player.getUniqueId())) {
                    spying.put(player.getUniqueId(), Collections.singletonList(realNick));
                    player.sendMessage(SPYING_PLAYER.replace("%nick%", realNick));
                } else {
                    if (spying.get(player.getUniqueId()).contains(realNick)) {
                        List<String> list = spying.get(player.getUniqueId());
                        list.remove(realNick);

                        if (list.size() == 0) {
                            spying.remove(player.getUniqueId());
                        } else {
                            spying.put(player.getUniqueId(), list);
                        }
                        player.sendMessage(NOT_SPYING_PLAYER.replace("%nick%", realNick));

                        list.clear();
                        list = null;

                    } else {
                        List<String> list = spying.get(player.getUniqueId());
                        list.add(realNick);

                        spying.put(player.getUniqueId(), list);

                        player.sendMessage(SPYING_PLAYER.replace("%nick%", realNick));

                        list.clear();
                        list = null;
                    }
                }

                target = null;
            } else {
                player.sendMessage(BukkitMessages.JOGADOR_OFFLINE);
            }
        }
    }

    private boolean isAll(String nick) {
        if (nick.equalsIgnoreCase("*")) return true;
        if (nick.equalsIgnoreCase("all")) return true;
        return nick.equalsIgnoreCase("todos");
    }

    @Completer(name = "spy", aliases = {"espiar"})
    public List<String> tagcompleter(BukkitCommandSender sender, String label, String[] args) {
        if (sender.isPlayer()) {

            if (args.length == 1) {
                List<String> list = new ArrayList<>();
                list.add("all");

                for (Player ons : Bukkit.getOnlinePlayers()) {
                    if (ons.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                        list.add(ons.getName());
                    }
                }

                return list;
            }
        }

        return new ArrayList<>();
    }
}