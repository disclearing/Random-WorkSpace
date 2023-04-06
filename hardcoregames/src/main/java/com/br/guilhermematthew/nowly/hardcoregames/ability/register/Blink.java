package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;

public class Blink extends Kit {

	public Blink() {
		initialize(getClass().getSimpleName());
		
		setItens(new ItemBuilder().material(Material.NETHER_STAR).name(getItemColor() + "Kit " + getName()).build());
	}

	public HashMap<UUID, Integer> blink = new HashMap<>();

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if ((player.getItemInHand().getType().equals(Material.NETHER_STAR)) && (containsHability(player))) {
			if (hasCooldown(player)) {
				sendMessageCooldown(player);
				return;
			}

			@SuppressWarnings("deprecation")
			Block block = player.getTargetBlock((HashSet<Byte>) null, 10);

			if (block.getType().equals(Material.AIR)) {

				blink.put(player.getUniqueId(),
						blink.containsKey(player.getUniqueId()) ? blink.get(player.getUniqueId()) + 1 : 1);

				if (blink.get(player.getUniqueId()).equals(4)) {
					blink.remove(player.getUniqueId());
					addCooldown(player, getCooldownSeconds());
				}

				block.setType(Material.LEAVES);

				Vector v = player.getEyeLocation().getDirection();

				player.teleport(block.getLocation().add(0, 2, 0).setDirection(v));
				player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);

				v = null;
			}
			block = null;
		}
	}

	@Override
	protected void clean(Player player) {
		if (blink.containsKey(player.getUniqueId())) {
			blink.remove(player.getUniqueId());
		}
	}
}