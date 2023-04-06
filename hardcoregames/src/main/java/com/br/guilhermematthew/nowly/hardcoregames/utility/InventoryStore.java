package com.br.guilhermematthew.nowly.hardcoregames.utility;

import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InventoryStore {

	private String nome;
	private ItemStack[] armor, inv;
	private List<PotionEffect> potions;
	
	public InventoryStore(String nome, ItemStack[] armor, ItemStack[] inv, List<PotionEffect> potions) {
		this.nome = nome;
		this.armor = armor;
		this.inv = inv;
		this.potions = potions;
	}
}