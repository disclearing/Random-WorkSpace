package com.br.guilhermematthew.nowly.hardcoregames.manager.gamer;

import java.util.UUID;

import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesOptions;
import com.br.guilhermematthew.nowly.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Gamer {

	private UUID uniqueId;
	private String kit1, kit2;
	
	private boolean eliminado, playing, online, relogar;
	private int kills, taskID;
	
	private Player player;
	
	public Gamer(UUID uniqueId) {
		setUniqueId(uniqueId);
		
		this.kit1 = "Nenhum";
		this.kit2 = "Nenhum";
		
		setKills(0);
		setTaskID(-1);
		setEliminado(false);
		setPlaying(true);
		setOnline(true);
		setRelogar(false);
	}
	
	public boolean containsKit(String name) {
		return getKit1().equalsIgnoreCase(name) || (HardcoreGamesOptions.DOUBLE_KIT && getKit2().equalsIgnoreCase(name));
	}
	
	public void addKill() {
		setKills(getKills() + 1);
	}
	
	public Player getPlayer() {
		if (this.player == null) {
			this.player = Bukkit.getPlayer(uniqueId);
		}
		
		return this.player;
	}

	public String getKits() {
		if (!getKit2().equalsIgnoreCase("Nenhum")) {
			return getKit1() + " e " + getKit2();
		}
		return getKit1();
	}

	public void setKit1(String kit) {
		this.kit1 = kit;
		HardcoreGamesScoreboard.getScoreBoardCommon().updateKit1(getPlayer(), kit);
	}
	
	public void setKit2(String kit) {
		this.kit2 = kit;
		HardcoreGamesScoreboard.getScoreBoardCommon().updateKit2(getPlayer(), kit);
	}

}