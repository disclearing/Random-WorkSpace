package com.br.guilhermematthew.nowly.commons.bukkit.queue;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent.UpdateType;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerQueueEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.queue.player.PlayerQueue;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketAction;
import com.br.guilhermematthew.servercommunication.client.Client;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlayerBukkitQueue {

    private Listener bukkitListener;
    private List<PlayerQueue> players;
    private boolean destroyOnFinish, stopOnFinish;
    private int ticks;
    private QueueType queueType;

    public PlayerBukkitQueue(int ticks, QueueType queueType) {
        this(ticks, true, queueType);
    }

    public PlayerBukkitQueue(int ticks, boolean destroyOnFinish, QueueType queueType) {
        this.players = new ArrayList<>();

        this.destroyOnFinish = destroyOnFinish;
        this.ticks = ticks;
        this.queueType = queueType;
    }

    public void start() {
        this.bukkitListener = new Listener() {

            @EventHandler
            public void onSecond(BukkitUpdateEvent event) {
                if (event.getType() != UpdateType.TICK) return;

                if (event.getCurrentTick() % ticks != 0) return;

                if (getPlayers().size() == 0) {
                    if (isDestroyOnFinish()) {
                        destroy();
                    }
                    return;
                }

                if (getPlayers().size() == 0) {
                    return;
                }

                PlayerQueue current = getPlayers().get(0);
                if (current == null || !current.getPlayer().isOnline()) {
                    getPlayers().remove(current);

                    current.destroy();
                    current = null;
                    return;
                }

                getPlayers().remove(current);

                Bukkit.getServer().getPluginManager().callEvent(new PlayerQueueEvent(current.getPlayer(), queueType, current.getServer()));

                current.destroy();
                current = null;
            }
        };

        Bukkit.getServer().getPluginManager().registerEvents(bukkitListener, BukkitMain.getInstance());
    }

    public void destroy() {
        if (bukkitListener != null) {
            HandlerList.unregisterAll(bukkitListener);

            bukkitListener = null;
        }

        if (isStopOnFinish()) {
            Client.getInstance().getClientConnection().sendPacket(
                    new CPacketAction(BukkitMain.getServerType().getName(), BukkitMain.getServerID()).writeType("Loggout"));

            BukkitMain.runLater(() -> {
                for (Player onlines : Bukkit.getOnlinePlayers()) {
                    onlines.kickPlayer("§cNão foi possível conectar-se ao servidor.");
                }

                Bukkit.shutdown();
            }, 30);
        }
    }

    public boolean addToQueue(final Player player) {
        return addToQueue(player, "");
    }

    public boolean addToQueue(final Player player, final String server) {
        if (getQueuePlayer(player) != null)
            return false;

        getPlayers().add(new PlayerQueue(player, server));
        return true;
    }

    public PlayerQueue getQueuePlayer(Player player) {
        return this.players.stream().filter(qp -> qp.player.equals(player)).findFirst().orElse(null);
    }
}