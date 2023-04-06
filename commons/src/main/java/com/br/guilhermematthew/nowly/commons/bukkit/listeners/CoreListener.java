package com.br.guilhermematthew.nowly.commons.bukkit.listeners;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitSettings;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.fake.FakeAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.api.player.PlayerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.title.TitleAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.vanish.VanishAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerChangeGroupEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerQueueEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerUpdateTabEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.server.ServerLoadedEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.server.ServerStatusUpdateEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.queue.PlayerBukkitQueue;
import com.br.guilhermematthew.nowly.commons.bukkit.queue.QueueType;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.tag.TagManager;
import com.br.guilhermematthew.nowly.commons.bukkit.worldedit.WorldEditManager;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.profile.GamingProfile;
import com.br.guilhermematthew.nowly.commons.custompackets.PacketType;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketCustomAction;
import com.br.guilhermematthew.servercommunication.client.Client;
import com.google.gson.JsonElement;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.Dye;

import java.util.Map.Entry;

@SuppressWarnings("deprecation")
public class CoreListener implements Listener {

    private static ItemStack lapis;
    int MINUTES = 0;

    public static void init() {
        ItemStack sopa = new ItemBuilder().material(Material.MUSHROOM_SOUP).build();
        ShapelessRecipe cocoa = new ShapelessRecipe(sopa);
        ShapelessRecipe cactus = new ShapelessRecipe(sopa);

        cactus.addIngredient(Material.BOWL);
        cactus.addIngredient(1, Material.CACTUS);

        cocoa.addIngredient(Material.BOWL);
        cocoa.addIngredient(Material.INK_SACK, 3);

        Bukkit.addRecipe(cocoa);
        Bukkit.addRecipe(cactus);

        Dye d = new Dye();
        d.setColor(DyeColor.BLUE);
        lapis = d.toItemStack();
        lapis.setAmount(3);
    }

    public static boolean handleSoup(Player player) {
        boolean tomou = false;

        if (player.getHealth() < player.getMaxHealth()) {
            double life = player.getHealth();
            player.setHealth(Math.min(life + 7D, 20.0D));
            tomou = true;
        } else if (player.getFoodLevel() < 20) {
            player.setFoodLevel(player.getFoodLevel() + 7);
            tomou = true;
        }

        if (tomou) {
            player.setItemInHand(new ItemStack(Material.BOWL));
            player.updateInventory();
        }

        return tomou;
    }

    @EventHandler
    public void openInventoryEvent(InventoryOpenEvent e) {
        if (e.getInventory() instanceof EnchantingInventory) {
            e.getInventory().setItem(1, lapis);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Bukkit.getScheduler().runTaskLater(BukkitMain.getInstance(), () -> {
            e.getEntity().spigot().respawn();
        }, 6L);
    }

    @EventHandler
    public void closeInventoryEvent(InventoryCloseEvent e) {
        if (e.getInventory() instanceof EnchantingInventory) {
            e.getInventory().setItem(1, null);
        }
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent e) {
        if (e.getClickedInventory() instanceof EnchantingInventory) {
            if (e.getSlot() == 1) e.setCancelled(true);
        }
    }

    @EventHandler
    public void enchantItemEvent(EnchantItemEvent event) {
        event.getInventory().setItem(1, lapis);
    }

    @EventHandler
    public void onGroupChange(PlayerChangeGroupEvent event) {
        Player player = event.getPlayer();
        Groups group = event.getGroup();

        BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());

        if (bukkitPlayer.getString(DataType.FAKE).equalsIgnoreCase("")) {
            bukkitPlayer.updateTag(player, bukkitPlayer.getGroup().getTag(), true);
        } else {
            TagManager.setTag(player, bukkitPlayer.getActualTag(), bukkitPlayer);
        }

        //TitleAPI.sendTitle(player, 1, 2, 3, "§6§lGRUPO",
         //       "§fVocê recebeu o grupo " + group.getColor() + group.getName().toUpperCase());
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();

        VanishAPI.updateInvisibles(player);

        BukkitPlayer profile = BukkitMain.getBukkitPlayer(player.getUniqueId());

        String fake = profile.getString(DataType.FAKE);
        Groups actualGroup = profile.getGroup();
        boolean save = false;

        if (!TagManager.hasPermission(player, actualGroup)) {
            profile.set(DataType.GROUP, "Membro");
            actualGroup = Groups.MEMBRO;
            save = true;
        }

        if(actualGroup != Groups.MEMBRO) {
            val tag = profile.getString(DataType.TAG);
            if(tag != null) {
                val group = Groups.getGroup(tag);

                if(group != null && TagManager.hasPermission(player, group)) actualGroup = group;
            }
        }

        profile.setActualTag(actualGroup.getTag());

        boolean resetFake = false;

        if (!fake.equalsIgnoreCase("")) {
            if (profile.getGroup().getLevel() > Groups.MEMBRO.getLevel()) {
                if (Bukkit.getPlayer(fake) == null) {
                    FakeAPI.changePlayerName(player, fake, true);

                    player.sendMessage(BukkitMessages.VOCE_ENTROU_NO_SERVIDOR_COM_O_FAKE);
                } else {
                    resetFake = true;
                }
            } else {
                resetFake = true;
            }
        }

        if (resetFake) {
            profile.set(DataType.FAKE, "");

            save = true;
        }

        TagManager.setTag(player, actualGroup);

        if (save) {
            BukkitMain.runAsync(() -> profile.getDataHandler().saveCategory(DataCategory.ACCOUNT));
        }

        PlayerAPI.updateTab(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void injectPermissions(PlayerLoginEvent event) {
        if (!CommonsGeneral.getProfileManager().containsProfile(event.getPlayer().getUniqueId())) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, BukkitMessages.THE_SERVER_NOT_RECEIVED_PLAYER_DATA);
            return;
        }

        BukkitMain.getBukkitPlayer(event.getPlayer().getUniqueId()).injectPermissions(event.getPlayer());
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }

        Player player = event.getPlayer();

        GamingProfile profile = CommonsGeneral.getProfileManager().getGamingProfile(player.getUniqueId());

        /*
        if (Bukkit.hasWhitelist()) {
            boolean kick = true;

            if (profile.getGroup().getLevel() > Groups.PRIME.getLevel()) {
                kick = false;
            } else if (player.hasPermission(CommonsConst.PERMISSION_PREFIX + ".whitelist")) {
                kick = false;
            }

            if (kick) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, BukkitMessages.THE_SERVER_WITH_WHITELIST);
            }
        }*/

        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            CommonsGeneral.getProfileManager().removeGamingProfile(player.getUniqueId());
            return;
        }

        if (profile.getGroup().getLevel() >= Groups.ADMIN.getLevel()) {
            if (!player.isOp()) {
                player.setOp(true);
            }
        } else {
            if (player.isOp()) {
                player.setOp(false);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (player.isOp()) {
            player.setOp(false);
        }

        VanishAPI.remove(player);
        WorldEditManager.checkAndRemove(player);
    }

    @EventHandler
    public void onLoad(ServerLoadedEvent event) {
        BukkitMain.console("--------------------------------------------------");
        BukkitMain.console("Servidor carregado no tipo: " + BukkitMain.getServerType().getName());
        BukkitMain.console("Servidor com ID #" + BukkitMain.getServerID());
        BukkitMain.console("Servidor totalmente carregado e estabilizado.");
        BukkitMain.console("---------------------------------------------------");

        Client.getInstance().getClientConnection().sendPacket(new CPacketCustomAction(BukkitMain.getServerType(), BukkitMain.getServerID())
                .type(PacketType.BUKKIT_SEND_INFO).field("bukkit-server-turn-on"));

        BukkitMain.setLoaded(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onServerUpdate(ServerStatusUpdateEvent event) {
        CPacketCustomAction PACKET = new CPacketCustomAction(BukkitMain.getServerType(), BukkitMain.getServerID())
                .type(PacketType.BUKKIT_SEND_SERVER_DATA);

        for (Entry<String, JsonElement> entry : event.getJson().entrySet()) {
            PACKET.getJson().add(entry.getKey(), entry.getValue());
        }

        Client.getInstance().getClientConnection().sendPacket(PACKET);
    }

    @EventHandler
    public void onPlayerQueue(PlayerQueueEvent event) {
        Player player = event.getPlayer();

        if (event.getQueueType() == QueueType.CONNECT) {
            BukkitPlayer bukkitPlayer = (BukkitPlayer) CommonsGeneral.getProfileManager()
                    .getGamingProfile(player.getUniqueId());
            bukkitPlayer.getDataHandler()
                    .sendCategoryToBungeecord(bukkitPlayer.getDataHandler().getListDataCategorysLoadeds());

            BukkitServerAPI.redirectPlayer(player, event.getResponse());
        } else if (event.getQueueType() == QueueType.KICK) {
            BukkitMain.runSync(() -> player.kickPlayer("§cVocê foi expulso do servidor."));
        } else if (event.getQueueType() == QueueType.SAVE) {
            BukkitMain.runAsync(() -> {
                BukkitPlayer bukkitPlayer = (BukkitPlayer) CommonsGeneral.getProfileManager()
                        .getGamingProfile(player.getUniqueId());
                bukkitPlayer.getDataHandler().saveCategory(DataCategory.ACCOUNT);
            });
        } else if (event.getQueueType() == QueueType.CHECK_TIME) {
            BukkitPlayer bukkitPlayer = (BukkitPlayer) CommonsGeneral.getProfileManager().getGamingProfile(player.getUniqueId());
            bukkitPlayer.validateGroups();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerUpdateTabEvent(PlayerUpdateTabEvent event) {
        TitleAPI.setHeaderAndFooter(event.getPlayer(), event.getHeader(), event.getFooter());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent event) {
        if (!BukkitSettings.DANO_OPTION) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamageByEntityLow(EntityDamageByEntityEvent event) {
        if (!BukkitSettings.PVP_OPTION) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEntityEvent event) {
        if ((event.getPlayer() != null) && (event.getRightClicked() instanceof Player)) {

            Player player = event.getPlayer(), clicked = (Player) event.getRightClicked();

            if ((player.getItemInHand().getType().equals(Material.AIR)) && (VanishAPI.inAdmin(player))) {
                player.performCommand("invsee " + clicked.getName());
            }
        }
    }

    @EventHandler
    public void onUpdate(BukkitUpdateEvent event) {
        if (event.getType() != BukkitUpdateEvent.UpdateType.MINUTO) return;

        MINUTES++;

        if (MINUTES % 2 == 0) {
            PlayerBukkitQueue queue = new PlayerBukkitQueue(30, true, QueueType.CHECK_TIME);

            for (Player onlines : Bukkit.getOnlinePlayers()) {
                queue.addToQueue(onlines);
            }

            queue.start();
        }
    }

    @EventHandler
    public void worldEditListener(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            return;
        }
        if (event.getClickedBlock() != null) {
            Player player = event.getPlayer();
            if (player.getItemInHand() != null && player.getItemInHand().getType() == Material.WOOD_AXE) {
                if (player.getItemInHand().hasItemMeta()) {
                    if (player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("§e§lWORLDEDIT")) {
                        if (event.getAction().name().contains("LEFT")) {
                            event.setCancelled(true);
                            WorldEditManager.setPos1(player, event.getClickedBlock().getLocation());
                        } else {
                            WorldEditManager.setPos2(player, event.getClickedBlock().getLocation());
                        }
                    }
                }
            }
        }
    }
}