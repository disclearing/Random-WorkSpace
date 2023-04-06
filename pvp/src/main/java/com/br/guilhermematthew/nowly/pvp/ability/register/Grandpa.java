package com.br.guilhermematthew.nowly.pvp.ability.register;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.pvp.ability.Kit;

public class Grandpa extends Kit {

	public Grandpa() {
		initialize(getClass().getSimpleName());
		
		setItens(new ItemBuilder().type(Material.STICK).name(
				getItemColor() + "Kit " + getName()).enchantment(Enchantment.KNOCKBACK, 3).build());
	}

	@Override
	protected void clean(Player player) {}
}