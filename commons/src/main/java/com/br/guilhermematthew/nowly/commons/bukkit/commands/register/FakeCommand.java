package com.br.guilhermematthew.nowly.commons.bukkit.commands.register;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.api.fake.FakeAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerRequestEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.tag.TagManager;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.profile.GamingProfile;
import com.br.guilhermematthew.nowly.commons.common.utility.mojang.UUIDFetcher.UUIDFetcherException;
import com.br.guilhermematthew.nowly.commons.custompackets.PacketType;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketCustomAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Random;

public class FakeCommand implements CommandClass {

    private final String[] names = {"Mine", "Craft", "Hyper", "Lord", "Zyper", "Beach", "Actor", "Games", "Nitro",
            "Man", "Plays", "Crazy", "Mega", "Mineman", "G0D", "Killer", "Noob", "Gamer", "Blessed", "Scroll", "Money",
            "Fish", "Ferrari", "Player", "Super", "Hype", "Net", "Flix", "Flex", "Corsa", "Prata", "Verde", "Rap",
            "Astra", "Onix"};
    private final Random random = new Random();

    @Command(name = "fake", groupsToUse = {Groups.PRIME, Groups.YOUTUBER}, aliases = {"nick"})
    public void fake(BukkitCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        Player p = commandSender.getPlayer();

        if (args.length == 0) {
            p.sendMessage(BukkitMessages.FAKE_HELP);
            return;
        }

        if (args[0].equalsIgnoreCase("#")) {
            BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(p.getUniqueId());
            if (!bukkitPlayer.containsFake()) {
                p.sendMessage(BukkitMessages.NAO_ESTA_USANDO_FAKE);
                return;
            }

            if (requestChangeNick(p, false)) {
                String realNick = bukkitPlayer.getNick();
                bukkitPlayer.getDataHandler().getData(DataType.FAKE).setValue("");

                BukkitMain.runAsync(() -> bukkitPlayer.getDataHandler().saveCategory(DataCategory.ACCOUNT));

                FakeAPI.changePlayerName(p, realNick, false);
                p.sendMessage(BukkitMessages.TIROU_FAKE);

                TagManager.setTag(p, bukkitPlayer.getGroup().getTag(), bukkitPlayer);

                bukkitPlayer.sendPacket(new CPacketCustomAction(bukkitPlayer.getNick())
                        .type(PacketType.BUNGEE_SET_FAST_SKIN).field(realNick));
            }
        } else if (args[0].equalsIgnoreCase("random")) {
            boolean finded = false;

            String nick = getRandomFake();
            if (Bukkit.getPlayer(nick) == null) {
                finded = true;
            } else {
                nick = getRandomFake();
                if (Bukkit.getPlayer(nick) == null) {
                    finded = true;
                } else {
                    nick = getRandomFake();
                    if (Bukkit.getPlayer(nick) == null) {
                        finded = true;
                    } else {
                        nick = getRandomFake();
                        if (Bukkit.getPlayer(nick) == null) {
                            finded = true;
                        }
                    }
                }
            }

            if (!finded) {
                p.sendMessage(BukkitMessages.NENHUM_FAKE_RANDOM);
                return;
            }

            if (isOriginal(nick)) {
                p.sendMessage(BukkitMessages.FAKE_INDISPONIVEL);
                return;
            }

            if (requestChangeNick(p, true)) {
                BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(p.getUniqueId());
                bukkitPlayer.getDataHandler().getData(DataType.FAKE).setValue(nick);

                BukkitMain.runAsync(() -> bukkitPlayer.getDataHandler().saveCategory(DataCategory.ACCOUNT));

                FakeAPI.changePlayerName(p, nick, false);
                p.sendMessage(BukkitMessages.FAKE_SUCESSO.replace("%nick%", nick));

                TagManager.setTag(p, Groups.MEMBRO.getTag(), bukkitPlayer);

                bukkitPlayer.sendPacket(new CPacketCustomAction(bukkitPlayer.getNick())
                        .type(PacketType.BUNGEE_SET_RANDOM_SKIN));
            }
        } else if (args[0].equalsIgnoreCase("list")) {
            if (!commandSender.hasPermission("fakelist")) {
                return;
            }

            int fakes = 0;

            for (GamingProfile profiles : CommonsGeneral.getProfileManager().getGamingProfiles()) {
                BukkitPlayer bukkitPlayer = (BukkitPlayer) profiles;
                if (bukkitPlayer.containsFake()) {
                    if (fakes == 0) {
                        p.sendMessage("");
                        p.sendMessage(BukkitMessages.FAKE_LIST_PREFIX);
                        p.sendMessage("");
                    }
                    p.sendMessage(
                            BukkitMessages.FAKE_LIST_LINHA.replace("%nickFake%", bukkitPlayer.getPlayer().getName())
                                    .replace("%nickReal%", bukkitPlayer.getNick()));
                    fakes++;
                }
            }

            if (fakes == 0) {
                p.sendMessage(BukkitMessages.NENHUM_JOGADOR_COM_FAKE);
            } else {
                p.sendMessage("");
            }
        } else {
            String nick = args[0];

            if (nick.length() < 5) {
                p.sendMessage("§cEste nick é muito pequeno!");
                return;
            }
            if (nick.length() > 16) {
                p.sendMessage("§cEste nick é muito grande!");
                return;
            }
            if (!validString(nick)) {
                p.sendMessage("§cEste nick contém caractéres não permitidos.");
                return;
            }

            Player t = Bukkit.getPlayer(nick);
            if (t != null && t.isOnline()) {
                p.sendMessage(BukkitMessages.FAKE_INDISPONIVEL);
                return;
            }

            if (isOriginal(nick)) {
                p.sendMessage(BukkitMessages.FAKE_INDISPONIVEL);
                return;
            }

            if (requestChangeNick(p, true)) {
                BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(p.getUniqueId());
                bukkitPlayer.getDataHandler().getData(DataType.FAKE).setValue(nick);
                bukkitPlayer.getDataHandler().saveCategory(DataCategory.ACCOUNT);

                FakeAPI.changePlayerName(p, nick, false);
                p.sendMessage(BukkitMessages.FAKE_SUCESSO.replace("%nick%", nick));

                TagManager.setTag(p, Groups.MEMBRO.getTag(), bukkitPlayer);

                bukkitPlayer.sendPacket(new CPacketCustomAction(bukkitPlayer.getNick())
                        .type(PacketType.BUNGEE_SET_RANDOM_SKIN));
            }
        }
    }

    private boolean validString(String str) {
        return str.matches("[a-zA-Z\\d]+") && !str.toLowerCase().contains(".com") && !str.toLowerCase().contains(".")
                && !str.toLowerCase().contains("lixo") && !str.toLowerCase().contains("ez")
                && !str.toLowerCase().contains("lag") && !str.toLowerCase().contains("merda")
                && !str.toLowerCase().contains("mush") && !str.toLowerCase().contains("server")
                && !str.toLowerCase().contains("fdp")
                && !str.toLowerCase().contains("zenix") && !str.toLowerCase().contains("empire")
                && !str.toLowerCase().contains("battle") && !str.toLowerCase().contains("like")
                && (!str.toLowerCase().contains("kits"));
    }

    private boolean isOriginal(String nick) {
        try {
            return CommonsGeneral.getUUIDFetcher().getUUID(nick) != null;
        } catch (UUIDFetcherException ex) {
            return true;
        }
    }

    private String getRandomFake() {
        String randomNick = "";

        if (random.nextBoolean()) {
            randomNick = names[random.nextInt(names.length - 1)] + random.nextInt(6000)
                    + names[random.nextInt(names.length - 1)];
        } else {
            randomNick = names[random.nextInt(names.length - 1)] + names[random.nextInt(names.length - 1)]
                    + random.nextInt(6000);
        }

        if (randomNick.length() > 16) {
            randomNick = getRandomFake();
        }
        return randomNick;
    }

    private boolean requestChangeNick(Player player, boolean colocar) {
        PlayerRequestEvent event = new PlayerRequestEvent(player, "fake");
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            player.sendMessage(BukkitMessages.NAO_PODE_TIRAR_FAKE.replace("%action%", colocar ? "colocar" : "tirar"));
            return false;
        }
        return true;
    }
}