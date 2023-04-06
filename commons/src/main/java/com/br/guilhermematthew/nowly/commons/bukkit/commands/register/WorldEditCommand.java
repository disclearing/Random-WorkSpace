package com.br.guilhermematthew.nowly.commons.bukkit.commands.register;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.bukkit.worldedit.Constructions;
import com.br.guilhermematthew.nowly.commons.bukkit.worldedit.WorldEditManager;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WorldEditCommand implements CommandClass {

    @SuppressWarnings("deprecation")
    @Command(name = "worldedit", aliases = {"we"}, groupsToUse = {Groups.ADMIN})
    public void worldedit(BukkitCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        if (args.length == 0) {
            commandSender.sendMessage("");
            commandSender.sendMessage("§cUse: /worldedit <Machado/Undo>");
            commandSender.sendMessage("§cUse: /worldedit set <ID>");
            commandSender.sendMessage("§cUse: /worldedit setblockpertick <Quantidade>");
            commandSender.sendMessage("");
            return;
        }
        Player player = commandSender.getPlayer();
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("machado")) {
                player.getInventory().addItem(new ItemBuilder().type(Material.WOOD_AXE).name("§e§lWORLDEDIT").build());
                player.sendMessage("§e§lWORLDEDIT §fVocê recebeu o Machado.");
            } else if (args[0].equalsIgnoreCase("undo")) {
                if (!WorldEditManager.hasRollingConstructionByUUID(player.getUniqueId())) {
                    commandSender.sendMessage("§e§lWORLDEDIT §fVocê não possuí uma construção.");
                    return;
                }
                Constructions construction = WorldEditManager.getConstructionByUUID(player.getUniqueId());
                if (construction.isFinished()) {
                    commandSender.sendMessage("§e§lWORLDEDIT §fA construção ainda está em andamento.");
                    return;
                }
                if (construction.isResetando()) {
                    commandSender.sendMessage("§e§lWORLDEDIT §fA construção já está em sendo resetada.");
                    return;
                }
                construction.startRegress();
                commandSender.sendMessage("§e§lWORLDEDIT §fRetirando blocos...");
            } else {
                commandSender.sendMessage("");
                commandSender.sendMessage("§cUse: /worldedit <Machado/Undo>");
                commandSender.sendMessage("§cUse: /worldedit set <ID>");
                commandSender.sendMessage("§cUse: /worldedit setblockpertick <Quantidade>");
                commandSender.sendMessage("");
            }
        } else if (args.length == 2) {
            if (args[0].contentEquals("setblockpertick")) {
                if (!StringUtility.isInteger(args[1])) {
                    commandSender.sendMessage("§cUse: /worldedit setblockpertick <Quantidade>");
                    return;
                }
                if (!WorldEditManager.hasRollingConstructionByUUID(player.getUniqueId())) {
                    commandSender.sendMessage("§e§lWORLDEDIT §fVocê não possuí uma construção.");
                    return;
                }
                int quantia = Integer.valueOf(args[1]);
                if (quantia > 1000) {
                    commandSender.sendMessage("§e§lWORLDEDIT §fValor máximo de apenas 1,000 blocos.");
                    return;
                }
                Constructions construction = WorldEditManager.getConstructionByUUID(player.getUniqueId());
                construction.setBlocksPerTick(quantia);
                commandSender.sendMessage("§e§lWORLDEDIT §fValor alterado.");
            } else if (args[0].equalsIgnoreCase("set")) {
                String idsOriginal = args[1];
                String ids = args[1].replaceAll(",", "");
                if (!StringUtility.isInteger(ids)) {
                    commandSender.sendMessage("§cUse: /worldedit set <ID>");
                    return;
                }
                if (WorldEditManager.hasRollingConstructionByUUID(player.getUniqueId())) {
                    if (!WorldEditManager.getConstructionByUUID(player.getUniqueId()).isFinished()) {
                        commandSender.sendMessage("§e§lWORLDEDIT §fVocê já possuí uma construção em andamento.");
                        return;
                    }
                }
                if (WorldEditManager.continueEdit(player)) {
                    List<Material> materiaisIds = new ArrayList<>();

                    if (idsOriginal.contains(",")) {
                        boolean error = false;
                        for (String string : idsOriginal.split(",")) {
                            try {
                                materiaisIds.add(Material.getMaterial(Integer.valueOf(string)));
                            } catch (NullPointerException ex) {
                                error = true;
                                break;
                            }
                        }
                        if (error) {
                            player.sendMessage("§e§lWORLDEDIT §fOcorreu um erro ao tentar encontrar o material.");
                            return;
                        }

                    } else {
                        try {
                            materiaisIds.add(Material.getMaterial(Integer.valueOf(args[1])));
                        } catch (NullPointerException ex) {
                            player.sendMessage("§e§lWORLDEDIT §fOcorreu um erro ao tentar encontrar o material.");
                            return;
                        }
                    }

                    player.sendMessage("§e§lWORLDEDIT §fProcessando blocos...");
                    List<Location> locations = null;
                    try {
                        locations = WorldEditManager.getLocationsFromTwoPoints(
                                WorldEditManager.getPos1(player), WorldEditManager.getPos2(player));
                    } catch (Exception ex) {
                        player.sendMessage("§e§lWORLDEDIT §fErro ao processar os blocos...");
                        return;
                    } finally {
                        player.sendMessage("§e§lWORLDEDIT §e" + StringUtility.formatValue(locations.size()) + " §fblocos processados.");
                    }

                    WorldEditManager.addConstructionByUUID(player, locations);
                    WorldEditManager.getConstructionByUUID(player.getUniqueId()).setBlocksToSet(materiaisIds);
                    WorldEditManager.getConstructionByUUID(player.getUniqueId()).start();

                    locations.clear();
                    materiaisIds.clear();
                }
            } else {
                commandSender.sendMessage("");
                commandSender.sendMessage("§cUse: /worldedit <Machado/Undo>");
                commandSender.sendMessage("§cUse: /worldedit set <ID>");
                commandSender.sendMessage("§cUse: /worldedit setblockpertick <Quantidade>");
                commandSender.sendMessage("");
            }
        } else {
            commandSender.sendMessage("");
            commandSender.sendMessage("§cUse: /worldedit <Machado/Undo>");
            commandSender.sendMessage("§cUse: /worldedit set <ID>");
            commandSender.sendMessage("§cUse: /worldedit setblockpertick <Quantidade>");
            commandSender.sendMessage("");
        }
    }
}