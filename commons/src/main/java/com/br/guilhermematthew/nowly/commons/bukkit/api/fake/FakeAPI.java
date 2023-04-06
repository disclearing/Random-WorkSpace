package com.br.guilhermematthew.nowly.commons.bukkit.api.fake;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.val;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class FakeAPI {

    public static void changePlayerName(Player player, String name) {
        changePlayerName(player, name, true);
    }

    public static void changePlayerName(Player player, String name, boolean respawn) {
        Collection<? extends Player> players = player.getWorld().getPlayers();
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        GameProfile playerProfile = entityPlayer.getProfile();

        if (respawn) {
            removeFromTab(player, players);
        }

        try {
            Field field = playerProfile.getClass().getDeclaredField("name");
            field.setAccessible(true);
            field.set(playerProfile, name);
            field.setAccessible(false);
            entityPlayer.getClass().getDeclaredField("displayName").set(entityPlayer, name);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (respawn) respawnPlayer(player);
    }

    public static void removePlayerSkin(Player player) {
        removePlayerSkin(player, true);
    }

    public static void removePlayerSkin(Player player, boolean respawn) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        GameProfile playerProfile = entityPlayer.getProfile();
        playerProfile.getProperties().removeAll("textures");

        if (respawn) {
            respawnPlayer(player);
        }
    }

    public static void changePlayerSkin(Player player, String skinValue, String skinSignature) {
        changePlayerSkin(player, skinValue, skinSignature, true);
    }

    public static void changePlayerSkin(Player player, String skinValue, String skinSignature, boolean respawn) {
        BukkitPlayer bPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
        bPlayer.setLastSkin(new Property("textures", skinValue, skinSignature));

        if (respawn) respawnPlayer(player);
    }

    public static void removeFromTab(Player player, Collection<? extends Player> players) {
        PacketPlayOutPlayerInfo removePlayerInfo =
                new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) player).getHandle());

        for (Player online : players) {
            if (online.canSee(player)) {
                ((CraftPlayer) online).getHandle().playerConnection.sendPacket(removePlayerInfo);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static void respawnPlayer(Player p) {
        try {
            if (!p.isOnline()) return;

            CraftPlayer cp = (CraftPlayer) p;
            EntityPlayer ep = cp.getHandle();
            int entId = ep.getId();
            Location l = p.getLocation();

            val textures = BukkitMain.getBukkitPlayer(p.getUniqueId()).getLastSkin();
            if (textures != null) {
                ep.getProfile().getProperties().removeAll(textures.getName());
                ep.getProfile().getProperties().put(textures.getName(), textures);
            }

            val actualPing = ep.ping;

            PacketPlayOutPlayerInfo removeInfo =
                    new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, ep);

            PacketPlayOutEntityDestroy removeEntity =
                    new PacketPlayOutEntityDestroy(entId);

            PacketPlayOutNamedEntitySpawn addNamed =
                    new PacketPlayOutNamedEntitySpawn(ep);

            PacketPlayOutPlayerInfo addInfo =
                    new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, ep);

            PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(((WorldServer) ep.getWorld()).dimension,
                    ep.world.getDifficulty(), ep.getWorld().worldData.getType(), EnumGamemode.getById(p.getGameMode().getValue()));

            PacketPlayOutPosition pos =
                    new PacketPlayOutPosition(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), new HashSet<>());

            PacketPlayOutEntityEquipment itemhand =
                    new PacketPlayOutEntityEquipment(entId, 0, CraftItemStack.asNMSCopy(p.getItemInHand()));

            PacketPlayOutEntityEquipment helmet =
                    new PacketPlayOutEntityEquipment(entId, 4, CraftItemStack.asNMSCopy(p.getInventory().getHelmet()));

            PacketPlayOutEntityEquipment chestplate =
                    new PacketPlayOutEntityEquipment(entId, 3, CraftItemStack.asNMSCopy(p.getInventory().getChestplate()));

            PacketPlayOutEntityEquipment leggings =
                    new PacketPlayOutEntityEquipment(entId, 2, CraftItemStack.asNMSCopy(p.getInventory().getLeggings()));

            PacketPlayOutEntityEquipment boots =
                    new PacketPlayOutEntityEquipment(entId, 1, CraftItemStack.asNMSCopy(p.getInventory().getBoots()));

            PacketPlayOutHeldItemSlot slot =
                    new PacketPlayOutHeldItemSlot(p.getInventory().getHeldItemSlot());

            List<Player> toUpdate = new ArrayList<>();

            for (Player pOnline : Bukkit.getServer().getOnlinePlayers()) {
                CraftPlayer craftOnline = (CraftPlayer) pOnline;
                PlayerConnection con = craftOnline.getHandle().playerConnection;

                if (pOnline.equals(p)) {
                    con.sendPacket(removeInfo);
                    con.sendPacket(addInfo);
                    con.sendPacket(respawn);
                    con.sendPacket(pos);
                    con.sendPacket(slot);

                    craftOnline.updateScaledHealth();
                    craftOnline.getHandle().triggerHealthUpdate();
                    craftOnline.updateInventory();

                    if (pOnline.isOp()) {
                        pOnline.setOp(false);
                        pOnline.setOp(true);
                    }
                } else if ((pOnline.canSee(p)) && (pOnline.getWorld().equals(p.getWorld()))) {
                    con.sendPacket(removeEntity);
                    con.sendPacket(removeInfo);
                    con.sendPacket(addInfo);
                    con.sendPacket(addNamed);
                    con.sendPacket(itemhand);
                    con.sendPacket(helmet);
                    con.sendPacket(chestplate);
                    con.sendPacket(leggings);
                    con.sendPacket(boots);

                    pOnline.hidePlayer(p);
                    toUpdate.add(pOnline);
                } else {
                    con.sendPacket(removeInfo);
                    con.sendPacket(addInfo);
                }
            }

            BukkitMain.runLater(() -> {
                if (!p.isOnline()) {
                    return;
                }

                toUpdate.forEach(players -> players.showPlayer(p));
            }, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addToTab(Player player, Collection<? extends Player> players) {
        PacketPlayOutPlayerInfo addPlayerInfo =
                new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) player).getHandle());

        PacketPlayOutPlayerInfo updatePlayerInfo =
                new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.UPDATE_DISPLAY_NAME, ((CraftPlayer) player).getHandle());

        for (Player online : players) {
            if (online.canSee(player)) {
                ((CraftPlayer) online).getHandle().playerConnection.sendPacket(addPlayerInfo);
                ((CraftPlayer) online).getHandle().playerConnection.sendPacket(updatePlayerInfo);
            }
        }
    }
}