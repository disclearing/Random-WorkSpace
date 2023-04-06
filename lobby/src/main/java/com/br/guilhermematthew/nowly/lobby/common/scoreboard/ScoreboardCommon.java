package com.br.guilhermematthew.nowly.lobby.common.scoreboard;

import com.br.guilhermematthew.nowly.lobby.LobbyMain;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import static org.bukkit.Bukkit.getServer;

public abstract class ScoreboardCommon {

	public abstract void createScoreboard(final Player player);
	public abstract void updateScoreboard(final Player player);


}