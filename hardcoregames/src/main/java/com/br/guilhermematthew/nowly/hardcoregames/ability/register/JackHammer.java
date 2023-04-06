package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import java.util.HashMap;
import java.util.UUID;

import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;

public class JackHammer extends Kit {

	public JackHammer() {
		initialize(getClass().getSimpleName());
		
		setItens(new ItemBuilder().material(Material.STONE_AXE).name(getItemColor() + "Kit " + getName()).build());
	}

	private HashMap<UUID, Integer> blocos = new HashMap<>();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent e) {
		if (e.isCancelled()) {
			return;
		}

		if ((e.getPlayer().getItemInHand().getType().equals(Material.STONE_AXE)) && (containsHability(e.getPlayer()))) {
			if (hasCooldown(e.getPlayer())) {
				sendMessageCooldown(e.getPlayer());
				return;
			}
			blocos.put(e.getPlayer().getUniqueId(),
					blocos.containsKey(e.getPlayer().getUniqueId()) ? blocos.get(e.getPlayer().getUniqueId()) + 1 : 1);
			if (blocos.get(e.getPlayer().getUniqueId()).equals(4)) {
				addCooldown(e.getPlayer(), getCooldownSeconds());
				blocos.remove(e.getPlayer().getUniqueId());
				return;
			}
			if (e.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR) {
				quebrar(e.getBlock(), BlockFace.UP);
			} else {
				quebrar(e.getBlock(), BlockFace.DOWN);
			}
		}
	}

	void quebrar(final Block b, final BlockFace face) {
		new BukkitRunnable() {
			Block block = b;

			@SuppressWarnings("deprecation")
			public void run() {
				if (block.getType() != Material.BEDROCK && block.getType() != Material.ENDER_PORTAL_FRAME
						&& block.getY() <= 128) {
					block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType().getId(), 30);
					block.setType(Material.AIR);
					block = block.getRelative(face);
				} else {
					cancel();
				}
			}
		}.runTaskTimer(HardcoreGamesMain.getInstance(), 2L, 2L);
	}

	@Override
	protected void clean(Player player) {
		if (blocos.containsKey(player.getUniqueId())) {
			blocos.remove(player.getUniqueId());
		}
	}
}