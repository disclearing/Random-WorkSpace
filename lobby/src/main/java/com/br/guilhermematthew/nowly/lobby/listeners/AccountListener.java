package com.br.guilhermematthew.nowly.lobby.listeners;

import java.util.UUID;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ActionItemStack;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ActionItemStack.InteractHandler;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.api.title.TitleAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.vanish.VanishAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerChangeVisibilityEvent;
import com.br.guilhermematthew.nowly.commons.common.data.DataHandler;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import com.br.guilhermematthew.nowly.lobby.LobbyMain;
import com.br.guilhermematthew.nowly.lobby.commands.ServerCommand;
import com.br.guilhermematthew.nowly.lobby.common.inventory.InventoryCommon;

public class AccountListener implements Listener {

	private final String VISIBILITY_DELAY_TAG = "visibility.delay.time";

	private boolean registred = false;

	private void register() {
		if (registred)
			return;
		registred = true;
		ActionItemStack.register("§fJogadores: §aON", visibilityHandler);
		ActionItemStack.register("§aSelecionar jogo", gamesModeHandler);
		ActionItemStack.register("§aMeu perfil", profileHandler);
	}

	@EventHandler
	public void onAsyncLogin(AsyncPlayerPreLoginEvent e) {
		if(BukkitMain.getServerType() == ServerType.LOBBY) return;

		val category = BukkitMain.getServerType() == ServerType.LOBBY_HARDCOREGAMES ?
				DataCategory.HARDCORE_GAMES : DataCategory.KITPVP;

		val bp = BukkitMain.getBukkitPlayer(e.getUniqueId());

		try {
			bp.getDataHandler().load(category);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		register();

		Player player = event.getPlayer();

		ItemBuilder itemBuilder = new ItemBuilder();

		addDelay(player);

		if (player.getGameMode() != GameMode.ADVENTURE) {
			player.setGameMode(GameMode.ADVENTURE);
		}

		player.setMaxHealth(1.7D);
		player.setHealth(1.7);

		BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());

		boolean playerVisibility = bukkitPlayer.getBoolean(DataType.PLAYERS_VISIBILITY);

		PlayerInventory playerInventory = player.getInventory();

		playerInventory.clear();
		playerInventory.setArmorContents(null);

		playerInventory.setItem(0, itemBuilder.type(Material.COMPASS).name("§aSelecionar jogo").build());
		playerInventory.setItem(1,
				itemBuilder.type(Material.SKULL_ITEM).skin(bukkitPlayer.getNick()).name("§aMeu perfil").build());

		playerInventory.setItem(7, itemBuilder.type(Material.INK_SACK).durability(playerVisibility ? 10 : 8)
				.name("§fJogadores: §aON").build());
		playerInventory.setItem(8, itemBuilder.type(Material.NETHER_STAR).name("§aSelecionar Lobby").build());

		player.updateInventory();

		if (BukkitMain.getServerType() == ServerType.LOBBY) {
			TitleAPI.sendTitle(player, "§2§lLEAGUE", "§eSeja bem-vindo(a)", 0, 0, 2);
		}

		Groups playerGroup = bukkitPlayer.getGroup();

		boolean announceJoin = !bukkitPlayer.containsFake() && !VanishAPI.inAdmin(player) && playerGroup.getLevel() > Groups.MEMBRO.getLevel()
				&& bukkitPlayer.getBoolean(DataType.ANNOUNCEMENT_JOIN);

		val playerTag = bukkitPlayer.getActualTag() != null ? bukkitPlayer.getActualTag() : playerGroup.getTag();
		Groups group = bukkitPlayer.getGroup();
		String joinMessage = group.getColor() + "§l" + group.getTag().getPrefix() + " " + group.getColor() +
				player.getName() + " §6entrou neste lobby!";

		for (Player onlines : Bukkit.getOnlinePlayers()) {
			if (announceJoin) {
				onlines.sendMessage(joinMessage);
			}
			if (onlines.getUniqueId() != player.getUniqueId()) {

				if (!playerVisibility) {
					player.hidePlayer(onlines);
				}

				if (!BukkitMain.getBukkitPlayer(onlines.getUniqueId()).getBoolean(DataType.PLAYERS_VISIBILITY)) {
					onlines.hidePlayer(player);
				}
			}
		}

		if (playerGroup.getLevel() > Groups.MEMBRO.getLevel()) {
			player.teleport(LobbyMain.getSpawn().clone().add(0, 0.5, 0));

			player.setAllowFlight(true);
			player.setFlying(true);
		} else {
			player.teleport(LobbyMain.getSpawn());
		}

		LobbyMain.getScoreBoardCommon().createScoreboard(player);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		UUID uniqueId = event.getPlayer().getUniqueId();

		ServerCommand.autorizados.remove(uniqueId);
	}

	@EventHandler
	public void onChangeVisibility(PlayerChangeVisibilityEvent event) {
		Player player = event.getPlayer();

		if (event.isVisibility()) {

			for (Player ons : Bukkit.getOnlinePlayers()) {
				if (!VanishAPI.isInvisible(ons) && !VanishAPI.inAdmin(ons)) {
					player.showPlayer(ons);
				}
			}

			VanishAPI.updateInvisibles(player);

			player.getInventory().setItem(player.getInventory().first(Material.INK_SACK), new ItemBuilder()
					.type(Material.INK_SACK).durability(10).name("§fJogadores: §aON").build());
		} else {
			for (Player ons : Bukkit.getOnlinePlayers()) {
				player.hidePlayer(ons);
			}

			VanishAPI.updateInvisibles(player);

			player.getInventory().setItem(player.getInventory().first(Material.INK_SACK), new ItemBuilder()
					.type(Material.INK_SACK).durability(8).name("§fJogadores: §cOFF").build());
		}
	}

	private final InteractHandler profileHandler = (player, itemStack, itemAction, clickedBlock) -> {
		if (!itemAction.name().contains("RIGHT"))
			return true;
		player.chat("/acc");
		return true;
	};

	private final InteractHandler gamesModeHandler = (player, itemStack, itemAction, clickedBlock) -> {
		if (!itemAction.name().contains("RIGHT"))
			return true;

		if (BukkitMain.getServerType() == ServerType.LOBBY_PVP) {
			InventoryCommon.getGamesInventory().open(player);
		} else {
			InventoryCommon.getGamesInventory().open(player);
		}
		return true;
	};

	private final InteractHandler visibilityHandler = (player, itemStack, itemAction, clickedBlock) -> {
		if (!itemAction.name().contains("RIGHT")) return true;
		if (hasDelay(player)) return true;

		addDelay(player);

		if (!itemAction.name().contains("RIGHT")) return true;

		DataHandler dataHandler = BukkitMain.getBukkitPlayer(player.getUniqueId()).getDataHandler();

		if (dataHandler.getBoolean(DataType.PLAYERS_VISIBILITY)) {
			dataHandler.getData(DataType.PLAYERS_VISIBILITY).setValue(false);

			LobbyMain.runAsync(() -> dataHandler.saveCategory(DataCategory.PREFERENCES));

			player.sendMessage(BukkitMessages.VOCE_ATIVOU_A_VISIBILIDADE_DOS_JOGADORES);

			Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeVisibilityEvent(player, false));
		} else {
			dataHandler.getData(DataType.PLAYERS_VISIBILITY).setValue(true);
			LobbyMain.runAsync(() -> dataHandler.saveCategory(DataCategory.PREFERENCES));

			player.sendMessage(BukkitMessages.VOCE_ATIVOU_A_VISIBILIDADE_DOS_JOGADORES);
			player.getInventory().setItem(player.getInventory().first(Material.INK_SACK), new ItemBuilder()
					.type(Material.INK_SACK).durability(10).name("§fJogadores: §aON").build());
			Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeVisibilityEvent(player, true));
		}
		return true;
	};

	private void addDelay(Player player) {
		player.setMetadata(VISIBILITY_DELAY_TAG,
				new FixedMetadataValue(LobbyMain.getInstance(), System.currentTimeMillis() + 4000L));
	}

	private boolean hasDelay(Player player) {
		if (!player.hasMetadata(VISIBILITY_DELAY_TAG))
			return false;
		return player.getMetadata(VISIBILITY_DELAY_TAG).get(0).asLong() > System.currentTimeMillis();
	}
}