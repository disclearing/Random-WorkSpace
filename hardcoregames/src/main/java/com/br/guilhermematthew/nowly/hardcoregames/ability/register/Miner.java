package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;

public class Miner extends Kit {

	public Miner() {
		initialize(getClass().getSimpleName());
		
		setItens(new ItemBuilder().material(Material.STONE_PICKAXE).unbreakable()
				.name(getItemColor() + "Kit " + getName()).enchantment(Enchantment.DIG_SPEED)
				.enchantment(Enchantment.DURABILITY, 5).build(), 
				new ItemBuilder().material(Material.APPLE).amount(2).name(getItemColor() + "Kit " + getName()).build());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		ItemStack hand = event.getPlayer().getInventory().getItemInHand();

		if (containsHability(event.getPlayer()) && hand != null && hand.getType().name().contains("PICKAXE")) {
			Location spawn = event.getBlock().getLocation().clone().add(0.5, 0.5, 0.5);
			Material mat = event.getBlock().getType();
			if (!mat.name().contains("ORE")) {
				return;
			}

			event.setCancelled(true);

			Map<Material, Integer> drops = new HashMap<>();
			for (Block b : getNearbyBlocks(event.getBlock(), 1)) {
				if (b.getType() == mat
						|| mat == Material.REDSTONE_ORE && b.getType() == Material.GLOWING_REDSTONE_ORE) {
					b.getDrops().forEach(drop -> {
						drops.put(drop.getType(), drops.getOrDefault(drop.getType(), 0) + drop.getAmount());
					});
					b.setType(Material.AIR);
				}
			}

			int xp = drops.entrySet().stream().filter($ -> !$.getKey().name().endsWith("_ORE"))
					.mapToInt($ -> $.getValue() * 2).sum();
			drops.forEach((material, q) -> {
				int i = q;
				while (i > 0) {
					spawn.getWorld().dropItem(spawn, new ItemStack(material, i));
					i -= Math.min(i, 64);
				}
			});

			if (xp > 0) {
				ExperienceOrb orb = event.getBlock().getWorld().spawn(spawn, ExperienceOrb.class);
				orb.setExperience(xp);
			}
		}
	}

	private List<Block> getNearbyBlocks(Block block, int i) {
		List<Block> blocos = new ArrayList<Block>();
		for (int x = -i; x <= i; x++) {
			for (int y = -i; y <= i; y++) {
				for (int z = -i; z <= i; z++) {
					blocos.add(block.getLocation().clone().add(x, y, z).getBlock());
				}
			}
		}
		return blocos;
	}

	@EventHandler
	public void onEat(PlayerItemConsumeEvent event) {
		if (!containsHability(event.getPlayer()))
			return;

		if (event.getItem().getType() == Material.APPLE) {
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 600, 1));
		}
	}

	@Override
	protected void clean(Player player) {
	}
}