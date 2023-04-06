package com.br.guilhermematthew.nowly.commons.bukkit.commands.register;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Completer;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.profile.addons.Medals;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import lombok.val;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MedalsCommand implements CommandClass {

    @Command(name = "medals", aliases = {"medal", "medalha", "medalhas"})
    public void medals(BukkitCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) return;

        Player player = commandSender.getPlayer();

        if (args.length >= 1) {

            final String medalName = StringUtility.createArgs(0, args);

            if (Medals.existMedal(medalName)) {

                final Medals medal = Medals.getMedalByName(medalName);
                if (player.hasPermission("medals." + medal.getName().toLowerCase()) || (player.hasPermission("medals.all"))) {
                    BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());

                    if (bukkitPlayer.getInt(DataType.MEDAL) == medal.getId()) {
                        player.sendMessage(BukkitMessages.TIROU_MEDALHA);
                        bukkitPlayer.set(DataType.MEDAL, 0);
                    } else {
                        bukkitPlayer.set(DataType.MEDAL, medal.getId());
                        player.sendMessage(BukkitMessages.MEDALHA_SELECIONADA.replace("%medalha%", medal.getColor() + medal.getName()));
                    }

                    BukkitMain.runAsync(() -> bukkitPlayer.getDataHandler().saveCategory(DataCategory.ACCOUNT));

                    if (!bukkitPlayer.containsFake()) bukkitPlayer.updateTag(player, bukkitPlayer.getActualTag(), true);
                } else {
                    player.sendMessage(BukkitMessages.NAO_POSSUI_MEDALHA);
                }
            } else {
                sendMedals(player);
            }
        } else {
            sendMedals(player);
        }
    }

    private void sendMedals(final Player player) {
        val playerMedals = getMedals(player);

        val message = new TextComponent(BukkitMessages.SUAS_MEDALHAS);

        for (int i = 0; i < playerMedals.size(); i++) {
            message.addExtra(i == 0 ? " " : "§f, ");
            if (i == 0) {
                message.addExtra("");
            }

            val medal = playerMedals.get(i);

            val baseComponent = new TextComponent(medal.getSymbol());
            baseComponent.setColor(ChatColor.getByChar(medal.getColor().charAt(1)));

            baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new BaseComponent[]{new TextComponent("§eClique para selecionar a " + medal.getColor() + medal.getSymbol())}));
            baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/medalha " + medal.getName()));

            message.addExtra(baseComponent);
        }

        message.addExtra("§f.");

        player.spigot().sendMessage(message);
    }

    private List<Medals> getMedals(final Player player) {
        if(player.hasPermission("medals.all")) return Arrays.asList(Medals.values());

        return Arrays.stream(Medals.values())
                .filter(medal -> player.hasPermission("medals." + medal.getName().toLowerCase()))
                .collect(Collectors.toList());
    }

    @Completer(name = "medals", aliases = {"medal", "medalha", "medalhas"})
    public List<String> medalCompleter(BukkitCommandSender sender, String label, String[] args) {
        if (sender.isPlayer()) {

            Player p = sender.getPlayer();

            if (args.length == 1) {
                List<String> list = new ArrayList<>();

                for (Medals m : Medals.values()) {
                    if (p.hasPermission("medals." + m.getName().toLowerCase()) || (p.hasPermission("medals.all"))) {
                        list.add(m.getName());
                    }
                }

                return list;
            }
        }
        return new ArrayList<>();
    }
}