package com.br.guilhermematthew.nowly.commons.bukkit.commands.register;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class InvseeCommand implements CommandClass {

    @Command(name = "invsee", aliases = {"inv"}, groupsToUse = {Groups.PRIME})
    public void invsee(BukkitCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        Player p = commandSender.getPlayer();
        if (args.length == 0) {
            p.sendMessage(BukkitMessages.INVSEE);
        } else if (args.length == 1) {
            Player d = Bukkit.getPlayer(args[0]);

            if (d == null) {
                p.sendMessage(BukkitMessages.JOGADOR_OFFLINE);
                return;
            }
            if (d == p) {
                p.sendMessage(BukkitMessages.VOCE_NAO_PODE_ABRIR_SEU_INVENTARIO);
                return;
            }

            p.setMetadata("inventory-view", new FixedMetadataValue(BukkitMain.getInstance(), d.getUniqueId().toString()));
            p.openInventory(d.getInventory());
            p.sendMessage(BukkitMessages.INVSEE_SUCESSO.replace("%nick%", d.getName()));

            BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(p) + " abriu o inventário de " + d.getName() + "]", Groups.ADMIN);
        } else {
            p.sendMessage(BukkitMessages.INVSEE);
        }
    }
}