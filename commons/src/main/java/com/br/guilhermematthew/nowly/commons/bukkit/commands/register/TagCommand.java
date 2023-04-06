package com.br.guilhermematthew.nowly.commons.bukkit.commands.register;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.tag.TagManager;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Completer;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import lombok.val;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TagCommand implements CommandClass {

    @Command(name = "tag")
    public void tag(BukkitCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) return;

        Player player = commandSender.getPlayer();

        if (args.length == 0) {
            sendTags(player);
        } else if (args.length == 1) {
            String selectedGroup = args[0];

            if (Groups.existGrupo(selectedGroup)) {
                Groups group = Groups.getGroup(selectedGroup);

                if (TagManager.hasPermission(player, group)) {
                    BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());

                    if (!bukkitPlayer.getActualTag().getName().equals(group.getTag().getName())) {
                        bukkitPlayer.updateTag(player, group.getTag(), false);
                    } else {
                        player.sendMessage(BukkitMessages.VOCE_JA_ESTA_USANDO_ESTA_TAG);
                    }
                } else {
                    player.sendMessage(BukkitMessages.NAO_POSSUI_TAG);

                }
            } else {
                sendTags(player);
            }
        }
    }

    private void sendTags(final Player player) {
        List<Groups> playerGroups = TagManager.getPlayerGroups(player);

        TextComponent message = new TextComponent(BukkitMessages.SUAS_TAGS);

        for (int i = 0; i < playerGroups.size(); i++) {
            message.addExtra(i == 0 ? " " : "§f, ");

            if (i == 0) {
                message.addExtra("");
            }

            Groups group = playerGroups.get(i);

            BaseComponent baseComponent = new TextComponent(group.getName());
            baseComponent.setColor(ChatColor.getByChar(group.getColor().charAt(1)));

            baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new BaseComponent[]{new TextComponent("§7Prévia: " + group.getColor() + "§l" + group.getTag().getPrefix() + " " + group.getColor() + player.getName() + "\n§eClique para selecionar!")}));
            baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tag " + group.getName()));

            message.addExtra(baseComponent);
        }

        message.addExtra("§f.");

        player.spigot().sendMessage(message);
    }

    @Completer(name = "tag")
    public List<String> tagCompleter(BukkitCommandSender sender, String label, String[] args) {
        if(!sender.isPlayer()) return Collections.emptyList();

        val p = sender.getPlayer();

        if(args.length < 1) return Arrays.stream(Groups.values())
                .filter(group -> TagManager.hasPermission(p, group))
                .map(Groups::getName)
                .collect(Collectors.toList());

        if(args.length > 1) return Collections.emptyList();

        return Arrays.stream(Groups.values())
                .filter(group -> group.getName().toLowerCase().startsWith(args[0].toLowerCase()) && TagManager.hasPermission(p, group))
                .map(Groups::getName)
                .collect(Collectors.toList());
    }
}