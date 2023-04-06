package com.br.guilhermematthew.nowly.login.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;

import com.br.guilhermematthew.nowly.commons.common.connections.mysql.MySQLManager;
import com.br.guilhermematthew.nowly.login.LoginMain;
import com.br.guilhermematthew.nowly.login.listener.GeneralListeners;
import com.br.guilhermematthew.nowly.login.manager.gamer.Gamer;
import com.br.guilhermematthew.nowly.login.manager.gamer.Gamer.AuthenticationType;

import lombok.Getter;

@Getter
public class LoginManager {

	private final HashMap<UUID, Gamer> gamers = new HashMap<>();

	public Gamer getGamer(Player player) {
		return getGamers().get(player.getUniqueId());
	}

	public void addGamer(Player player, AuthenticationType authenticationType) {
		if (getGamers().containsKey(player.getUniqueId())) {
			getGamers().get(player.getUniqueId()).handleLogin(player);
			return;
		}
		getGamers().put(player.getUniqueId(), new Gamer(player, authenticationType));
	}

	public void removeGamers(boolean addToQueue) {
		List<UUID> toRemove = new ArrayList<>();

		for (Gamer gamers : getGamers().values()) {
			if (System.currentTimeMillis() > gamers.getTimestamp() + TimeUnit.MINUTES.toMillis(10)) {
				if (!gamers.getPlayer().isOnline()) {
					toRemove.add(gamers.getUniqueId());
				}
			}
		}

		if (toRemove.size() != 0) {
			for (UUID uuid : toRemove) {
				String nick = getGamers().get(uuid).getNick();

				getGamers().remove(uuid);

				if (addToQueue) {
					GeneralListeners.getRunnableQueue().add(() -> LoginMain.runAsync(() -> {
						if (MySQLManager.contains("premium_map", "nick", nick)) {

							if (!MySQLManager.contains("accounts", "nick", nick)) {
								MySQLManager
										.executeUpdate("DELETE FROM premium_map WHERE nick='" + nick + "';");
							}
						}
					}));
				} else {
					LoginMain.runAsync(() -> {
						if (MySQLManager.contains("premium_map", "nick", nick)) {
							if (!MySQLManager.contains("accounts", "nick", nick)) {
								MySQLManager
								.executeUpdate("DELETE FROM premium_map WHERE nick='" + nick + "';");
							}
						}
					});
				}
			}
		}

		toRemove.clear();
	}
}