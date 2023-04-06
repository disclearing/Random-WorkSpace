package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import java.util.ArrayList;

import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.Gamer;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.api.vanish.VanishAPI;

public class Endermage extends Kit {

	public Endermage() {
		initialize(getClass().getSimpleName());
		setUseInvincibility(true);
		
		setItens(new ItemBuilder().material(Material.NETHER_BRICK_ITEM).name(getItemColor() + "Kit " + getName())
				.build());
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (!event.getAction().name().contains("RIGHT"))
			return;

		if ((event.getPlayer().getItemInHand().getType().equals(Material.NETHER_BRICK_ITEM))
				&& (containsHability(event.getPlayer()))) {

			if (event.getClickedBlock().getType() != Material.ENDER_PORTAL_FRAME) {
				Player player = event.getPlayer();

				if (hasCooldown(player)) {
					sendMessageCooldown(player);
					return;
				}

				final Block b = event.getClickedBlock();
				final BlockState bs = b.getState();

				if (b.getType().equals(Material.BEDROCK)) {
					player.sendMessage("§cVocê não pode puxar neste bloco.");
					return;
				}
				if (player.getLocation().getBlockY() > 115) {
					player.sendMessage("§cVocê não pode puxar nesta altura.");
					return;
				}

				b.setType(Material.ENDER_PORTAL_FRAME);
				addCooldown(player, getCooldownSeconds());

				new BukkitRunnable() {
					int segundos = 5;
					final Location portal2 = b.getLocation().clone().add(0.5, 1.5, 0.5);

					public void run() {
						ArrayList<Player> players = getNearbyPlayers(player, portal2);

						if (!player.isOnline() || calculateDistance(player.getLocation(), portal2) > 50) {
							if (!player.isOnline()) {
								cancel();
							}
							resetBlock(b, bs);
						}

						if (!players.isEmpty()) {
							for (Player pl : players) {
								pl.setFallDistance(0);
								pl.setNoDamageTicks(110);
								pl.teleport(portal2);
								player.sendMessage("§aVocê puxou: " + pl.getName());
								pl.sendMessage("§aVocê foi puxado pelo " + player.getName());
								pl.sendMessage("§aVocê esta invencível por 5 segundos");
							}
							player.setFallDistance(0);
							player.setNoDamageTicks(110);
							player.teleport(portal2);
							player.sendMessage("§cVocê esta invencível por 5 segundos");
							resetBlock(b, bs);
							cancel();
						}

						if (segundos == 0) {
							resetBlock(b, bs);
							cancel();
						}
						segundos--;
					}
				}.runTaskTimer(HardcoreGamesMain.getInstance(), 20L, 20L);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void resetBlock(Block b, BlockState bs) {
		HardcoreGamesMain.runLater(() -> b.setTypeIdAndData(bs.getTypeId(), bs.getRawData(), false), 100);
	}

	private ArrayList<Player> getNearbyPlayers(Player p2, Location portal) {
		ArrayList<Player> players = new ArrayList<>();

		for (Player onlines : Bukkit.getOnlinePlayers()) {
			if (onlines == p2)
				continue;
			if (!isEnderable(portal, onlines.getLocation()))
				continue;
			if (onlines.getLocation().getBlockY() > 128)
				continue;
			if (VanishAPI.isInvisible(onlines))
				continue;
			Gamer gamer = GamerManager.getGamer(onlines.getUniqueId());

			if (!gamer.containsKit("Endermage") && gamer.isPlaying()) {
				players.add(onlines);
			}
		}
		return players;
	}

	private boolean isEnderable(Location portal, Location player) {
		return (Math.abs(portal.getX() - player.getX()) < 2.0D) && (Math.abs(portal.getZ() - player.getZ()) < 2.0D)
				&& (Math.abs(portal.getY() - player.getY()) > 2.0D);
	}

	public int calculateDistance(Location a, Location b) {
		int distance = 0, x1 = a.getBlockX(), x2 = b.getBlockX(), z1 = a.getBlockZ(), z2 = b.getBlockZ();
		if (x1 != x2) {
			if (x1 < x2) {
				for (int i = x1; i <= x2 - 1; i++)
					distance++;
			} else {
				for (int i = x2; i <= x1 - 1; i++)
					distance++;
			}
		}
		if (z1 != z2) {
			if (z1 < z2) {
				for (int i = z1; i <= z2 - 1; i++)
					distance++;
			} else {
				for (int i = z2; i <= z1 - 1; i++)
					distance++;
			}
		}
		return distance;
	}

	@Override
	protected void clean(Player player) {
	}
}