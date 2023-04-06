package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;

public class Grandpa extends Kit {

	public Grandpa() {
		initialize(getClass().getSimpleName());
		
		setItens(new ItemBuilder().material(Material.STICK).name(getItemColor() + "Kit " + getName())
				.enchantment(Enchantment.KNOCKBACK, 3).build());
	}

	@Override
	protected void clean(Player player) {
	}
}