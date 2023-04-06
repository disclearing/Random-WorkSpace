package com.br.guilhermematthew.nowly.commons.bukkit.menu;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.ClickType;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.MenuClickHandler;
import com.br.guilhermematthew.nowly.commons.bukkit.api.menu.MenuInventory;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.profile.GamingProfile;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class DuelsInfoInventory extends MenuInventory {

    public DuelsInfoInventory(final String nickViewer, final String nickAlvo, GamingProfile profile) {
        super(nickAlvo.equalsIgnoreCase(nickViewer) ? "Suas estatísticas (Duels)" : "Estatísticas de " + nickAlvo + " (Duels)", 3);
        Player target = BukkitServerAPI.getExactPlayerByNick(nickAlvo);
        GamingProfile profile2 = getProfile(target, nickAlvo);
        if (profile2 == null)
            return;
        final DataCategory[] CATEGORYS_TO_LOAD = {DataCategory.HARDCORE_GAMES, DataCategory.KITPVP, DataCategory.GLADIATOR};

        for (DataCategory data : CATEGORYS_TO_LOAD) {
            if(profile == null) break;

            try {
                profile.getDataHandler().load(data);
            } catch (SQLException e) {
                e.printStackTrace();
                profile = null;
            }
        }

        if (profile == null) return;

        ItemBuilder itemBuilder = new ItemBuilder();

        setItem(10, itemBuilder.type(Material.IRON_FENCE).name("§aGladiator").lore(
                "§7Solo Wins: §a" + profile.getIntFormatted(DataType.GLADIATOR_WINS),
                "§7Solo Losses: §a" + profile.getIntFormatted(DataType.GLADIATOR_LOSES),
                "§7Solo Winstreak: §a" + profile.getIntFormatted(DataType.GLADIATOR_WINSTREAK),
                "§7Solo Maior Winstreak: §a" + profile.getIntFormatted(DataType.GLADIATOR_MAXWINSTREAK),
                "",
                "§7Duplas Wins: §a0",
                "§7Duplas Losses: §a0",
                "§7Duplas Winstreak: §a0",
                "§7Duplas Maior Winstreak: §a0").build());

        setItem(12, itemBuilder.type(Material.MUSHROOM_SOUP).name("§aSopa").lore(
                "§7Solo Wins: §a0",
                "§7Solo Losses: §a0",
                "§7Solo Winstreak: §a0",
                "§7Solo Maior Winstreak: §a0",
                "",
                "§7Duplas Wins: §a0",
                "§7Duplas Losses: §a0",
                "§7Duplas Winstreak: §a0",
                "§7Duplas Maior Winstreak: §a0").build());

        setItem(11, itemBuilder.type(Material.WEB).name("§aSimulator").lore(
                "§7Solo Wins: §a0",
                "§7Solo Losses: §a0",
                "§7Solo Winstreak: §a0",
                "§7Solo Maior Winstreak: §a0",
                "",
                "§7Duplas Wins: §a0",
                "§7Duplas Losses: §a0",
                "§7Duplas Winstreak: §a0",
                "§7Duplas Maior Winstreak: §a0").build());

        setItem(22, itemBuilder.type(Material.ARROW).name("§eRetornar").lore(
                "§7a Suas estatísticas").build(), new MenuClickHandler() {

            @Override
            public void onClick(Player player, Inventory inventory, ClickType type, ItemStack itemStack, int slot) {
                player.closeInventory();

                new PlayerStatisticsInventory(nickViewer, nickAlvo, profile2).open(player);
            }
        });

    }

    private GamingProfile getProfile(Player target, final String nickAlvo) {
        GamingProfile profile2;

        if (target != null) {
            profile2 = BukkitMain.getBukkitPlayer(target.getUniqueId());
        } else {
            profile2 = new BukkitPlayer(nickAlvo, "", CommonsGeneral.getUUIDFetcher().getOfflineUUID(nickAlvo));
        }

        if (!profile2.getDataHandler().isCategoryLoaded(DataCategory.ACCOUNT)) {
            try {
                profile2.getDataHandler().load(DataCategory.ACCOUNT);
            } catch (SQLException e) {
                e.printStackTrace();
                profile2 = null;
            }
        }

        return profile2;
    }

}