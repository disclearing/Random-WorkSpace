package com.br.guilhermematthew.nowly.hardcoregames.game.types;

import java.util.List;
import java.util.UUID;

import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.StringUtils;
import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import com.br.guilhermematthew.nowly.hardcoregames.base.GameType;
import com.br.guilhermematthew.nowly.hardcoregames.events.game.GameStageChangeEvent;
import com.br.guilhermematthew.nowly.hardcoregames.events.game.GameStartedEvent;
import com.br.guilhermematthew.nowly.hardcoregames.listeners.EndListener;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.Gamer;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;
import com.br.guilhermematthew.nowly.hardcoregames.manager.kit.KitManager;
import com.br.guilhermematthew.nowly.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.enums.GameStages;
import com.br.guilhermematthew.nowly.hardcoregames.listeners.InvincibilityListener;
import com.br.guilhermematthew.nowly.hardcoregames.listeners.SpectatorListener;

public class AutomaticEventType extends GameType {

	@Override
	public void initialize() {
		registerListeners();

		KitManager.disableKits("Achilles", "Barbarian", "Boxer", "Checkpoint", "Demoman", "Fisherman", "Launcher",
				"Monk", "Phantom", "Stomper", "Tank", "Thor", "Urgal", "Viking", "Madman", "Digger", "HotPotato");

		HardcoreGamesMain.console("GameType (AutomaticEventType) has been loaded.");

		new BukkitRunnable() {

			int seconds = 400;

			@Override
			public void run() {
				seconds--;

				if (!isPreGame()) {
					cancel();
					return;
				}

				if (seconds == 0) {
					cancel();

					if (GamerManager.getAliveGamers().size() < 20) {
						HardcoreGamesMain.console("AutomaticEventType fechando por conta do Timer (Poucos jogadores).");
						callEnd();
					}
				}
			}
		}.runTaskTimer(HardcoreGamesMain.getInstance(), 20, 20);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void start() {
		HardcoreGamesMain.getTimerManager().updateTime(60);
		HardcoreGamesMain.getGameManager().setStage(GameStages.INVINCIBILITY);

		PluginManager pluginManager = Bukkit.getServer().getPluginManager();

		pluginManager.callEvent(new GameStageChangeEvent(GameStages.WAITING, GameStages.INVINCIBILITY));
		pluginManager.registerEvents(new InvincibilityListener(), HardcoreGamesMain.getInstance());

		List<Kit> kits = KitManager.getAllKitsAvailables();

		ItemBuilder itemBuilder = new ItemBuilder();

		ItemStack capacete = itemBuilder.type(Material.IRON_HELMET).name("§fCapacete de Ferro").build(),
				peitoral = itemBuilder.type(Material.IRON_CHESTPLATE).name("§fPeitoral de Ferro").build(),
				calça = itemBuilder.type(Material.IRON_LEGGINGS).name("§fCalça de Ferro").build(),
				bota = itemBuilder.type(Material.IRON_BOOTS).name("§fBota de Ferro").build();

		for (Player player : Bukkit.getOnlinePlayers()) {
			Gamer gamer = GamerManager.getGamer(player.getUniqueId());

			if (gamer == null)
				continue;

			player.getPlayer().setItemOnCursor(new ItemStack(0));

			if (player.getOpenInventory() instanceof PlayerInventory)
				player.closeInventory();

			PlayerInventory playerInventory = player.getInventory();

			playerInventory.clear();
			playerInventory.setArmorContents(null);

			playerInventory.setHelmet(capacete);
			playerInventory.setChestplate(peitoral);
			playerInventory.setLeggings(calça);
			playerInventory.setBoots(bota);

			for (PotionEffect pe : player.getActivePotionEffects()) {
				player.removePotionEffect(pe.getType());
			}

			if (player.getGameMode() != GameMode.SURVIVAL) player.setGameMode(GameMode.SURVIVAL);

			player.setAllowFlight(false);
			player.setFlying(false);

			playerInventory.setItem(0, itemBuilder.type(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL).build());
			playerInventory.setItem(3, itemBuilder.type(Material.LAVA_BUCKET).build());
			playerInventory.setItem(4, itemBuilder.type(Material.WOOD).amount(64).build());
			playerInventory.setItem(7, itemBuilder.type(Material.WATER_BUCKET).build());
			playerInventory.setItem(8, itemBuilder.type(Material.COMPASS).name("§6Bússola").build());

			KitManager.give(player, gamer.getKit1(), true);
			KitManager.give(player, gamer.getKit2(), false);

			playerInventory.addItem(itemBuilder.type(Material.LAVA_BUCKET).build());
			playerInventory.addItem(itemBuilder.type(Material.LAVA_BUCKET).build());
			playerInventory.addItem(itemBuilder.type(Material.DIAMOND_SWORD).build());

			playerInventory.setItem(18, capacete);
			playerInventory.setItem(19, peitoral);
			playerInventory.setItem(20, calça);
			playerInventory.setItem(21, bota);
			playerInventory.setItem(22, itemBuilder.type(Material.BOWL).amount(64).build());
			playerInventory.setItem(23, itemBuilder.type(Material.INK_SACK).durability(3).amount(64).build());
			playerInventory.setItem(24, itemBuilder.type(Material.INK_SACK).durability(3).amount(64).build());
			playerInventory.setItem(25, itemBuilder.type(Material.LOG).amount(32).build());
			playerInventory.setItem(26, itemBuilder.type(Material.STONE_AXE).build());

			playerInventory.setItem(9, capacete);
			playerInventory.setItem(10, peitoral);
			playerInventory.setItem(11, calça);
			playerInventory.setItem(12, bota);
			playerInventory.setItem(13, itemBuilder.type(Material.BOWL).amount(64).build());
			playerInventory.setItem(14, itemBuilder.type(Material.INK_SACK).durability(3).amount(64).build());
			playerInventory.setItem(15, itemBuilder.type(Material.INK_SACK).durability(3).amount(64).build());
			playerInventory.setItem(16, itemBuilder.type(Material.INK_SACK).durability(3).amount(64).build());
			playerInventory.setItem(17, itemBuilder.type(Material.STONE_PICKAXE).build());

			HardcoreGamesScoreboard.createScoreboard(gamer);
		}

		Bukkit.broadcastMessage(StringUtils.THE_GAME_HAS_STARTED);

		World world = Bukkit.getWorlds().get(0);
		world.setTime(0);
		world.setStorm(false);
		world.setThundering(false);

		world.playSound(world.getSpawnLocation(), Sound.AMBIENCE_THUNDER, 4.0F, 4.0F);

		pluginManager.registerEvents(new SpectatorListener(), HardcoreGamesMain.getInstance());
		pluginManager.callEvent(new GameStartedEvent());

		// recyclable
		kits.clear();
	}

	@Override
	public void checkWin() {
		if (isEnd() || isPreGame())
			return;

		if (Bukkit.getOnlinePlayers().size() == 0) {
			callEnd();
			return;
		}

		List<Player> aliveGamers = GamerManager.getAliveGamers();

		if (aliveGamers.size() == 0) {
			callEnd();
			return;
		}

		if (aliveGamers.size() != 1)
			return;

		Player winner = aliveGamers.get(0);
		if (winner == null) {
			callEnd();
			return;
		}

		EndListener.winner = winner;

		callEnd();

		BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(winner.getUniqueId());

		final int xp = 250, coins = 500;

		winner.sendMessage(BukkitMessages.KILL_MESSAGE_XP.replace("%quantia%", "250"));
		winner.sendMessage(BukkitMessages.KILL_MESSAGE_COINS.replace("%quantia%", "500"));

		bukkitPlayer.addXP(xp);
		bukkitPlayer.add(DataType.COINS, coins);
		bukkitPlayer.add(DataType.HG_EVENT_WINS);
		bukkitPlayer.getDataHandler().saveCategorys(DataCategory.ACCOUNT, DataCategory.HARDCORE_GAMES);

		aliveGamers.clear();
	}

	@Override
	public void registerListeners() {

	}

	@Override
	public void choiceWinner() {
		UUID ganhador = getMVP();

		for (Player player : GamerManager.getAliveGamers()) {
			if (player.getUniqueId() != ganhador) {
				setEspectador(player, true);
			}
		}

		HardcoreGamesMain.runLater(this::checkWin, 20);
	}
}