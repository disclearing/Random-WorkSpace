package com.br.guilhermematthew.nowly.pvp.commands;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.player.PlayerRequestEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.manager.configuration.PluginConfiguration;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.pvp.PvPMain;
import com.br.guilhermematthew.nowly.pvp.StringUtils;
import com.br.guilhermematthew.nowly.pvp.manager.combatlog.CombatLogManager;
import com.br.guilhermematthew.nowly.pvp.mode.arena.ArenaMode;
import com.br.guilhermematthew.nowly.pvp.mode.fps.FPSMode;
import com.br.guilhermematthew.nowly.pvp.mode.lavachallenge.LavaChallengeMode;

public class ServerCommand implements CommandClass {

	public static ArrayList<UUID> autorizados = new ArrayList<>();
	
	@Command(name = "spawn")
	public void spawn(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		
		Player player = commandSender.getPlayer();
		
		if (CombatLogManager.getCombatLog(player).isFighting()) {
			player.sendMessage(StringUtils.VOCE_NAO_PODE_IR_PARA_O_SPAWN_EM_PVP);
			return;
		}
		
		Bukkit.getServer().getPluginManager().callEvent(new PlayerRequestEvent(player, "teleport-spawn"));
	}
	
	@Command(name = "setloc", groupsToUse= {Groups.ADMIN})
	public void setloc(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		
		if (args.length == 0) {
			commandSender.sendMessage("§cUse: /setloc spawn");
			return;
		}
		
		Player player = commandSender.getPlayer();
		
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("spawn")) {
				PluginConfiguration.setLocation(PvPMain.getInstance(), "spawn", player);
				
				switch (BukkitMain.getServerType()) {
				case PVP_FPS:
					FPSMode.setSpawn(PluginConfiguration.getLocation(PvPMain.getInstance(), "spawn"));
					break;
				case PVP_LAVACHALLENGE:
					LavaChallengeMode.setSpawn(PluginConfiguration.getLocation(PvPMain.getInstance(), "spawn"));
					break;
				case PVP_ARENA:
					ArenaMode.setSpawn(PluginConfiguration.getLocation(PvPMain.getInstance(), "spawn"));
					break;
				default:
					break;
				}
				commandSender.sendMessage("§aSpawn setado!");
			} else {
				commandSender.sendMessage("§cUse: /setloc spawn");
			}
		} else {
			commandSender.sendMessage("§cUse: /setloc spawn");
		}
	}
	
	@Command(name = "build", aliases= {"b"}, groupsToUse= {Groups.ADMIN})
	public void build(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		
		Player player = commandSender.getPlayer();
		
		if (autorizados.contains(player.getUniqueId())) {
			autorizados.remove(player.getUniqueId());
			player.sendMessage("§aVocê desativou o modo construçăo.");
		} else {
			autorizados.add(player.getUniqueId());
			player.sendMessage("§aVocê ativou o modo construçăo.");
		}
	}

	/*
	@Command(name = "kit")
	public void kit(BukkitCommandSender sender, String label, String[] args) {
		if(!sender.isPlayer() || BukkitMain.getServerType() != ServerType.PVP_ARENA) return;

		val player = sender.getPlayer();
		if(args.length == 0) {
			val kits = new ArrayList<>(KitManager.getKits().keySet());

			val text = new TextComponent("Todos os Kits (" + kits.size() + "): ");
			text.setColor(ChatColor.GREEN);

			for (int i = 0; i < kits.size(); i++) {
				val kit = kits.
			}
		}

	}*/

}