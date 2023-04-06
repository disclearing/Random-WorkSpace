package com.br.guilhermematthew.nowly.login.manager.captcha;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;

public class CaptchaManager {

	private final static ItemStack olhoDoFim = new ItemBuilder().type(Material.SKULL_ITEM).skinURL("http://textures.minecraft.net/texture/57c57ecc6f34fc34cd3524ce0b7c1dd1c405f1310ea25425e72c8a502e99ad52").durability(3).name("§aClique para escolher este").build();
	private final static ItemStack enderPearl = new ItemBuilder().type(Material.SKULL_ITEM).skinURL("http://textures.minecraft.net/texture/c9f28bf9d149443583f9c1cbc0f17d8f186648336d7d3688ed471cfdf8837002").durability(3).name("§7Ops! Escolha outro está é a errada.").build();
	
	public static void createCaptcha(Player player) {
		if (!player.isOnline()) return;
		
		Inventory inventory = player.getServer().createInventory(null, 3 * 9, "Sistema de captcha");
		
		int randomSlot = CommonsConst.RANDOM.nextInt(26);
		inventory.setItem(randomSlot, olhoDoFim);
		
		for (int i = 0; i < 27; i++) {
			 if (i == randomSlot) {
				 continue;
			 }
			 inventory.setItem(i, enderPearl);
		}
		
		player.openInventory(inventory);
	}
}