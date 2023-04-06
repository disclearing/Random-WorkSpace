package com.br.guilhermematthew.nowly.pvp.manager.gamer;

import java.util.UUID;

import com.br.guilhermematthew.nowly.pvp.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Gamer {

	private UUID uniqueId;
	private boolean protection;
	private Player player;
	private String kit1, kit2;

	public Gamer(UUID uniqueId) {
		setUniqueId(uniqueId);
		setProtection(true);
		setKit1("Nenhum");
		setKit2("Nenhum");
	}

	public Player getPlayer() {
		if (player == null)
			player = Bukkit.getPlayer(getUniqueId());

		return player;
	}

	public void removeProtection(Player player, boolean message) {
		if (!isProtection()) {
			return;
		}

		setProtection(false);
		player.closeInventory();

		if (message) {
			player.sendMessage(StringUtils.PERDEU_A_PROTEÇĂO);
		}
	}

	public boolean containsKit(String name) {
		return getKit1().equalsIgnoreCase(name) || getKit2().equalsIgnoreCase(name);
	}

	public void joinArena() {
		if (getKit1().equalsIgnoreCase("Nenhum")) {
			setKit1("PvP");
		}
		if (getKit2().equalsIgnoreCase("Nenhum")) {
			setKit2("PvP");
		}

		removeProtection(getPlayer(), true);
	}

	public String getKits() {
		return getKit1() + " e " + getKit2();
	}
}