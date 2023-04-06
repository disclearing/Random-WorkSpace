package com.br.guilhermematthew.nowly.lobby.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.bukkit.manager.configuration.PluginConfiguration;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import com.br.guilhermematthew.nowly.lobby.LobbyMain;

public class ServerCommand implements CommandClass {

	public static ArrayList<UUID> autorizados = new ArrayList<>();
	
	private final List<String> bots = Arrays.asList("hg", "evento", "quartel", "pvp", "duels",
			"arena", "fps", "lavachallenge");
	
	private final List<String> holograms = Arrays.asList("kills", "killstreak", "wins", "killsevent", "winsevent");
	
	@Command(name = "setloc", groupsToUse= {Groups.ADMIN})
	public void setloc(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		
		if (args.length == 0) {
			sendHelp(commandSender);
			return;
		}
		
		Player player = commandSender.getPlayer();
		
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("spawn")) {
				PluginConfiguration.setLocation(LobbyMain.getInstance(), "spawn", player);
				LobbyMain.setSpawn(PluginConfiguration.getLocation(LobbyMain.getInstance(), "spawn"));
				commandSender.sendMessage("§aSpawn setado!");
			} else {
				sendHelp(commandSender);
			}
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("bot")) {
				if (isBot(args[1])) {
					String botName = args[1].toLowerCase();
					PluginConfiguration.setLocation(LobbyMain.getInstance(), "bots." + botName, player);
				    player.sendMessage("§aLocal do bot setado.");
				} else {
					commandSender.sendMessage("§cBot năo encontrado.");
				}
			} else if (args[0].equalsIgnoreCase("hologramas")) { 
				if (isHologram(args[1])) {
					String name = args[1];
					PluginConfiguration.setLocation(LobbyMain.getInstance(), "hologramas." + name.toLowerCase(), player);
					player.sendMessage("§aHolograma setado, é preciso reiniciar o servidor para aplicar o novo local.");
				} else {
					commandSender.sendMessage("§cHolograma inválido.");
				}
			} else {
				sendHelp(commandSender);
			}
		} else {
			sendHelp(commandSender);
		}
		
		player = null;
	}
	
	private void sendHelp(BukkitCommandSender commandSender) {
		commandSender.sendMessage("");
		commandSender.sendMessage("§cUse: /setloc spawn");
		commandSender.sendMessage("§cUse: /setloc bot <" + StringUtility.formatArrayToString(bots, true) + ">");
		commandSender.sendMessage("§cUse: /setloc hologramas <" + StringUtility.formatArrayToString(holograms, true) + ">");
		commandSender.sendMessage("");
	}

	@Command(name = "build", aliases= {"b"}, groupsToUse= {Groups.ADMIN})
	public void build(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		
		Player player = commandSender.getPlayer();
		
		if (autorizados.contains(player.getUniqueId())) {
			autorizados.remove(player.getUniqueId());
			player.sendMessage("§aVocę desativou o modo construçăo.");
			player.setGameMode(GameMode.ADVENTURE);
		} else {
			autorizados.add(player.getUniqueId());
			player.sendMessage("§aVocę ativou o modo construçăo.");
			player.setGameMode(GameMode.CREATIVE);
		}
		
		player = null;
	}
	
	public boolean isBot(String name) {
		return bots.contains(name.toLowerCase());
	}

	public boolean isHologram(String name) {
		return holograms.contains(name.toLowerCase());
	}
}