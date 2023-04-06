package io.github.guilhermematthew.anticheat.list;

import io.github.guilhermematthew.anticheat.controller.PlayerController;
import io.github.guilhermematthew.anticheat.listeners.ConstantListeners;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class Autoclicker extends ConstantListeners {

    private Map<Player, PlayerController> clicksPerSecond;
    private Map<Player, Long> cooldownMap;

/*    public Autoclicker() {
        clicksPerSecond = new HashMap<>();
        cooldownMap = new HashMap<>();

        ProtocolLibrary.getProtocolManager()
                .addPacketListener(new PacketAdapter(BukkitMain.getInstance(), PacketType.Play.Client.ARM_ANIMATION) {

                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        Player player = event.getPlayer();

                        if (player == null || ProtocolGetter.getPing(player) >= 100)
                            return;

                        if (cooldownMap.containsKey(player) && cooldownMap.get(player) > System.currentTimeMillis())
                            return;

                        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.ADVENTURE)
                            return;

                        try {
                            if (player.getTargetBlock((Set<Material>) null, 4).getType() != Material.AIR) {
                                return;
                            }
                        } catch (IllegalStateException ex) {
                            return;
                        }

                        handle(player);
                    }

                });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(BlockDamageEvent event) {
        clicksPerSecond.remove(event.getPlayer());
        cooldownMap.put(event.getPlayer(), System.currentTimeMillis() + 10000l);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        clicksPerSecond.remove(event.getPlayer());
        cooldownMap.remove(event.getPlayer());
    }

    public void handle(Player player) {
        Clicks click = clicksPerSecond.computeIfAbsent(player, v -> new Clicks());

        if (click.getExpireTime() < System.currentTimeMillis()) {
            if (click.getClicks() >= 20) {
                alert(player, click.getClicks());
            }

            clicksPerSecond.remove(player);
            return;
        }

        click.addClick();
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (event.getType() == UpdateType.SECOND) {
            Iterator<Entry<Player, Clicks>> iterator = clicksPerSecond.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<Player, Clicks> entry = iterator.next();

                if (entry.getValue().getExpireTime() < System.currentTimeMillis()) {
                    if (entry.getValue().getClicks() >= 20) {
                        alert(entry.getKey(), entry.getValue().getClicks());
                    }

                    iterator.remove();
                }
            }
        }
    }*/
}
