package com.br.guilhermematthew.nowly.lobby.common.scoreboard.types;

import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent;
import com.br.guilhermematthew.nowly.commons.common.data.DataHandler;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.lobby.LobbyMain;
import com.br.guilhermematthew.nowly.lobby.common.scoreboard.animating.StringAnimation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.sidebar.Sidebar;
import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.sidebar.SidebarManager;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import com.br.guilhermematthew.nowly.lobby.common.scoreboard.ScoreboardCommon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class LobbyScoreboard extends ScoreboardCommon {

	private StringAnimation animation;
	private String text = "";

	public void init() {
		animation = new StringAnimation(" LEAGUE ", "§f§l", "§2§l", "§a§l", 3);
		text = animation.next();

		registerListener();

		Bukkit.getScheduler().runTaskTimer(LobbyMain.getInstance(), new Runnable() {
			public void run() {
				text = animation.next();

				for (Player onlines : Bukkit.getOnlinePlayers()) {
					if (onlines == null) {
						continue;
					}
					if (!onlines.isOnline()) {
						continue;
					}
					if (onlines.isDead()) {
						continue;
					}
					Scoreboard score = onlines.getScoreboard();
					if (score == null) {
						continue;
					}
					Objective objective = score.getObjective(DisplaySlot.SIDEBAR);
					if (objective == null) {
						continue;
					}
					objective.setDisplayName(text);
				}
			}
		}, 40, 2L);
	}

	@Override
	public void createScoreboard(Player player) {
		Sidebar sidebar = SidebarManager.getSidebar(player.getUniqueId());
		
		sidebar.setTitle("§2§lLEAGUE");
			
		BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
		Groups group = bukkitPlayer.getGroup();
		sidebar.addBlankLine();
		sidebar.addLine("rank", "Rank: ", group.getColor() + (group.getLevel() == Groups.MEMBRO.getLevel() ? "Membro" : group.getTag().getColor() + group.getTag().getName()));
		sidebar.addBlankLine();
		sidebar.addLine("lobbyId", "Lobby: ", "§7#" + BukkitMain.getServerID());
		sidebar.addLine("onlines", "Players: ", "§a" + StringUtility.formatValue(CommonsGeneral.getServersManager().getAmountOnNetwork()));
		sidebar.addBlankLine();
		sidebar.addLine("§aleaguemc.com.br");
		
		sidebar.update();
		
		group = null;
		bukkitPlayer = null;
		
		sidebar = null;
	}

	@Override
	public void updateScoreboard(Player player) {
		BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
		Groups group = bukkitPlayer.getGroup();
		SidebarManager.getSidebar(player.getUniqueId()).updateLine("rank", "Rank: ", group.getColor() + (group.getLevel() == Groups.MEMBRO.getLevel() ? "Membro" : group.getTag().getColor() + group.getTag().getName()));
		SidebarManager.getSidebar(player.getUniqueId()).updateLine("onlines", "Players: ", "§a" + StringUtility.formatValue(
				CommonsGeneral.getServersManager().getAmountOnNetwork()));
	}

	private void registerListener() {
		Bukkit.getServer().getPluginManager().registerEvents(new Listener() {

			@EventHandler
			public void update(BukkitUpdateEvent event) {
				if (event.getType() != BukkitUpdateEvent.UpdateType.SEGUNDO) {
					return;
				}

				for (Player onlines : Bukkit.getOnlinePlayers()) {
					updateScoreboard(onlines);
				}
			}

		}, LobbyMain.getInstance());
	}

}