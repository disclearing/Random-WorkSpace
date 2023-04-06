package com.br.guilhermematthew.nowly.commons.bukkit.listeners;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AfkListener implements Listener {

   /* HashMap<Player, Long> playerAfkHashList = new HashMap<>();
    ArrayList<Player> removePlayer = new ArrayList<>();

    public void checkPlayerAfk() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(BukkitMain.getInstance(), () -> {
            for (Map.Entry<Player, Long> entry : playerAfkHashList.entrySet()) {

                if ((System.currentTimeMillis() - entry.getValue()) > 5 * 20) {
                    Player player = entry.getKey();
                    removePlayer.add(player);
                    connectMessage(player, "afk");
                }
            }
            for (Player removePlayer : removePlayer) {
                playerAfkHashList.remove(removePlayer);
            }
        }, 0, 20);

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        playerAfkHashList.put(event.getPlayer(), System.currentTimeMillis());
    }

    public void findServer(Player p, ServerType serverType) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("FindServer");
        out.writeUTF(serverType.name());
        p.sendPluginMessage(BukkitMain.getInstance(), "BungeeCord", out.toByteArray());
    }

    public void connectMessage(Player p, String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        p.sendPluginMessage(BukkitMain.getInstance(), "BungeeCord", out.toByteArray());
    }
*/

}
