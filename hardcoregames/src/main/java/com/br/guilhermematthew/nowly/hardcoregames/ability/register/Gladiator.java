package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import com.br.guilhermematthew.nowly.hardcoregames.listeners.GameListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.api.player.PlayerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent.UpdateType;
import com.br.guilhermematthew.nowly.commons.bukkit.worldedit.WorldEditAPI;
import com.br.guilhermematthew.nowly.hardcoregames.ability.utility.GladiatorFight;
import com.br.guilhermematthew.nowly.hardcoregames.events.player.PlayerDamagePlayerEvent;

public class Gladiator extends Kit {

	public Gladiator() {
		initialize(getClass().getSimpleName());
		
		setItens(new ItemBuilder().material(Material.IRON_FENCE).name(getItemColor() + "Kit " + getName()).build());
	}

	public static ConcurrentHashMap<String, GladiatorFight> gladiatorFights = new ConcurrentHashMap<>();
	public static HashMap<UUID, String> gladArena = new HashMap<>();

	@EventHandler
	public void onPlayerDamage(PlayerDamagePlayerEvent event) {
		if (inGlad(event.getDamaged())) {

			if (!inGlad(event.getPlayer())) {
				event.setCancelled(true);
			} else {
				if (!getGladArena(event.getDamaged()).equals(getGladArena(event.getPlayer()))) {
					event.setCancelled(true);
				}
			}
		} else if (inGlad(event.getPlayer())) {
			if (!inGlad(event.getDamaged())) {
				event.setCancelled(true);
			} else {
				if (!getGladArena(event.getDamaged()).equals(getGladArena(event.getPlayer()))) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onSecond(BukkitUpdateEvent event) {
		if (event.getType() != UpdateType.SEGUNDO) return;

		for (GladiatorFight glads : gladiatorFights.values()) {
			if (!glads.isEnded()) {
				glads.onSecond();
			}
		}
	}

	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof Player))
			return;

		Player player = event.getPlayer();

		if ((player.getItemInHand().getType().equals(Material.IRON_FENCE)) && (useAbility(player))) {
			if (inGlad(player)) {
				player.sendMessage("§cVocê já esta em um gladiator.");
				return;
			}
			Player player1 = (Player) event.getRightClicked();
			if (inGlad(player1)) {
				player.sendMessage("§cEste jogador já está em um Gladiator.");
				return;
			}

			Location toGlad = getLocationForGlad(player.getLocation());
			if (toGlad == null) {
				player.sendMessage("§cVocê não pode criar sua arena aqui.");
				return;
			}

			int id = getArenaID();
			if ((gladiatorFights.containsKey("arena" + id)) || (id == 0)) {
				player.sendMessage("§cVocê não pode criar sua arena aqui.");
				return;
			}

			gladiatorFights.put("arena" + id, new GladiatorFight(toGlad.getBlock(), id, player, player1));

			gladArena.put(player.getUniqueId(), "arena" + id);
			gladArena.put(player1.getUniqueId(), "arena" + id);

			// removerBlocos(toGlad);
			criarGladiator(toGlad);

			Location l1 = new Location(toGlad.getWorld(), toGlad.getX() + 6.5D, toGlad.getY() + 1.500,
					toGlad.getZ() + 6.5D);
			l1.setYaw(135.0F);
			Location l2 = new Location(toGlad.getWorld(), toGlad.getX() - 5.5D, toGlad.getY() + 1.500,
					toGlad.getZ() - 5.5D);
			l2.setYaw(315.0F);

			player.setFallDistance(-5);
			player1.setFallDistance(-5);

			player.teleport(l1);
			player1.teleport(l2);

			l1 = null;
			l2 = null;
		}
	}

	private int getArenaID() {
		int id = 0;

		try {
			id = CommonsConst.RANDOM.nextInt(2000) + 3;
		} catch (Exception ex) {
			id = 0;
		}
		
		return id;
	}

	public static String getGladArena(Player player) {
		return gladArena.getOrDefault(player.getUniqueId(), "0");
	}

	public static boolean inGlad(Player player) {
		return gladArena.containsKey(player.getUniqueId());
	}

	public static GladiatorFight getGladiatorFight(Player player) {
		return gladiatorFights.get(getGladArena(player));
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (event.getBlock().getType() == Material.GLASS) {
			if (gladArena.containsKey(event.getPlayer().getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if ((e.getBlock().getType().equals(Material.IRON_FENCE)) && (containsHability(e.getPlayer()))) {
			e.setCancelled(true);
		}
	}

	public static Location getLocationForGlad(Location loc) {
		loc.setY(110);

		boolean hasGladi = true;
		while (hasGladi) {

			hasGladi = false;
			boolean stop = false;

			for (double x = -8.0D; x <= 8.0D; x += 1.0D) {
				for (double z = -8.0D; z <= 8.0D; z += 1.0D) {
					for (double y = 0.0D; y <= 10.0D; y += 1.0D) {
						Location location = new Location(loc.getWorld(), loc.getX() + x, 110.0D + y, loc.getZ() + z);

						if (location.getBlock().getType() != Material.AIR) {
							hasGladi = true;
							loc = new Location(loc.getWorld(), loc.getX() + 30.0D, 110.0D, loc.getZ());
							stop = true;
						}
						if (stop)
							break;
					}
					if (stop)
						break;
				}
				if (stop)
					break;
			}
		}
		return loc;
	}

	public static void criarGladiator(Location loc) {
		List<Location> cuboid = new ArrayList<>();

		for (int bX = -8; bX <= 8; bX++) {
			for (int bZ = -8; bZ <= 8; bZ++) {
				for (int bY = -1; bY <= 10; bY++) {
					if (bY == -1) {
						cuboid.add(loc.clone().add(bX, bY, bZ));
					} else if ((bX == -8) || (bZ == -8) || (bX == 8) || (bZ == 8)) {
						cuboid.add(loc.clone().add(bX, bY, bZ));
					}
				}
			}
		}

		for (Location loc1 : cuboid) {
			WorldEditAPI.setAsyncBlock(loc1.getWorld(), loc1, 20);
		}

		cuboid.clear();
		cuboid = null;
	}

	public static void removerBlocos(Location loc) {
		List<Location> cuboid = new ArrayList<>();

		for (int bX = -8; bX <= 8; bX++) {
			for (int bZ = -8; bZ <= 8; bZ++) {
				for (int bY = -1; bY <= 10; bY++) {
					cuboid.add(loc.clone().add(bX, bY, bZ));
				}
			}
		}

		for (Location loc1 : cuboid) {
			WorldEditAPI.setAsyncBlock(loc1.getWorld(), loc1, 0);
		}

		cuboid.clear();
		cuboid = null;
	}

	public static boolean isQuitedOnGladiator(Player player) {
		if (inGlad(player)) {
			GladiatorFight glad = getGladiatorFight(player);

			glad.teleportBack();

			Player winner = null;

			if (glad.getPlayers()[0].getUniqueId().equals(player.getUniqueId())) {
				winner = glad.getPlayers()[1];
			} else {
				winner = glad.getPlayers()[0];
			}

			PlayerAPI.dropItems(player, glad.getBackForPlayer(player).add(0, 0.5, 0));

			glad.cancelGlad();

			GameListener.handleStats(winner, player);

			glad = null;
			return true;
		}
		return false;
	}

	@Override
	protected void clean(Player player) {
	}
}