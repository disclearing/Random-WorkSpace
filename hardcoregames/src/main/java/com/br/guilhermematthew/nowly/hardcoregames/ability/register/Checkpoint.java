package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import java.util.HashMap;
import java.util.UUID;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;

public class Checkpoint extends Kit {

	public Checkpoint() {
		initialize(getClass().getSimpleName());
		
		setItens(new ItemBuilder().material(Material.NETHER_FENCE).name(getItemColor() + "Kit " + getName()).build(),
				new ItemBuilder().material(Material.FLOWER_POT_ITEM).name(getItemColor() + "Kit " + getName()).build());
	}

	private HashMap<UUID, Location> checkpoints = new HashMap<UUID, Location>();

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void colocar(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		Block b = e.getBlock();

		if (b.getType() == Material.NETHER_FENCE) {
			if (containsHability(p)) {
				if (hasCooldown(p)) {
					sendMessageCooldown(p);
					e.setBuild(false);
					e.setCancelled(true);
					return;
				}
				p.setItemInHand(e.getItemInHand());
				p.updateInventory();
				if (checkpoints.containsKey(p.getUniqueId())) {
					Location loc = checkpoints.get(p.getUniqueId());
					loc.getBlock().setType(Material.AIR);
				}
				checkpoints.put(p.getUniqueId(), e.getBlock().getLocation());
				p.sendMessage("§aPosição setada.");
			}
		}
	}

	@EventHandler
	public void teleportCheckpoint(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ItemStack i = p.getItemInHand();

		if (i.getType() == Material.FLOWER_POT_ITEM) {
			e.setCancelled(true);

			if (p.getLocation().getY() > 128) {
				return;
			}

			if (!containsHability(p)) {
				return;
			}

			if (!checkpoints.containsKey(p.getUniqueId())) {
				p.sendMessage("§cVocê não ainda não salvou nenhuma localização.");
			} else {
				if (hasCooldown(p)) {
					sendMessageCooldown(p);
					return;
				}
				p.teleport(this.checkpoints.get(p.getUniqueId()));
				p.sendMessage("§aTeleportado");
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
				addCooldown(p, getCooldownSeconds());
			}
		}
	}

	@EventHandler
	public void blockDamage(BlockDamageEvent e) {
		if (this.checkpoints.containsValue(e.getBlock().getLocation())
				&& e.getBlock().getType() == Material.NETHER_FENCE) {
			e.setCancelled(true);
			for (UUID p : this.checkpoints.keySet()) {
				if (this.checkpoints.get(p) == e.getBlock().getLocation()) {
					if (Bukkit.getPlayer(p) != null)
						Bukkit.getPlayer(p).sendMessage("§cO seu Checkpoint foi destruído.");
					this.checkpoints.remove(p);
				}
			}
			e.getBlock().setType(Material.AIR);
			e.getPlayer().sendMessage("§cVocê destruiu um checkpoint.");
			e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
		}
	}

	@Override
	protected void clean(Player player) {
	}
}