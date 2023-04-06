package com.br.guilhermematthew.nowly.login.listener;

import com.br.guilhermematthew.nowly.commons.bukkit.utility.LocationUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import com.br.guilhermematthew.nowly.login.LoginMain;
import com.br.guilhermematthew.nowly.login.LoginMessages;
import com.br.guilhermematthew.nowly.login.manager.captcha.CaptchaManager;
import org.bukkit.inventory.meta.ItemMeta;

public class CaptchaListeners implements Listener {

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if ((event.getClickedInventory() != null) && (event.getCurrentItem() != null) && (event.getCurrentItem().hasItemMeta())) {
			Player player = (Player) event.getWhoClicked();
			if (event.getClickedInventory().getTitle().equalsIgnoreCase("Sistema de captcha")) {
				event.setCancelled(true);

				ItemStack item = event.getCurrentItem();
				if (item.getType() == Material.SKULL_ITEM) {
					ItemMeta itemMeta = item.getItemMeta();
					if (itemMeta != null) {
						if (itemMeta.getDisplayName().equals("§aClique para escolher este")) {
							LoginMain.getManager().getGamer(player).setCaptchaConcluido(true);

							player.sendMessage(LoginMessages.CAPTCHA_CONCLUIDO);
							player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);

							player.closeInventory();
							} else {
							player.kickPlayer("§cDetectamos que Você errou em nosso teste CAPTCHA\n§cVocê foi removido por segurança!");
						}

						}

						itemMeta = null;
					}
				}
		}
	}


	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getInventory().getName().contains("Sistema")) {
			Player player = (Player) event.getPlayer();
			
			LoginMain.runLater(() -> {
				if (player == null) return;
				if (LoginMain.getManager().getGamer(player).isCaptchaConcluido()) return;
				
				CaptchaManager.createCaptcha(player);
			}, 20);
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if (!LocationUtil.isRealMovement(event.getFrom(), event.getTo())) return;
		
		if (!LoginMain.getManager().getGamer(event.getPlayer()).isLogado()) {
			event.getPlayer().teleport(event.getFrom());
		}
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();

		if (!LoginMain.getManager().getGamer(player).isLogado()) {

			String msg = event.getMessage().toLowerCase();
			if (msg.startsWith("/logar")
					|| (msg.startsWith("/registrar") || (msg.startsWith("/login") || (msg.startsWith("/register"))))) {
				event.setCancelled(false);
			} else {
				event.setCancelled(true);
				player.sendMessage(LoginMessages.PARA_USAR_UM_COMANDO_E_NECESSARIO_ESTAR_LOGADO);
			}
		}
	}
}