package com.br.guilhermematthew.nowly.commons.bukkit.commands.register;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.profile.addons.League;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import org.bukkit.entity.Player;

public class RankCommand implements CommandClass {

    @Command(name = "rank", aliases = {"rank", "liga", "ligas", "nivel", "level", "nvl", "lvl"})
    public void rank(BukkitCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        Player player = commandSender.getPlayer();

        BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
        final int XP = bukkitPlayer.getInt(DataType.XP);
        League liga = League.getRanking(XP);
        final int leftXp = liga.getExperience() - XP;
        final int porcentLevel = liga.getExperience() - XP;
        final String barExp = this.display(player, leftXp, liga.getExperience());
        League next = liga.getNextLeague();
        player.sendMessage("");
        player.sendMessage("§f" + liga.getColor() + liga.getName() + " §7" + String.valueOf(barExp) + "§7>" + " §f" + next.getNextLeague().getColor() + next.getNextLeague().getName() + " §7" + XP + "§7/" + next.getNextLeague().getExperience() + "§7 XP's" + "§f (" + porcentLevel * 100 / liga.getExperience() + "§f%)");

        player = null;
        bukkitPlayer = null;
        liga = null;
    }

    @Command(name = "ranklist", aliases = {"ranks", "lvllist", "leaguelist", "ligalist", "ligal"})
    public void rankList(BukkitCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        Player player = commandSender.getPlayer();

        BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
        final int XP = bukkitPlayer.getInt(DataType.XP);
        League liga = League.getRanking(XP);
        final int leftXp = liga.getExperience() - XP;
        final int porcentLevel = liga.getExperience() + XP;
        final String barExp = this.display(player, leftXp, liga.getExperience());
        League next = liga.getNextLeague();
        player.sendMessage("");
        player.sendMessage("");
        player.sendMessage("§aLista de ranks:");
        player.sendMessage(" §4❂ Champion");
        player.sendMessage(" §c✿ Immortal: " + League.ImmortalI.getSymbol() + "§f, §c" + League.ImmortalII.getSymbol() + "§f, §c" + League.ImmortalIII.getSymbol() + "§f, §c" + League.ImmortalIV.getSymbol());
        player.sendMessage(" §5✦ Enderlore: " + League.EnderloreI.getSymbol() + "§f, §5" + League.EnderloreII.getSymbol() + "§f, §5" + League.EnderloreIII.getSymbol() + "§f, §5" + League.EnderloreIV.getSymbol() + "§f, §5" + League.EnderloreV.getSymbol());
        player.sendMessage(" §b❆ Diamond: " + League.DiamondI.getSymbol() + "§f, §b" + League.DiamondII.getSymbol() + "§f, §b" + League.DiamondIII.getSymbol() + "§f, §b" + League.DiamondIV.getSymbol() + "§f, §b" + League.DiamondV.getSymbol());
        player.sendMessage(" §3❖ Platinum: " + League.PlatinumI.getSymbol() + "§f, §3" + League.PlatinumII.getSymbol() + "§f, §3" + League.PlatinumIII.getSymbol() + "§f, §3" + League.PlatinumIV.getSymbol() + "§f, §3" + League.PlatinumV.getSymbol());
        player.sendMessage(" §6✻ Gold: " + League.GoldI.getSymbol() + "§f, §6" + League.GoldII.getSymbol() + "§f, §6" + League.GoldIII.getSymbol() + "§f, §6" + League.GoldIV.getSymbol() + "§f, §6" + League.GoldV.getSymbol());
        player.sendMessage(" §7✯ Bronze: " + League.BronzeI.getSymbol() + "§f, §7" + League.BronzeII.getSymbol() + "§f, §7" + League.BronzeIII.getSymbol() + "§f, §7" + League.BronzeIV.getSymbol() + "§f, §7" + League.BronzeV.getSymbol());
        player.sendMessage("");

        player = null;
        bukkitPlayer = null;
        liga = null;
    }

    private String display(final Player player, final int remaing, final int total) {
        final StringBuilder bar = new StringBuilder();
        final double percentage = remaing * 100 / total;
        final double count = 20.0 - Math.max((percentage > 0.0) ? 1 : 0, percentage / 5.0);
        for (int a = 0; a < count; ++a) {
            bar.append("§a§m-");
        }
        for (int a = 0; a < 20.0 - count; ++a) {
            bar.append("§7§m-");
        }
        return bar.toString();
    }

}