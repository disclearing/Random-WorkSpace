package com.br.guilhermematthew.nowly.commons.bukkit.commands.register;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.api.vanish.VanishAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.data.DataHandler;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.tag.Tag;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TellCommand implements CommandClass {

    @Command(name = "tell", aliases = {"pm"}, runAsync = true)
    public void tell(BukkitCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        Player player = commandSender.getPlayer();

        if (args.length == 0) {
            player.sendMessage(BukkitMessages.COMMAND_TELL_USAGE);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
                DataHandler dataHandler = BukkitMain.getBukkitPlayer(player.getUniqueId()).getDataHandler();
                if (dataHandler.getBoolean(DataType.RECEIVE_PRIVATE_MESSAGES)) {
                    player.sendMessage(BukkitMessages.TELL_JA_ESTA_ATIVADO);
                    return;
                }
                dataHandler.getData(DataType.RECEIVE_PRIVATE_MESSAGES).setValue(true);
                player.sendMessage(BukkitMessages.TELL_ATIVADO);

                BukkitMain.runAsync(() -> dataHandler.saveCategory(DataCategory.ACCOUNT));
            } else if (args[0].equalsIgnoreCase("off")) {
                DataHandler dataHandler = BukkitMain.getBukkitPlayer(player.getUniqueId()).getDataHandler();
                if (!dataHandler.getBoolean(DataType.RECEIVE_PRIVATE_MESSAGES)) {
                    player.sendMessage(BukkitMessages.TELL_JA_ESTA_ATIVADO);
                    return;
                }
                dataHandler.getData(DataType.RECEIVE_PRIVATE_MESSAGES).setValue(false);
                player.sendMessage(BukkitMessages.TELL_DESATIVADO);

                BukkitMain.runAsync(() -> dataHandler.saveCategory(DataCategory.ACCOUNT));
            } else {
                player.sendMessage(BukkitMessages.COMMAND_TELL_USAGE);
            }
        } else {
            handleTell(player, Bukkit.getPlayer(args[0]), StringUtility.createArgs(1, args));
        }
    }

    private void handleTell(Player sender, Player receiver, String mensagem) {
        if (receiver == null) {
            sender.sendMessage(BukkitMessages.JOGADOR_OFFLINE);
            return;
        }
        if (receiver == sender) {
            sender.sendMessage(BukkitMessages.VOCE_NAO_PODE_ENVIAR_MENSAGEM_PARA_VOCE_MESMO);
            return;
        }

        BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(sender.getUniqueId()),
                bukkitPlayer1 = BukkitMain.getBukkitPlayer(receiver.getUniqueId());

        if (VanishAPI.isInvisible(receiver)) {
            if (bukkitPlayer.getGroup().getLevel() <= Groups.PRIME.getLevel()) {
                sender.sendMessage(BukkitMessages.JOGADOR_OFFLINE);
                return;
            }
        }

        if (!bukkitPlayer1.getBoolean(DataType.RECEIVE_PRIVATE_MESSAGES)) {
            sender.sendMessage(BukkitMessages.JOGADOR_COM_TELL_DESATIVADO);
            return;
        }

        bukkitPlayer.setLastMessage(receiver.getName());
        bukkitPlayer1.setLastMessage(sender.getName());

        final Tag tag = bukkitPlayer.getGroup().getTag(), tag1 = bukkitPlayer1.getGroup().getTag();

        sender.sendMessage(BukkitMessages.TELL_PARA_JOGADOR.replace("%nick%", tag1.getColor() + receiver.getName())
                .replace("%mensagem%", mensagem));

        receiver.sendMessage(BukkitMessages.TELL_DE_JOGADOR.replace("%nick%", tag.getColor() + sender.getName())
                .replace("%mensagem%", mensagem));

        if (SpyCommand.spying.size() != 0) {
            for (Player spys : SpyCommand.getSpys()) {
                if (SpyCommand.isSpyingAll(spys)) {
                    spys.sendMessage(BukkitMessages.TELL_SPY.replace("%nick%", tag.getColor() + sender.getName())
                            .replace("%nick1%", tag1.getColor() + receiver.getName()).replace("%mensagem%", mensagem));
                } else {
                    boolean send = false;

                    if (SpyCommand.isSpyingPlayer(spys, bukkitPlayer.getNick())) {
                        send = true;
                    } else if (SpyCommand.isSpyingPlayer(spys, bukkitPlayer.getNick())) {
                        send = true;
                    }

                    if (send) {
                        spys.sendMessage(BukkitMessages.TELL_SPY.replace("%nick%", tag.getColor() + sender.getName())
                                .replace("%nick1%", tag1.getColor() + receiver.getName())
                                .replace("%mensagem%", mensagem));
                    }
                }
            }
        }
    }

    @Command(name = "reply", aliases = {"r"})
    public void reply(BukkitCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        Player player = commandSender.getPlayer();
        if (args.length == 0) {
            player.sendMessage(BukkitMessages.COMMAND_REPLY_USAGE);
            return;
        }
        final BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
        if (bukkitPlayer.getLastMessage().equalsIgnoreCase("")) {
            player.sendMessage(BukkitMessages.VOCE_NAO_TEM_NENHUMA_CONVERSA_PARA_RESPONDER);
            return;
        }
        handleTell(player, Bukkit.getPlayer(bukkitPlayer.getLastMessage()), StringUtility.createArgs(0, args));
    }
}