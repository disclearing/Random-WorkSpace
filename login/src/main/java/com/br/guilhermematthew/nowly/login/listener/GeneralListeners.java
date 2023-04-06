package com.br.guilhermematthew.nowly.login.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent.UpdateType;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.login.LoginMain;
import com.br.guilhermematthew.nowly.login.LoginMessages;
import com.br.guilhermematthew.nowly.login.commands.ServerCommand;
import com.br.guilhermematthew.nowly.login.manager.captcha.CaptchaManager;
import com.br.guilhermematthew.nowly.login.manager.gamer.Gamer;
import com.br.guilhermematthew.nowly.login.manager.gamer.Gamer.AuthenticationType;

import lombok.Getter;

public class GeneralListeners implements Listener {

	private static final Inventory inventory = Bukkit.getServer().createInventory(null, 9, "Clique para se conectar");
	
	static {
		//inventory.setItem(0, new ItemBuilder().type(Material.NETHER_STAR).name("§aConectar a Main Lobby").build());
	}
	
	private int MINUTES = 0;
	
	@Getter
	private static final List<Runnable> runnableQueue = new ArrayList<>();
	
	@EventHandler
	public void onMinute(BukkitUpdateEvent event) {
		if (event.getType() != UpdateType.MINUTO) {
			return;
		}
		
		if (MINUTES == 10) {
			LoginMain.getManager().removeGamers(true);
			MINUTES=0;
			return;
		}
		MINUTES++;
	}
	
	@EventHandler
	public void onUpdate(BukkitUpdateEvent event) {
		if (event.getType() != UpdateType.SEGUNDO) return;
		
		for (Player onlines : Bukkit.getOnlinePlayers()) {
			 Gamer gamer = LoginMain.getManager().getGamer(onlines);
			
			 if (gamer == null)
				 continue;
			
			if (gamer.isCaptchaConcluido()) {
				if (!gamer.isLogado()) {
					if (gamer.getSecondsLogin() == 20) {
						onlines.kickPlayer(LoginMessages.VOCE_DEMOROU_MUITO);				
						continue;
					}
					if (gamer.getSecondsLogin() % 2 == 0) {
		    			if (gamer.getAuthenticationType() == AuthenticationType.LOGAR) {
		    				onlines.sendMessage(LoginMessages.MENSAGEM_PARA_LOGAR);
		    			} else {
		    				onlines.sendMessage(LoginMessages.MENSAGEM_PARA_SE_REGISTRAR);
		    			}
					}
					gamer.addSecondsLogin();
				} else {
					if (gamer.getSecondsConnect() == 25) {
						onlines.kickPlayer(LoginMessages.VOCE_DEMOROU_MUITO_PARA_ENTRAR_NO_LOBBY);				
						continue;
					}
					gamer.addSecondsConnect();
				}
			} else {
				if (gamer.getSecondsCaptcha() == 8) {
					onlines.kickPlayer(LoginMessages.TEMPO_ESGOTADO);
					continue;
				}
				gamer.addSecondsCaptcha();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onUpdateHandleQueue(BukkitUpdateEvent event) {
		if (event.getType() != UpdateType.SEGUNDO)
			return;
		
		if (runnableQueue.size() > 0) {
			Runnable runnable = runnableQueue.remove(0);
			runnable.run();
		}
	}
	
	@EventHandler
	public void onInventory(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			if (event.getInventory().getTitle().equals(inventory.getTitle())) {
				if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
					event.setCancelled(true);
					Player player = (Player)event.getWhoClicked();
					
					player.closeInventory();
					
					BukkitServerAPI.redirectPlayer(player, "Lobby");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR) @SneakyThrows
	public void onLogin(PlayerLoginEvent event) {
		if (event.getResult() == PlayerLoginEvent.Result.ALLOWED) {
			Player player = event.getPlayer();
			
			AuthenticationType authenticationType = AuthenticationType.LOGAR;
			BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());

			bukkitPlayer.getDataHandler().load(DataCategory.REGISTER);
			String password = bukkitPlayer.getString(DataType.REGISTRO_SENHA);

			System.out.println(password);

			if (password.length() < 4) {
				authenticationType = AuthenticationType.REGISTRAR;
				bukkitPlayer.set(DataType.LAST_IP, event.getAddress().getHostAddress());
				bukkitPlayer.set(DataType.FIRST_LOGGED_IN, System.currentTimeMillis());
			}
			
			LoginMain.getManager().addGamer(player, authenticationType);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		player.getInventory().clear();
		player.teleport(LoginMain.getSpawn());
		
		if (!player.getGameMode().equals(GameMode.ADVENTURE)) {
		    player.setGameMode(GameMode.ADVENTURE);
		}
		
		BukkitMain.runLater(() -> {
			if (!player.isOnline())
				return;
			
			CaptchaManager.createCaptcha(player);
		}, 30L);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		final UUID uuid = event.getPlayer().getUniqueId();
	
		LoginMain.getManager().getGamers().get(uuid).refresh();

		ServerCommand.autorizados.remove(uuid);
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onAsyncChat(AsyncPlayerChatEvent event) {
		if (event.getMessage().startsWith("/")) return;

		event.setCancelled(true);
	}
	
	@EventHandler
	public void onSpread(BlockSpreadEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onFood(FoodLevelChangeEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		event.setCancelled(event.toWeatherState());
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		event.setCancelled(true);
		if (event.getCause().equals(DamageCause.VOID)) {
			event.getEntity().teleport(LoginMain.getSpawn());
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void ignite(BlockIgniteEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onEntitySpawn(CreatureSpawnEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onSpawn(ItemSpawnEvent event) {
		event.setCancelled(true);
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onBreak(BlockBreakEvent event) {
		event.setCancelled((!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) ||
				(!ServerCommand.autorizados.contains(event.getPlayer().getUniqueId())));
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		event.setCancelled((!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) ||
				(!ServerCommand.autorizados.contains(event.getPlayer().getUniqueId())));
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if ((event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) && 
				(ServerCommand.autorizados.contains(event.getPlayer().getUniqueId()))) {
			event.setCancelled(false);
		} else {
			event.setCancelled(true);
		}
		
		if (event.getAction() != Action.PHYSICAL) {
			if (event.getPlayer().getItemInHand().getType().equals(Material.COMPASS)) {
				event.getPlayer().openInventory(inventory);
			}
		}
	}
}