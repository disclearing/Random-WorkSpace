package com.br.guilhermematthew.nowly.hardcoregames.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.br.guilhermematthew.nowly.hardcoregames.utility.HardcoreGamesUtility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.br.guilhermematthew.nowly.commons.bukkit.api.player.PlayerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.title.TitleAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.vanish.VanishAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Completer;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.StringUtils;
import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesOptions;
import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;
import com.br.guilhermematthew.nowly.hardcoregames.manager.kit.KitManager;
import com.br.guilhermematthew.nowly.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import com.br.guilhermematthew.nowly.hardcoregames.manager.structures.StructuresManager;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerCommand implements CommandClass {

	@Command(name = "spawn")
	public void spawn(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer())return;
		
		if (!HardcoreGamesMain.getGameManager().isPreGame()) {
			commandSender.sendMessage("§cO jogo já iniciou!");
			return;
		}
		
		if (HardcoreGamesMain.getTimerManager().getTime().get() <= 11) {
			commandSender.sendMessage("§cVocê não pode ir para o spawn agora!");
			return;
		}
		
		Player player = commandSender.getPlayer();
		
		player.teleport(HardcoreGamesUtility.getRandomLocation(30));
		
		player = null;
	}
	
	@Command(name = "specs", groupsToUse = {Groups.BETA})
	public void specs(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		
		Player player = commandSender.getPlayer();
		
		if (HardcoreGamesMain.getGameManager().isPreGame()) {
			player.sendMessage(StringUtils.COMMAND_SPECS_GAME_DONT_STARTED);
    		return;
    	}
		
		if (args.length != 1) {
			commandSender.sendMessage(StringUtils.COMMANDS_SPECS_USAGE);
			return;
		}
		
		if (args[0].equalsIgnoreCase("on")) {
			int vendo = 0;
			
			for (Player onlines : Bukkit.getOnlinePlayers()) {
				 if ((VanishAPI.inAdmin(onlines)) || (VanishAPI.isInvisible(onlines))) {
					 continue;
				 }
				 if (!GamerManager.getGamer(onlines.getUniqueId()).isPlaying()) {
					 vendo++;
					 player.showPlayer(onlines);
				 }
			}
			
			if (vendo != 0) {
			    player.sendMessage(StringUtils.SPECS_ATIVADOS);
			} else {
				player.sendMessage(StringUtils.NENHUM_SPEC_ENCONTRADO);
			}
		} else if (args[0].equalsIgnoreCase("off")) {
			int escondidos = 0;
			
			for (Player onlines : Bukkit.getOnlinePlayers()) {
				 if (!GamerManager.getGamer(onlines.getUniqueId()).isPlaying()) {
					 escondidos++;
					 player.hidePlayer(onlines);
				 }
			}
			
			if (escondidos != 0) {
			    player.sendMessage(StringUtils.SPECS_DESATIVADOS);
			} else {
				player.sendMessage(StringUtils.NENHUM_SPEC_ENCONTRADO);
			}
		} else {
			commandSender.sendMessage(StringUtils.COMMANDS_SPECS_USAGE);
		}
	}
	
	@Command(name = "desistir", groupsToUse = {Groups.BETA})
	public void desistir(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) return;
		
		if (HardcoreGamesMain.getGameManager().isPreGame()) {
			commandSender.sendMessage(StringUtils.COMMAND_DESISTIR_O_JOGO_AINDA_NAO_INICIOU);
    		return;
    	}
		
		Player player = commandSender.getPlayer();
		
		if (GamerManager.getGamer(player.getUniqueId()).isPlaying()) {
			player.sendMessage(StringUtils.COMMAND_DESISTIR_SUCESSO);
			
			PlayerAPI.dropItems(player, player.getLocation());
			
			HardcoreGamesMain.getGameManager().getGameType().setEspectador(player, false);
		} else {
			player.sendMessage(StringUtils.COMMAND_DESISTIR_NAO_ESTA_JOGANDO);
		}
		
		player = null;
	}
	
	@Command(name = "feast")
	public void feast(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) return;
		
		
		Player player = commandSender.getPlayer();
		
		if (HardcoreGamesMain.getGameManager().isPreGame()) {
			player.sendMessage(StringUtils.COMMAND_FEAST_O_JOGO_AINDA_NAO_INICIOU);
    		return;
    	}
    	if (!player.getInventory().contains(Material.COMPASS)) {
			player.sendMessage(StringUtils.COMMAND_FEAST_NAO_TEM_BUSSOLA);
    		return;
    	}
    	
    	if (StructuresManager.getFeast().getLocation() == null) {
    		player.sendMessage(StringUtils.COMMAND_FEAST_NAO_SPAWNOU);
    		return;
    	}
    	
    	player.setCompassTarget(StructuresManager.getFeast().getLocation());
    	player.sendMessage(StringUtils.COMMAND_FEAST_SUCESSO);
    	
    	player = null;
	}
	
	@Command(name = "kit")
	public void kit(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		
		Player player = commandSender.getPlayer();
		
		if (args.length == 0) {
			List<String> kits = null;
			
			if (!HardcoreGamesOptions.DOUBLE_KIT) {
				kits = KitManager.getPlayerKits(player).stream().map(Kit::getName).collect(Collectors.toList());
			} else {
				kits = KitManager.getAllKits().stream().map(Kit::getName).collect(Collectors.toList());
			}
    		
    		TextComponent text = new TextComponent("§a" + (HardcoreGamesOptions.DOUBLE_KIT ? "Todos os Kits" : "Seus Kits")
    				+ " (" + kits.size() + "): ");
    		
    		for (int i = 0; i < kits.size(); i++) {
    			 String kit = kits.get(i);
    			 text.addExtra(i == 0 ? "" : ", ");
    			 text.addExtra(buildKitComponent(KitManager.getKitInfo(kit.toLowerCase()), true));
    		}
    		
    		player.spigot().sendMessage(text);
    		player.sendMessage("");
    		
    		text = null;
    		
    		kits.clear();
    		kits = null;
		} else if (args.length == 1) {
    		String kit = args[0].toLowerCase();
    		
    		if (!KitManager.getKits().containsKey(kit)) {
    			player.sendMessage(StringUtils.COMMAND_KIT_NAO_EXISTE);
    			return;
    		}
    		
			if (HardcoreGamesOptions.KITS_DISABLEDS) {
				player.sendMessage(StringUtils.COMMAND_KIT_TODOS_KITS_DESATIVADOS);
				return;
			}
			if (KitManager.getKitsDesativados().contains(kit.toLowerCase())) {
				player.sendMessage(StringUtils.COMMAND_KIT_KIT_DESATIVADO);
			    return;
			}
			
			
			if (KitManager.isSameKit(kit, GamerManager.getGamer(player.getUniqueId()).getKit1())) {
				player.sendMessage(StringUtils.VOCE_NAO_PODE_PEGAR_DOIS_KITS_IGUAIS);
				return;
			}
			
			if (KitManager.isSameKit(kit, GamerManager.getGamer(player.getUniqueId()).getKit2())) {
				player.sendMessage(StringUtils.VOCE_NAO_PODE_PEGAR_DOIS_KITS_IGUAIS);
				return;
			}
			
			if (KitManager.hasCombinationOp(kit, GamerManager.getGamer(player.getUniqueId()).getKit2())) {
				player.sendMessage(StringUtils.VOCE_NAO_PODE_USAR_ESTA_COMBINACAO_DE_KIT);
				return;
			}
			
			boolean hasPermission = true;
			
			if (!HardcoreGamesOptions.DOUBLE_KIT) {
				hasPermission = KitManager.hasPermissionKit(player, kit, true);
			}
			
			if (!hasPermission) {
				return;
			}
			
			kit = KitManager.getKitInfo(kit).getName();
			
			if (HardcoreGamesMain.getGameManager().isPreGame()) {
				GamerManager.getGamer(player.getUniqueId()).setKit1(kit);
				
    			player.sendMessage(StringUtils.COMMAND_KIT_SUCESS.replace("%kit%", kit));
    			
    			TitleAPI.sendTitle(player, StringUtils.TITLE_ON_KIT_SELECTED.replace("%kit%", kit),
    					  StringUtils.SUBTITLE_ON_KIT_SELECTED, 0, 0, 5);
    			
    			HardcoreGamesScoreboard.getScoreBoardCommon().updateKit1(player, kit);
			} else {
				if (player.hasPermission("hardcoregames.pegarkit")) {
					if (HardcoreGamesMain.getTimerManager().getTime().get() <= 300) {
		 				if (GamerManager.getGamer(player.getUniqueId()).getKit1().equalsIgnoreCase("Nenhum")) {
		 		 			player.sendMessage(StringUtils.COMMAND_KIT_SUCESS.replace("%kit%", kit));
		 	    			
		 	    			TitleAPI.sendTitle(player, StringUtils.TITLE_ON_KIT_SELECTED.replace("%kit%", kit),
		 	    					  StringUtils.SUBTITLE_ON_KIT_SELECTED, 0, 0, 5);
		 	    			
		 					KitManager.handleKitSelect(player, true, kit);
		 				} else {
		 					player.sendMessage(StringUtils.COMMAND_KIT_VOCE_JA_ESTA_COM_KIT);
		 				}
					} else {
						player.sendMessage(StringUtils.COMMAND_KIT_TEMPO_ESGOTADO);
					}
				} else {
					player.sendMessage(StringUtils.COMMAND_KIT_SEM_PERM_PARA_PEGAR_DEPOIS_DO_JOGO_TER_INICIADO);
				}
			}
		}
	}
	
	@Command(name = "kit2")
	public void kit2(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		
		if (!HardcoreGamesOptions.DOUBLE_KIT) {
			commandSender.sendMessage("§cEsta partida está habilitada apenas o primeiro KIT.");
			return;
		}
		
		Player player = commandSender.getPlayer();
		
		if (args.length == 0) {
			List<String> kits = KitManager.getPlayerKits(player).stream().map(Kit::getName).collect(Collectors.toList());
			
    		TextComponent text = new TextComponent("§aSeus Kits (" + kits.size() + "): ");
    		
    		for (int i = 0; i < kits.size(); i++) {
    			 String kit = kits.get(i);
    			 text.addExtra(i == 0 ? "" : ", ");
    			 text.addExtra(buildKitComponent(KitManager.getKitInfo(kit.toLowerCase()), false));
    		}
    		
    		player.spigot().sendMessage(text);
    		player.sendMessage("");
    		
    		kits.clear();
		} else if (args.length == 1) {
    		String kit = args[0].toLowerCase();
    		
    		if (!KitManager.getKits().containsKey(kit)) {
    			player.sendMessage(StringUtils.COMMAND_KIT_NAO_EXISTE);
    			return;
    		}
    		
			if (HardcoreGamesOptions.KITS_DISABLEDS) {
				player.sendMessage(StringUtils.COMMAND_KIT_TODOS_KITS_DESATIVADOS);
				return;
			}
			if (KitManager.getKitsDesativados().contains(kit.toLowerCase())) {
				player.sendMessage(StringUtils.COMMAND_KIT_KIT_DESATIVADO);
			    return;
			}
			
			
			if (KitManager.isSameKit(kit, GamerManager.getGamer(player.getUniqueId()).getKit1())) {
				player.sendMessage(StringUtils.VOCE_NAO_PODE_PEGAR_DOIS_KITS_IGUAIS);
				return;
			}
			
			if (KitManager.isSameKit(kit, GamerManager.getGamer(player.getUniqueId()).getKit2())) {
				player.sendMessage(StringUtils.VOCE_NAO_PODE_PEGAR_DOIS_KITS_IGUAIS);
				return;
			}
			
			if (KitManager.hasCombinationOp(kit, GamerManager.getGamer(player.getUniqueId()).getKit1())) {
				player.sendMessage(StringUtils.VOCE_NAO_PODE_USAR_ESTA_COMBINACAO_DE_KIT);
				return;
			}
			
			if (!KitManager.hasPermissionKit(player, kit, true)) {
				return;
			}
			
			kit = KitManager.getKitInfo(kit).getName();
			
			if (HardcoreGamesMain.getGameManager().isPreGame()) {
				GamerManager.getGamer(player.getUniqueId()).setKit2(kit);
				
    			player.sendMessage(StringUtils.COMMAND_KIT_SUCESS.replace("%kit%", kit));
    			
    			TitleAPI.sendTitle(player, StringUtils.TITLE_ON_KIT_SELECTED.replace("%kit%", kit),
    					  StringUtils.SUBTITLE_ON_KIT_SELECTED, 0, 0, 5);
    			
    			HardcoreGamesScoreboard.getScoreBoardCommon().updateKit2(player, kit);
			} else {
				if (player.hasPermission("hardcoregames.pegarkit")) {
					if (HardcoreGamesMain.getTimerManager().getTime().get() <= 300) {
		 				if (GamerManager.getGamer(player.getUniqueId()).getKit2().equalsIgnoreCase("Nenhum")) {
		 		 			player.sendMessage(StringUtils.COMMAND_KIT_SUCESS.replace("%kit%", kit));
		 	    			
		 	    			TitleAPI.sendTitle(player, StringUtils.TITLE_ON_KIT_SELECTED.replace("%kit%", kit),
		 	    					  StringUtils.SUBTITLE_ON_KIT_SELECTED, 0, 0, 5);
		 	    			
		 					KitManager.handleKitSelect(player, false, kit);
		 				} else {
		 					player.sendMessage(StringUtils.COMMAND_KIT_VOCE_JA_ESTA_COM_KIT);
		 				}
					} else {
						player.sendMessage(StringUtils.COMMAND_KIT_TEMPO_ESGOTADO);
					}
				} else {
					player.sendMessage(StringUtils.COMMAND_KIT_SEM_PERM_PARA_PEGAR_DEPOIS_DO_JOGO_TER_INICIADO);
				}
			}
		}
	}
	
	@Completer(name = "kit", aliases = "kit2")
	public List<String> kitCompleter(BukkitCommandSender sender, String label, String[] args) {
		List<String> list = new ArrayList<>();
		
		if (args.length == 1) {
			String search = args[0].toLowerCase();
			for (Kit kit : KitManager.getAllKits()) {
				 if (kit.getName().toLowerCase().startsWith(search)) {
					 list.add(kit.getName());
				 }
			}
		}
		
		return list;
	}
	
	private BaseComponent buildKitComponent(Kit kit, boolean primary) {
		BaseComponent baseComponent = new TextComponent("§6" + kit.getName());
		BaseComponent descComponent = new TextComponent("§eDescrição: \n");
		for (String desc : kit.getDescription())
			descComponent.addExtra(desc.replaceAll("&", "§") + "\n");

		baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new BaseComponent[] { descComponent, new TextComponent("\n"), new TextComponent("§aClique para selecionar!") }));
		baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, (primary ? "/kit " : "/kit2 ") + kit.getName()));
		return baseComponent;
	}
}