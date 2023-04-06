package com.br.guilhermematthew.nowly.commons.bukkit.commands.register;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class SyncTpallCommand implements CommandClass {

    private boolean running = false;

    @Command(name = "synctpall", aliases = {"stpall"}, groupsToUse = {Groups.MOD})
    public void synctpall(BukkitCommandSender commandSender, String label, String[] args) {
        if (!commandSender.isPlayer()) {
            return;
        }

        if (running) {
            commandSender.sendMessage(BukkitMessages.SYNC_TPALL_RUNNING);
            return;
        }

        running = true;
        startSyncTpall(commandSender.getPlayer());

        BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(commandSender.getPlayer()) + " puxou todos os jogadores sincronizadamente]", Groups.ADMIN);
    }

    private void startSyncTpall(Player player) {
        new BukkitRunnable() {

            final Location loc = player.getLocation();
            final ArrayList<Player> players = (ArrayList<Player>) player.getWorld().getPlayers();
            final int toTeleport = players.size();
            int teleporteds = 0;

            public void run() {
                if (teleporteds >= toTeleport) {
                    cancel();
                    running = false;
                    if (player.isOnline()) {
                        player.sendMessage(BukkitMessages.SYNC_TPALL_FINALIZED);
                    }
                    return;
                }

                for (int i = 0; i < 2; i++) {
                    try {
                        Player t = players.get(teleporteds + i);
                        if ((t != null) && (t != player)) {
                            t.teleport(loc);
                        }
                    } catch (IndexOutOfBoundsException e) {
                    } catch (NullPointerException e) {
                    }
                }
                teleporteds = teleporteds += 2;
            }
        }.runTaskTimer(BukkitMain.getInstance(), 5L, 5L);
    }
}