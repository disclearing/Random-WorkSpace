package com.br.guilhermematthew.nowly.commons.bukkit.api.vanish;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerAdminChangeEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerAdminChangeEvent.AdminChangeType;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.tag.TagManager;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.tag.Tag;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class VanishAPI {

    private static final List<Player> invisiveis = new ArrayList<>();
    private static final List<Player> admin = new ArrayList<>();

    /*
    private static final HashMap<UUID, ItemStack[]> itens = new HashMap<>();
    private static final HashMap<UUID, ItemStack[]> armadura = new HashMap<>();*/

    public static void hide(Player player) {
        if (invisiveis.contains(player)) {
            return;
        }

        invisiveis.add(player);

        Tag tag =
                BukkitMain.getBukkitPlayer(player.getUniqueId()).getGroup().getTag();

        for (Player onlines : Bukkit.getOnlinePlayers()) {
            if (!TagManager.hasPermission(onlines, tag)) {
                onlines.hidePlayer(player);
            }
        }

        player.sendMessage(BukkitMessages.PLAYER_FICOU_INVISIVEL.replace("%grupo%", tag.getColor() + "§l" + tag.getName()));
    }

    public static void show(Player player) {
        if (!invisiveis.contains(player)) {
            return;
        }

        invisiveis.remove(player);

        Bukkit.getOnlinePlayers().forEach(onlines -> onlines.showPlayer(player));

        player.sendMessage(BukkitMessages.PLAYER_FICOU_VISIVEL);
    }

    public static void updateInvisibles(Player player) {
        for (Player invisible : invisiveis) {

            if (invisible != null && invisible.isOnline()) {
                Tag tag = BukkitMain.getBukkitPlayer(invisible.getUniqueId()).getGroup().getTag();

                if (TagManager.hasPermission(player, tag)) {
                    player.showPlayer(invisible);
                } else {
                    player.hidePlayer(invisible);
                }
            }
        }
    }

    public static void changeAdmin(Player player) {
        changeAdmin(player, true);
    }

    public static void changeAdmin(Player player, boolean callEvent) {
        boolean inAdmin = admin.contains(player);

        Groups playerGroup = BukkitMain.getBukkitPlayer(player.getUniqueId()).getGroup();

        if (inAdmin) {
            //saiu do admin
           /* player.getInventory().clear();
            player.getInventory().setArmorContents(null);

            player.getInventory().setContents(itens.get(player.getUniqueId()));
            player.getInventory().setArmorContents(armadura.get(player.getUniqueId()));

            armadura.remove(player.getUniqueId());
            itens.remove(player.getUniqueId());*/
            admin.remove(player);

            invisiveis.remove(player);
        } else {
            if (!admin.contains(player)) {
                admin.add(player);
            }

            if (!invisiveis.contains(player)) {
                invisiveis.add(player);
            }

            /*
            itens.put(player.getUniqueId(), player.getInventory().getContents());
            armadura.put(player.getUniqueId(), player.getInventory().getArmorContents());

            player.getInventory().clear();
            player.getInventory().setArmorContents(null);*/
        }

        for (Player onlines : Bukkit.getOnlinePlayers()) {
            if (player == onlines) {
                continue;
            }

            if (inAdmin) {
                onlines.showPlayer(player);
            }

            if (TagManager.hasPermission(onlines, playerGroup)) {
                onlines.sendMessage(inAdmin ? BukkitMessages.PLAYER_SAIU_DO_ADMIN.replace("%nick%", player.getName()) :
                        BukkitMessages.PLAYER_ENTROU_NO_ADMIN.replace("%nick%", player.getName()));
            } else {
                if (!inAdmin) {
                    onlines.hidePlayer(player);
                }
            }
        }

        if (callEvent) {
            Bukkit.getPluginManager().callEvent(new PlayerAdminChangeEvent(player, inAdmin ? AdminChangeType.SAIU : AdminChangeType.ENTROU));
        }

        player.setGameMode(inAdmin ? GameMode.SURVIVAL : GameMode.CREATIVE);

        player.sendMessage(inAdmin ? BukkitMessages.SAIU_DO_ADMIN : BukkitMessages.ENTROU_NO_ADMIN);
    }

    public static void remove(Player player) {
        admin.remove(player);
        invisiveis.remove(player);
        /*itens.remove(uniqueId);
        armadura.remove(uniqueId);*/
    }

    public static boolean inAdmin(Player player) {
        return admin.contains(player);
    }

    public static boolean isInvisible(Player player) {
        return invisiveis.contains(player);
    }
}