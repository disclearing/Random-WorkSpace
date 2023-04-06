package com.br.guilhermematthew.nowly.hardcoregames.listeners;

import java.util.Random;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.bossbar.BossBarAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.player.PlayerAPI;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.ServerType;
import com.br.guilhermematthew.nowly.commons.common.serverinfo.enums.GameStages;
import com.br.guilhermematthew.nowly.commons.common.utility.system.DateUtils;
import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.StringUtils;
import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesOptions;
import com.br.guilhermematthew.nowly.hardcoregames.ability.register.Gladiator;
import com.br.guilhermematthew.nowly.hardcoregames.ability.utility.GladiatorFight;
import com.br.guilhermematthew.nowly.hardcoregames.events.game.GameStageChangeEvent;
import com.br.guilhermematthew.nowly.hardcoregames.events.game.GameTimerEvent;
import com.br.guilhermematthew.nowly.hardcoregames.events.player.PlayerDamagePlayerEvent;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.Gamer;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;
import com.br.guilhermematthew.nowly.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import com.br.guilhermematthew.nowly.hardcoregames.manager.structures.StructuresManager;
import com.br.guilhermematthew.nowly.hardcoregames.manager.structures.types.MiniFeast;
import com.br.guilhermematthew.nowly.hardcoregames.utility.HardcoreGamesUtility;

public class GameListener implements Listener {

	private final Random random = new Random();

	@EventHandler
	public void onTimer(GameTimerEvent event) {
		if (event.getTime() > 5 && event.getTime() % 240 == 0) {
			if (HardcoreGamesOptions.MINIFEAST) {
				MiniFeast.create();
			}
		}

		if (event.getTime() == HardcoreGamesOptions.FEAST_SPAWN) {
			if (HardcoreGamesOptions.FEAST) StructuresManager.getFeast().createFeast(StructuresManager.getValidLocation(true));
		}

		if (event.getTime() == 35 * 60 || event.getTime() == 36 * 60 || event.getTime() == 37 * 60
				|| event.getTime() == 38 * 60 || event.getTime() == 39 * 60) {

			Bukkit.broadcastMessage(StringUtils.FINAL_ARENA_IN.replace("%tempo%",
					DateUtils.formatSeconds(((HardcoreGamesOptions.FINAL_BATTLE_SPAWN) - event.getTime()))));

		} else if (event.getTime() == HardcoreGamesOptions.FINAL_BATTLE_SPAWN) {
			HardcoreGamesOptions.MINIFEAST = false;
			StructuresManager.getFinalBattle().create();
		}

		if (event.getTime() == 50 * 60 || event.getTime() == 55 * 60 || event.getTime() == 57 * 60
				|| event.getTime() == 58 * 60 || event.getTime() == 59 * 60) {
			Bukkit.broadcastMessage(StringUtils.GAME_END_IN.replace("%tempo%",
					DateUtils.formatSeconds((HardcoreGamesOptions.MAX_TIME - event.getTime()))));
		} else if (event.getTime() == HardcoreGamesOptions.MAX_TIME) {
			HardcoreGamesMain.getGameManager().getGameType().choiceWinner();
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			if (GamerManager.getGamer(player.getUniqueId()).isPlaying()) {
				player.setSaturation(5f);
			} else {
				event.setCancelled(true);
			}

			player = null;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent event) {
		event.setDeathMessage(null);
		event.getDrops().clear();

		Player morreu = event.getEntity(), 
				matou = morreu.getKiller();

		Location loc = morreu.getLocation();

		if (Gladiator.inGlad(morreu)) {
			GladiatorFight glad = Gladiator.getGladiatorFight(morreu);

			loc = glad.getBackForPlayer(morreu);

			glad.cancelGlad();

			glad = null;
		}

		PlayerAPI.dropItems(morreu, loc);

		Gamer gamerDied = GamerManager.getGamer(morreu.getUniqueId());
		if (!availableToRespawn(morreu)) {
			gamerDied.setEliminado(true);
			gamerDied.setPlaying(false);
		}

		final int jogadoresRestantes = GamerManager.getAliveGamers().size();

		if (matou != null && matou instanceof Player) {
		   Gamer gamerKill = GamerManager.getGamer(matou.getUniqueId());
			gamerKill.addKill();
			
			Bukkit.broadcastMessage(StringUtils.PLAYER_DEATH_FOR_PLAYER.replace("%matou%", matou.getName())
					.replace("%matouKit%", gamerKill.getKits()).replace("%morreu%", morreu.getName())
					.replace("%morreuKit%", gamerDied.getKits()).replace("%restantes%", "" + jogadoresRestantes)
					.replace("%item%", getItemInHand(matou.getItemInHand().getType())));
			
			HardcoreGamesScoreboard.getScoreBoardCommon().updateKills(matou, gamerKill.getKills());

			gamerKill = null;
		} else {
			Bukkit.broadcastMessage(StringUtils.PLAYER_DEATH.replace("%morreu%", morreu.getName())
					.replace("%kit%", gamerDied.getKit1()).replace("%restantes%", "" + jogadoresRestantes)
					.replace("%causa%", getCausa(morreu.getLastDamageCause().getCause())));
		}

		handleStats(matou, morreu);

		gamerDied = null;
		morreu = null;
		matou = null;
		loc = null;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onEntitySpawn(CreatureSpawnEvent event) {
		if (event.getEntityType() == EntityType.GHAST || event.getEntityType() == EntityType.PIG_ZOMBIE) {
			event.setCancelled(true);
			return;
		}

		if (event.getSpawnReason() != SpawnReason.NATURAL)
			return;

		if (random.nextInt(5) > 2) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(HardcoreGamesUtility.getRandomLocation(160));
	}

	@EventHandler
	public void onAutoRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();

		if (availableToRespawn(player)) {
			HardcoreGamesMain.getGameManager().getGameType().setGamer(player);
		} else {
			HardcoreGamesMain.getGameManager().getGameType().setEspectador(player);

			BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());

			HardcoreGamesMain.runAsync(() -> {
				bukkitPlayer.getDataHandler().saveCategorys(DataCategory.ACCOUNT, DataCategory.HARDCORE_GAMES);
			});
			
			if (!HardcoreGamesUtility.availableToSpec(player)) {
				BukkitServerAPI.redirectPlayer(player, "LobbyHardcoreGames", true);
			}
		}

		player = null;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onHit(PlayerDamagePlayerEvent event) {
		val gamer = GamerManager.getGamer(event.getDamaged());

		if(HardcoreGamesOptions.DOUBLE_KIT) {
			BossBarAPI.send(event.getPlayer(),
					event.getDamaged().getName() + " - " + gamer.getKit1() + " - " + gamer.getKit2(), 3);
		} else {
			BossBarAPI.send(event.getPlayer(),
					event.getDamaged().getName() + " - " + gamer.getKit1(), 3);
		}
	}
	
	@EventHandler
	public void onEnderPearl(ProjectileLaunchEvent event) {
		if (event.getEntity().getShooter() instanceof Player && event.getEntity() instanceof EnderPearl) {
			int tempo = HardcoreGamesMain.getTimerManager().getTime().get();
			
			if (tempo > (39 * 60) + 35 && tempo < (40 * 60) + 5) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (HardcoreGamesMain.getTimerManager().getTime().get() > (39 * 60)) {
			event.setCancelled(true);
		}
	}

	private boolean availableToRespawn(final Player player) {
		if (BukkitMain.getServerType() == ServerType.EVENTO || BukkitMain.getServerType() == ServerType.CHAMPIONS) {
			return false;
		} else {
			return player.hasPermission("hardcoregames.respawn")
					&& HardcoreGamesMain.getTimerManager().getTime().get() <= 300;
		}
	}

	private static String getItemInHand(final Material material) {
		String causa = "o Punho";

		switch (material) {
		case WOOD_SWORD:
			causa = "uma Espada de Madeira";
			break;
		case STONE_SWORD:
			causa = "uma Espada de Pedra";
			break;
		case GOLD_SWORD:
			causa = "uma Espada de Ouro";
			break;
		case IRON_SWORD:
			causa = "uma Espada de Ferro";
			break;
		case DIAMOND_SWORD:
			causa = "uma Espada de Diamante";
			break;
		case WOOD_PICKAXE:
			causa = "uma Picareta de Madeira";
			break;
		case STONE_PICKAXE:
			causa = "uma Picareta de Pedra";
			break;
		case GOLD_PICKAXE:
			causa = "uma Picareta de Ouro";
			break;
		case IRON_PICKAXE:
			causa = "uma Picareta de Ferro";
			break;
		case DIAMOND_PICKAXE:
			causa = "uma Picareta de Diamante";
			break;
		case WOOD_AXE:
			causa = "um Machado de Madeira";
			break;
		case STONE_AXE:
			causa = "um Machado de Pedra";
			break;
		case GOLD_AXE:
			causa = "um Machado de Ouro";
			break;
		case IRON_AXE:
			causa = "um Machado de Ferro";
			break;
		case DIAMOND_AXE:
			causa = "um Machado de Diamante";
			break;
		case COMPASS:
			causa = "uma Bussola";
			break;
		case MUSHROOM_SOUP:
			causa = "uma Sopa";
			break;
		case STICK:
			causa = "um Graveto";
			break;
		default:
			break;
		}
		return causa;
	}

	private String getCausa(DamageCause deathCause) {
		String cause = "por uma causa desconhecida";

		switch (deathCause) {
		case ENTITY_ATTACK:
			cause = "atacado por um monstro";
			break;
		case CUSTOM:
			cause = "de uma forma não conhecida";
			break;
		case BLOCK_EXPLOSION:
			cause = "explodido em mil pedaços";
			break;
		case ENTITY_EXPLOSION:
			cause = "explodido por um monstro";
			break;
		case CONTACT:
			cause = "espetado por um cacto";
			break;
		case FALL:
			cause = "de queda";
			break;
		case FALLING_BLOCK:
			cause = "stompado por um bloco";
			break;
		case FIRE_TICK:
			cause = "pegando fogo";
			break;
		case FIRE:
			cause = "pegando fogo";
			break;
		case LAVA:
			cause = "nadando na lava";
			break;
		case LIGHTNING:
			cause = "atingido por um raio";
			break;
		case MAGIC:
			cause = "atingido por uma magia";
			break;
		case MELTING:
			cause = "atingido por um boneco de neve";
			break;
		case POISON:
			cause = "envenenado";
			break;
		case PROJECTILE:
			cause = "atingido por um projétil";
			break;
		case STARVATION:
			cause = "de fome";
			break;
		case SUFFOCATION:
			cause = "sufocado";
			break;
		case SUICIDE:
			cause = "se suicidando";
			break;
		case THORNS:
			cause = "encostando em alguns espinhos";
			break;
		case VOID:
			cause = "pelo void";
			break;
		case WITHER:
			cause = "pelo efeito do whiter";
			break;
		case DROWNING:
			cause = "afogado";
			break;
		default:
			break;
		}

		return cause;
	}

	public static void handleStats(final Player killer, final Player died) {
		BukkitPlayer bukkitPlayerDied = BukkitMain.getBukkitPlayer(died.getUniqueId());

		died.sendMessage(BukkitMessages.DEATH_MESSAGE_XP.replace("%quantia%", "2"));
		died.sendMessage(BukkitMessages.DEATH_MESSAGE_COINS.replace("%quantia%", "10"));

		bukkitPlayerDied.add(BukkitMain.getServerType() == ServerType.HARDCORE_GAMES ? DataType.HG_DEATHS
				: DataType.HG_EVENT_DEATHS);
		bukkitPlayerDied.remove(DataType.COINS, 10);
		bukkitPlayerDied.removeXP(2);

		HardcoreGamesMain.runAsync(() -> {
			bukkitPlayerDied.getDataHandler().saveCategorys(DataCategory.ACCOUNT, DataCategory.HARDCORE_GAMES);
		});

		if (killer == null) {
			return;
		}

		BukkitPlayer bukkitPlayerKiller = BukkitMain.getBukkitPlayer(killer.getUniqueId());

		final int xp = PlayerAPI.getXPKill(killer, bukkitPlayerKiller.getLong(DataType.DOUBLEXP_TIME)),
				coins = PlayerAPI.getCoinsKill(killer, bukkitPlayerKiller.getLong(DataType.DOUBLECOINS_TIME));

		bukkitPlayerKiller.add(DataType.COINS, coins);
		bukkitPlayerKiller.addXP(xp);
		bukkitPlayerKiller.add(
				BukkitMain.getServerType() == ServerType.HARDCORE_GAMES ? DataType.HG_KILLS : DataType.HG_EVENT_KILLS);
		HardcoreGamesMain.runAsync(() -> {
			bukkitPlayerKiller.getDataHandler().saveCategorys(DataCategory.ACCOUNT, DataCategory.HARDCORE_GAMES);
		});
	}

	@EventHandler
	public void onGameEnd(GameStageChangeEvent event) {
		if (event.getNewStage() == GameStages.END) {
			HardcoreGamesMain.console("Removing listeners from GameListener");

			HandlerList.unregisterAll(this);
		}
	}
}