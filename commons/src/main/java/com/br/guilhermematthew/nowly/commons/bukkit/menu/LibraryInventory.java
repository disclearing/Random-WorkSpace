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
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.profile.GamingProfile;
import com.br.guilhermematthew.nowly.commons.common.profile.addons.League;
import com.br.guilhermematthew.nowly.commons.common.utility.skin.Skin;
import com.br.guilhermematthew.nowly.commons.custompackets.PacketType;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketCustomAction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class LibraryInventory extends MenuInventory {

    public LibraryInventory(final String nickViewer, String nickAlvo) {
        super("Biblioteca de Skins", 5);

        Player target = BukkitServerAPI.getExactPlayerByNick(nickAlvo);
        GamingProfile profile = getProfile(target, nickAlvo);
        if (profile == null)
            return;

        Groups playerGroup = profile.getGroup();
        League playerLeague = League.getRanking(profile.getInt(DataType.XP));

        ItemBuilder itemBuilder = new ItemBuilder();

        setItem(10, itemBuilder.type(Material.SKULL_ITEM).durability(3)
                .skinURL("http://textures.minecraft.net/texture/a7af2b86c73a493e1ef2eac449de5c080e01586d34c682c78a5638dbf76ed9d1")
                .name("§aBiel")
                .lore("§eClique para selecionar")
                .build(), new MenuClickHandler() {

            @Override
            public void onClick(Player player, Inventory inventory, ClickType type, ItemStack itemStack, int slot) {
                BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
                player.closeInventory();
                bukkitPlayer.sendPacket(new CPacketCustomAction(bukkitPlayer.getNick())
                        .type(PacketType.BUNGEE_SET_SKIN).field("biel"));
            }
        });

        setItem(11, itemBuilder.type(Material.SKULL_ITEM).durability(3)
                .skinURL("http://textures.minecraft.net/texture/548a43d56f2940a43ebdacf142d5c90a005685f930247420646280c4af60e03e")
                .name("§aTh")
                .lore("§eClique para selecionar")
                .build(), new MenuClickHandler() {

            @Override
            public void onClick(Player player, Inventory inventory, ClickType type, ItemStack itemStack, int slot) {
                BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
                player.closeInventory();
                bukkitPlayer.sendPacket(new CPacketCustomAction(bukkitPlayer.getNick())
                        .type(PacketType.BUNGEE_SET_SKIN).field("zFlint"));
            }
        });

        setItem(12, itemBuilder.type(Material.SKULL_ITEM).durability(3)
                .skinURL("http://textures.minecraft.net/texture/7ebbe3ad08ac92f1bfafa132e1be48f5ad41a67aac16bba325669190d2f21523")
                .name("§atheoow")
                .lore("§eClique para selecionar")
                .build(), new MenuClickHandler() {

            @Override
            public void onClick(Player player, Inventory inventory, ClickType type, ItemStack itemStack, int slot) {
                BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
                player.closeInventory();
                bukkitPlayer.sendPacket(new CPacketCustomAction(bukkitPlayer.getNick())
                        .type(PacketType.BUNGEE_SET_SKIN).field("theoow"));
            }
        });

        GamingProfile finalProfile = profile;

        setItem(26, itemBuilder.type(Material.ARROW).name("§eRetornar").lore(
                "§7a Meu perfil").build(), new MenuClickHandler() {

            @Override
            public void onClick(Player player, Inventory inventory, ClickType type, ItemStack itemStack, int slot) {
                player.closeInventory();
                new AccountInventory(nickViewer).open(player);
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