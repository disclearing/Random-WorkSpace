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

public class PlayerStatisticsInventory extends MenuInventory {

    public PlayerStatisticsInventory(final String nickViewer, final String nickAlvo, GamingProfile profile) {
        super(nickAlvo.equalsIgnoreCase(nickViewer) ? "Suas estatísticas" : "Estatísticas de " + nickAlvo, 3);

        final DataCategory[] CATEGORYS_TO_LOAD = {DataCategory.HARDCORE_GAMES, DataCategory.KITPVP, DataCategory.GLADIATOR};
        Player target = BukkitServerAPI.getExactPlayerByNick(nickAlvo);
        GamingProfile profile2 = getProfile(target, nickAlvo);
        if (profile2 == null)
            return;
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

        setItem(11, itemBuilder.type(Material.IRON_CHESTPLATE).name("§aEstatísticas do PvP").lore(
                "§7Arena Kills: §a" + profile.getIntFormatted(DataType.PVP_KILLS),
                "§7Arena Deaths: §a" + profile.getIntFormatted(DataType.PVP_DEATHS),
                "§7Arena Maior Killstreak: §a" + profile.getIntFormatted(DataType.PVP_MAXKILLSTREAK),
                "",
                "§7Fps Kills: §a" + profile.getIntFormatted(DataType.FPS_KILLS),
                "§7Fps Deaths: §a" + profile.getIntFormatted(DataType.FPS_DEATHS),
                "§7Fps Maior Killstreak: §a" + profile.getIntFormatted(DataType.FPS_MAXKILLSTREAK),
                "",
                "§7Coins: §6" + profile.getIntFormatted(DataType.COINS)).build());

        setItem(10, itemBuilder.type(Material.MUSHROOM_SOUP).name("§aEstatísticas do HG").lore(
                "§7HG Wins: §a" + profile.getIntFormatted(DataType.HG_WINS),
                "§7HG Kills: §a" + profile.getIntFormatted(DataType.HG_KILLS),
                "§7HG Deaths: §a" + profile.getIntFormatted(DataType.HG_DEATHS),
                "",
                "§7Champions Wins: §a" + profile.getIntFormatted(DataType.HG_EVENT_WINS),
                "§7Champions Kills: §a" + profile.getIntFormatted(DataType.HG_EVENT_KILLS),
                "§7Champions Deaths: §a" + profile.getIntFormatted(DataType.HG_EVENT_DEATHS),
                "",
                "§7Coins: §6" + profile.getIntFormatted(DataType.COINS)).build());

        setItem(12, itemBuilder.type(Material.DIAMOND_SWORD).name("§aEstatísticas do Duels").lore(
                "§eClique para ver mais!").build(), new MenuClickHandler() {

            @Override
            public void onClick(Player player, Inventory inventory, ClickType type, ItemStack itemStack, int slot) {
                player.closeInventory();

                new DuelsInfoInventory(nickViewer, nickAlvo, profile2).open(player);
            }
        });

        setItem(22, itemBuilder.type(Material.ARROW).name("§eRetornar").lore(
                "§7a Suas estatísticas").build(), new MenuClickHandler() {

            @Override
            public void onClick(Player player, Inventory inventory, ClickType type, ItemStack itemStack, int slot) {
                player.closeInventory();

                new AccountInventory(nickViewer, nickAlvo).open(player);
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
