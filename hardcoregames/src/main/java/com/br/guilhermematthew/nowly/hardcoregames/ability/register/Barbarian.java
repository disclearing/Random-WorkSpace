package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import java.util.HashMap;
import java.util.UUID;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;

public class Barbarian extends Kit {

	public Barbarian() {
		initialize(getClass().getSimpleName());
		
		setItens(new ItemBuilder().material(Material.WOOD_SWORD).name(getItemColor() + "Barbarian Sword").unbreakable().build());
	}

	private HashMap<UUID, Integer> kills = new HashMap<UUID, Integer>();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent event) {
		final Player morreu = event.getEntity(), killer = morreu.getKiller();

		if (killer == null) {
			return;
		}

		if (!(killer instanceof Player)) {
			return;
		}

		if (this.kills.containsKey(killer.getUniqueId())) {
			this.kills.put(killer.getUniqueId(), this.kills.get(killer.getUniqueId()) + 1);
		} else {
			this.kills.put(killer.getUniqueId(), 1);
		}

		if (checkItem(killer.getItemInHand(), getItemColor() + "Barbarian Sword")) {
			switch (this.kills.get(killer.getUniqueId())) {
			case 1:
				killer.getItemInHand().setType(Material.STONE_SWORD);
				killer.getItemInHand().setDurability((short) 0);
				break;
			case 5:
				killer.getItemInHand().setType(Material.IRON_SWORD);
				killer.getItemInHand().setDurability((short) 0);
				break;
			case 8:
				killer.getItemInHand().setType(Material.DIAMOND_SWORD);
				killer.getItemInHand().setDurability((short) 0);
				break;
			case 10:
				killer.getItemInHand().addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
				killer.getItemInHand().setDurability((short) 0);
				break;
			case 12:
				killer.getItemInHand().addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
				killer.getItemInHand().setDurability((short) 0);
				break;
			}
		}
	}

	@Override
	protected void clean(Player player) {
		if (kills.containsKey(player.getUniqueId())) {
			kills.remove(player.getUniqueId());
		}
	}
}