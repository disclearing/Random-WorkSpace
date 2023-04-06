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
import com.br.guilhermematthew.nowly.hardcoregames.listeners.SpectatorListener;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.Gamer;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;
import com.br.guilhermematthew.nowly.hardcoregames.manager.kit.KitManager;
import com.br.guilhermematthew.nowly.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.enums.GameStages;
import com.br.guilhermematthew.nowly.hardcoregames.listeners.InvincibilityListener;

public class NormalType extends GameType {

	@Override
	public void initialize() {
		registerListeners();
		
		HardcoreGamesMain.console("GameType (NormalType) has been loaded.");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void start() {
		PluginManager pluginManager = Bukkit.getServer().getPluginManager();

		pluginManager.callEvent(new GameStageChangeEvent(GameStages.WAITING, GameStages.INVINCIBILITY));

		HardcoreGamesMain.getGameManager().setStage(GameStages.INVINCIBILITY);
		HardcoreGamesMain.getTimerManager().updateTime(120);
		
		pluginManager.registerEvents(new InvincibilityListener(), HardcoreGamesMain.getInstance());

		List<Kit> kits = KitManager.getAllKitsAvailables();
		
		ItemStack compass = new ItemBuilder().type(Material.COMPASS).name("§6Bússola").build();
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			 Gamer gamer = GamerManager.getGamer(player.getUniqueId());
			 
			 if (gamer == null) continue;
			  
			 player.getPlayer().setItemOnCursor(new ItemStack(0));
			 
			 if (player.getOpenInventory() instanceof PlayerInventory) {
				 player.closeInventory();
			 }
			 
			 PlayerInventory playerInventory = player.getInventory();
			 
			 playerInventory.clear();
			 playerInventory.setArmorContents(null);
			 
			 for (PotionEffect pe : player.getActivePotionEffects()) {
		          player.removePotionEffect(pe.getType());
			 }
		        
		     if (player.getGameMode() != GameMode.SURVIVAL) {
		    	 player.setGameMode(GameMode.SURVIVAL);
		     }
		     
		     player.setAllowFlight(false);
		     player.setFlying(false);
			 
		     KitManager.give(player, gamer.getKit1(), true);
		     KitManager.give(player, gamer.getKit2(), false);
		     
			 playerInventory.addItem(compass);
			 
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

		kits.clear();
	}
	
	@Override
	public void checkWin() {
		if (isEnd() || isPreGame()) return;
		
		if (Bukkit.getOnlinePlayers().size() == 0) {
			callEnd();
			return;
		}
		
		List<Player> aliveGamers = GamerManager.getAliveGamers();
		
		if (aliveGamers.size() == 0) {
			callEnd();
			return;
		}
		
		if (aliveGamers.size() != 1) return;
		
		Player winner = aliveGamers.get(0);
		
		if (winner == null) {
			callEnd();
			return;
		}
		
		EndListener.winner = winner;
		
		callEnd();
		
		BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(winner.getUniqueId());
		
		final int xp = 100, coins = 200;
		
		winner.sendMessage(BukkitMessages.KILL_MESSAGE_XP.replace("%quantia%", "100"));
		winner.sendMessage(BukkitMessages.KILL_MESSAGE_COINS.replace("%quantia%", "200"));
		
		bukkitPlayer.addXP(xp);
		bukkitPlayer.add(DataType.COINS, coins);
		bukkitPlayer.add(DataType.HG_WINS);
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