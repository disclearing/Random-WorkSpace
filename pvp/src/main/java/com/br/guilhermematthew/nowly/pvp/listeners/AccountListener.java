package com.br.guilhermematthew.nowly.pvp.listeners;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.pvp.commands.ServerCommand;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.server.ServerStatusUpdateEvent;
import com.br.guilhermematthew.nowly.pvp.manager.gamer.Gamer;
import com.br.guilhermematthew.nowly.pvp.manager.gamer.GamerManager;

import java.sql.SQLException;

public class AccountListener implements Listener {
	
	private final int MEMBERS_SLOTS = 80;

	@EventHandler
	public void onPre(AsyncPlayerPreLoginEvent e) throws SQLException {
		BukkitMain.getBukkitPlayer(e.getUniqueId()).getDataHandler().load(DataCategory.KITPVP);
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) return;
		
		if (Bukkit.getOnlinePlayers().size() >= MEMBERS_SLOTS) {
			if (!event.getPlayer().hasPermission("commons.entrar")) {
				event.disallow(Result.KICK_OTHER, BukkitMessages.ACABOU_OS_SLOTS_PARA_MEMBROS);
			} else {
				event.allow();
				
				Gamer randomGamer = GamerManager.getGamers().values().stream().
						filter(Gamer::isProtection).
						filter(check -> !check.getPlayer().hasPermission("commons.entrar")).findAny().orElse(null);
				
				if (randomGamer != null) {
					randomGamer.getPlayer().sendMessage(BukkitMessages.SEU_SLOT_FOI_DADO_A_UM_JOGADOR_VIP);
					
					BukkitServerAPI.redirectPlayer(randomGamer.getPlayer(), "LobbyPvP", true);
				}
			}
		}
		
		if (event.getResult() == PlayerLoginEvent.Result.ALLOWED) {
			GamerManager.addGamer(event.getPlayer().getUniqueId());
		}
	}
	
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        GamerManager.removeGamer(event.getPlayer().getUniqueId());

		ServerCommand.autorizados.remove(event.getPlayer().getUniqueId());
    }
    
	@EventHandler
	public void onUpdateServer(ServerStatusUpdateEvent event) {
		event.writeMemberSlots(MEMBERS_SLOTS);
	}
}