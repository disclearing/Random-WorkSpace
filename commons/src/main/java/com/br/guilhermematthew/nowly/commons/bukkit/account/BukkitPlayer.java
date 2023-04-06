package com.br.guilhermematthew.nowly.commons.bukkit.account;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.account.permission.PlayerAttachment;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerChangeTagEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerRequestEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.tag.TagManager;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.profile.GamingProfile;
import com.br.guilhermematthew.nowly.commons.common.profile.addons.League;
import com.br.guilhermematthew.nowly.commons.common.tag.Tag;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class BukkitPlayer extends GamingProfile {

    private String lastMessage;
    private Long lastChangeSkin;
    private Property lastSkin;
    private PlayerAttachment playerAttachment;
    private Tag actualTag;

    public BukkitPlayer(String nick, String address, UUID uniqueId) {
        super(nick, address, uniqueId);
        this.lastMessage = "";
        this.lastChangeSkin = 0L;
        this.actualTag = Groups.MEMBRO.getTag();
    }


    public void addXP(final int amount) {
        League actualLeague = League.getRanking(getInt(DataType.XP)),
                newLeague = League.getRanking(getInt(DataType.XP) + amount);

        getData(DataType.XP).add(amount);

        if (actualLeague != newLeague) {
            Player player = getPlayer();

            Bukkit.broadcastMessage(BukkitMessages.PLAYER_SUBIU_DE_LIGA.replace("%nick%", player.getName()).replace("%liga%", newLeague.getColor() +
                    newLeague.getName().toUpperCase()));

            player.sendMessage(BukkitMessages.VOCE_SUBIU_DE_LIGA);

            TagManager.setTag(player, getActualTag(), this);
            Bukkit.getServer().getPluginManager().callEvent(new PlayerRequestEvent(player, "update-scoreboard"));
        }
    }

    public void removeXP(final int amount) {
        League actualLeague = League.getRanking(getInt(DataType.XP));

        getData(DataType.XP).remove(amount);

        League newLeague = League.getRanking(getInt(DataType.XP));

        if (actualLeague != newLeague) {
            Player player = getPlayer();

            Bukkit.getServer().getPluginManager().callEvent(new PlayerRequestEvent(player, "update-scoreboard"));
            TagManager.setTag(player, getActualTag(), this);
        }
    }

    public void updateTag(final Player player, final Tag newTag, boolean forced) {
        PlayerChangeTagEvent event = new PlayerChangeTagEvent(player, getActualTag(), newTag, forced);

        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        if(actualTag != newTag) {
            set(DataType.TAG, newTag.getName());

            BukkitMain.runAsync(() -> getDataHandler().saveCategory(DataCategory.ACCOUNT));
        }

        this.actualTag = newTag;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getUniqueId());
    }

    public void setLastMessage(String name) {
        this.lastMessage = name;
    }

    public boolean canChangeSkin() {
        return getLastChangeSkin() + TimeUnit.SECONDS.toMillis(40) < System.currentTimeMillis();
    }

    public void injectPermissions(Player player) {
        if (playerAttachment == null) playerAttachment = new PlayerAttachment(player, BukkitMain.getInstance());

        playerAttachment.removePermissions(playerAttachment.getPermissions());
        playerAttachment.addPermissions(getGroup().getPermissions());

        val permissions = getData(DataType.PERMISSIONS).getList();
        playerAttachment.addPermissions(permissions);
    }

    public void validateGroups() {
        Long groupTime = getLong(DataType.GROUP_TIME);

        if (groupTime != 0L) {
            if (System.currentTimeMillis() > groupTime) {
                getData(DataType.GROUP_TIME).setValue(0L);
                getData(DataType.GROUP).setValue("Membro");
                getData(DataType.GROUP_CHANGED_BY).setValue("Console");

                Groups groupExpired = getGroup();

                if (getString(DataType.FAKE).isEmpty()) {
                    setActualTag(getGroup().getTag());
                    TagManager.setTag(getPlayer(), getGroup());
                } else {
                    setActualTag(Groups.MEMBRO.getTag());
                    TagManager.setTag(getPlayer(), Groups.MEMBRO);
                }

                Player player = getPlayer();
                player.sendMessage(String.format(BukkitMessages.SEU_TEMPO_DE_GRUPO_EXPIROU, groupExpired.getColor() + groupExpired.getName()));

                injectPermissions(player);

                BukkitMain.runAsync(() -> getDataHandler().saveCategory(DataCategory.ACCOUNT));
            }
        }
    }
}