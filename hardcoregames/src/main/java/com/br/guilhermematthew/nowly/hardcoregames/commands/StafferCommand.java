package com.br.guilhermematthew.nowly.hardcoregames.commands;

import java.util.*;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.bukkit.api.player.PlayerAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.bukkit.worldedit.WorldEditAPI;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import com.br.guilhermematthew.nowly.commons.common.utility.system.DateUtils;
import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.StringUtils;
import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesOptions;
import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.Gamer;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;
import com.br.guilhermematthew.nowly.hardcoregames.manager.kit.KitManager;
import com.br.guilhermematthew.nowly.hardcoregames.manager.structures.StructuresManager;
import com.br.guilhermematthew.nowly.hardcoregames.utility.InventoryStore;

public class StafferCommand implements CommandClass {

	private final HashMap<String, InventoryStore> Skits = new HashMap<>();
	
	@Command(name = "reviver", aliases= {"respawn"}, groupsToUse= {Groups.ADMIN})
	public void reviver(BukkitCommandSender commandSender, String label, String[] args) {
		if (args.length != 1) {
			commandSender.sendMessage("§cUtilize: /reviver <Nick>");
			return;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		if (target != null) { 
			if (!GamerManager.getGamer(target.getUniqueId()).isPlaying()) {
				HardcoreGamesMain.getGameManager().getGameType().setGamer(target);
				
				commandSender.sendMessage("§aVocê reviveu o jogador §7" + target.getName());
				target.sendMessage("§aVocê foi revivido!");
			} else {
				commandSender.sendMessage("§cEste jogador não está eliminado.");
			}
		} else {
			commandSender.sendMessage("§cJogador offline.");
		}
	}
	
	@Command(name = "hginfo", groupsToUse= {Groups.ADMIN})
	public void hginfo(BukkitCommandSender commandSender, String label, String[] args) {
		commandSender.sendMessage("");
		
		int vivos = 0, onlines = 0, espectando = 0, relog = 0;
		
		for (Gamer gamers : GamerManager.getGamers()) {
			 onlines++;
			
			 if (gamers.isPlaying()) {
				 vivos++;
			 }
			 
			 if (gamers.isOnline() && !gamers.isPlaying()) {
				 espectando++;
			 }
			 
			 if (gamers.isRelogar()) {
				 relog++;
			 }
		}
		
		commandSender.sendMessage("");
		commandSender.sendMessage("§fJogadores jogando: §a" + vivos);
		commandSender.sendMessage("§fJogadores espectando: §a" + espectando);
		commandSender.sendMessage("§fJogadores para relogar: §a" + relog);
		commandSender.sendMessage("§fJogadores online: §a" + onlines);
		commandSender.sendMessage("");
	}
	
	@Command(name = "toggle", groupsToUse= {Groups.MOD_PLUS})
	public void toggle(BukkitCommandSender commandSender, String label, String[] args) {
		if (args.length == 0) {
			commandSender.sendMessage("§cUse /toggle <drops/break/place/feast/minifeast> <on/off>");
			commandSender.sendMessage("§cUse /toggle kit <kit> <on/off>");
			commandSender.sendMessage("§cUse /toggle kit * <on/off> - para desativar/ativar todos os kits.");
    	} else if (args.length == 2) {
			if(args[0].equalsIgnoreCase("doublekit")) {
				if(args[1].equalsIgnoreCase("on")) {
					HardcoreGamesOptions.DOUBLE_KIT = true;
					HardcoreGamesScoreboard.init();

					if(HardcoreGamesMain.getGameManager().isGaming()) return;

					Bukkit.getOnlinePlayers().forEach(player -> {

						ItemBuilder itemBuilder = new ItemBuilder();

						PlayerInventory playerInventory = player.getInventory();

						playerInventory.clear();
						playerInventory.setArmorContents(null);

						playerInventory.setItem(0, itemBuilder.type(Material.CHEST).name("§aEscolher Kit").build());

						if (HardcoreGamesOptions.DOUBLE_KIT) {
							playerInventory.setItem(1, itemBuilder.type(Material.CHEST).amount(2).name("§aEscolher Kit 2").build());
						}

						playerInventory.addItem(itemBuilder.type(Material.EMERALD).name("§6Loja de Kits").build());

						playerInventory.setItem(8, itemBuilder.type(Material.BED).name("§cVoltar ao Lobby").build());
					});
				} else if(args[1].equalsIgnoreCase("off")) {
					GamerManager.getGamers().forEach(gamer -> gamer.setKit2("Nenhum"));

					HardcoreGamesOptions.DOUBLE_KIT = false;
					HardcoreGamesScoreboard.init();

					if(HardcoreGamesMain.getGameManager().isGaming()) return;

					Bukkit.getOnlinePlayers().forEach(player -> {

						ItemBuilder itemBuilder = new ItemBuilder();

						PlayerInventory playerInventory = player.getInventory();

						playerInventory.clear();
						playerInventory.setArmorContents(null);

						playerInventory.setItem(0, itemBuilder.type(Material.CHEST).name("§aEscolher Kit").build());

						if (HardcoreGamesOptions.DOUBLE_KIT) {
							playerInventory.setItem(1, itemBuilder.type(Material.CHEST).amount(2).name("§aEscolher Kit 2").build());
						}

						playerInventory.addItem(itemBuilder.type(Material.EMERALD).name("§6Loja de Kits").build());

						playerInventory.setItem(8, itemBuilder.type(Material.BED).name("§cVoltar ao Lobby").build());
					});
				}
			} else if (args[0].equalsIgnoreCase("feast")) {
				if (args[1].equalsIgnoreCase("on")) {
					if (!HardcoreGamesOptions.FEAST) {
						HardcoreGamesOptions.FEAST = true;
						Bukkit.broadcastMessage(StringUtils.FEAST_ATIVADO);
					} else {
						commandSender.sendMessage(StringUtils.FEAST_JA_ATIVADO);
					}
				} else if (args[1].equalsIgnoreCase("off")) {
					if (HardcoreGamesOptions.FEAST) {
						Bukkit.broadcastMessage(StringUtils.FEAST_DESATIVADO);
						HardcoreGamesOptions.FEAST = false;
					} else {
						commandSender.sendMessage(StringUtils.FEAST_JA_DESATIVADO);
					}
				}
    		} else if (args[0].equalsIgnoreCase("minifeast")) {
				if (args[1].equalsIgnoreCase("on")) {
					if (!HardcoreGamesOptions.MINIFEAST) {
						HardcoreGamesOptions.MINIFEAST = true;
						Bukkit.broadcastMessage(StringUtils.MINIFEAST_ATIVADO);
					} else {
						commandSender.sendMessage(StringUtils.MINIFEAST_JA_ATIVADO);
					}
				} else if (args[1].equalsIgnoreCase("off")) {
					if (HardcoreGamesOptions.MINIFEAST) {
						HardcoreGamesOptions.MINIFEAST = false;
						Bukkit.broadcastMessage(StringUtils.MINIFEAST_DESATIVADO);
					} else {
						commandSender.sendMessage(StringUtils.MINIFEAST_JA_DESATIVADO);
					}
				}
    		} else if (args[0].equalsIgnoreCase("drops")) {
				if (args[1].equalsIgnoreCase("on")) {
					if (!HardcoreGamesOptions.DROP_OPTION) {
						HardcoreGamesOptions.DROP_OPTION = true;
						Bukkit.broadcastMessage(StringUtils.DROPS_ATIVADO);
					} else {
						commandSender.sendMessage(StringUtils.DROPS_JA_ATIVADO);
					}
				} else if (args[1].equalsIgnoreCase("off")) {
					if (HardcoreGamesOptions.DROP_OPTION) {
						HardcoreGamesOptions.DROP_OPTION = false;
						Bukkit.broadcastMessage(StringUtils.DROPS_DESATIVADO);
					} else {
						commandSender.sendMessage(StringUtils.DROPS_JA_DESATIVADO);
					}
				}
			} else if (args[0].equalsIgnoreCase("break")) {
				if (args[1].equalsIgnoreCase("on")) {
					if (!HardcoreGamesOptions.BREAK_OPTION) {
						HardcoreGamesOptions.BREAK_OPTION = true;
						Bukkit.broadcastMessage(StringUtils.BREAK_ATIVADO);
					} else {
						commandSender.sendMessage(StringUtils.BREAK_JA_ATIVADO);
					}
				} else if (args[1].equalsIgnoreCase("off")) {
					if (HardcoreGamesOptions.BREAK_OPTION) {
						HardcoreGamesOptions.BREAK_OPTION = false;
						Bukkit.broadcastMessage(StringUtils.BREAK_DESATIVADO);
					} else {
						commandSender.sendMessage(StringUtils.BREAK_JA_DESATIVADO);
					}
				}
			} else if (args[0].equalsIgnoreCase("place")) {
				if (args[1].equalsIgnoreCase("on")) {
					if (!HardcoreGamesOptions.PLACE_OPTION) {
						HardcoreGamesOptions.PLACE_OPTION = true;
						Bukkit.broadcastMessage(StringUtils.PLACE_ATIVADO);
					} else {
						commandSender.sendMessage(StringUtils.PLACE_JA_ATIVADO);
					}
				} else if (args[1].equalsIgnoreCase("off")) {
					if (HardcoreGamesOptions.PLACE_OPTION) {
						HardcoreGamesOptions.PLACE_OPTION = false;
						Bukkit.broadcastMessage(StringUtils.PLACE_DESATIVADO);
					} else {
						commandSender.sendMessage(StringUtils.PLACE_JA_DESATIVADO);
					}
				}
			} else {
				commandSender.sendMessage("§cUse /toggle <drops/break/place> <on/off>");
				commandSender.sendMessage("§cUse /toggle kit <kit> <on/off>");
				commandSender.sendMessage("§cUse /toggle kit * <on/off> - para desativar/ativar todos os kits.");
			}
		} else if (args.length == 3) {
			if (args[0].equalsIgnoreCase("kit")) {
				String s = args[1].toLowerCase();

				if (s.equalsIgnoreCase("*")) {
					if (args[2].equalsIgnoreCase("off")) {
						for (Kit allKits : KitManager.getAllKits()) {
							 if (!KitManager.getKitsDesativados().contains(allKits.getName().toLowerCase())) {
								 KitManager.getKitsDesativados().add(allKits.getName().toLowerCase());
							 }
						}
						
						for (Player on : Bukkit.getOnlinePlayers()) {
							 KitManager.removeKits(on, true);
						}
						
						Bukkit.broadcastMessage(StringUtils.TODOS_OS_KITS_FORAM_DESATIVADOS);
					} else if (args[2].equalsIgnoreCase("on")) {
						KitManager.getKitsDesativados().clear();

						Bukkit.broadcastMessage(StringUtils.TODOS_OS_KITS_FORAM_ATIVADOS);
					} else {
						commandSender.sendMessage("§cUse /toggle <drops/break/place> <on/off>");
						commandSender.sendMessage("§cUse /toggle kit <kit> <on/off>");
						commandSender.sendMessage("§cUse /toggle kit * <on/off> - para desativar/ativar todos os kits.");
					}
				} else if (KitManager.getKits().containsKey(s)) {
					s = Objects.requireNonNull(KitManager.getKitInfo(s)).getName();
					
					if (args[2].equalsIgnoreCase("on")) {
						if (KitManager.getKitsDesativados().contains(s.toLowerCase())) {
							KitManager.getKitsDesativados().remove(s.toLowerCase());
							
							commandSender.sendMessage(StringUtils.KIT_ATIVADO.replace("%kit%", s));
						} else {
							commandSender.sendMessage(StringUtils.KIT_JA_ESTA_ATIVADO.replace("%kit%", s));
						}
					} else if (args[2].equalsIgnoreCase("off")) {
						if (KitManager.getKitsDesativados().contains(s.toLowerCase())) {
							commandSender.sendMessage(StringUtils.KIT_JA_ESTA_DESATIVADO.replace("%kit%", s));
						} else {
							KitManager.getKitsDesativados().add(s.toLowerCase());
							commandSender.sendMessage(StringUtils.KIT_DESATIVADO.replace("%kit%", s));
							
							for (Player on : Bukkit.getOnlinePlayers()) {
								 KitManager.removeIfContainsKit(on, s);
							}		
						}
					}
				} else {
					commandSender.sendMessage(StringUtils.COMMAND_KIT_NAO_EXISTE);
				}
			}
		} else {
			commandSender.sendMessage("§cUse /toggle <drops/break/place> <on/off>");
			commandSender.sendMessage("§cUse /toggle kit <kit> <on/off>");
			commandSender.sendMessage("§cUse /toggle kit * <on/off> - para desativar/ativar todos os kits.");
		}
	}
	
	@Command(name = "fkit", aliases= {"forcekit"}, groupsToUse= {Groups.MOD_PLUS})
	public void fkit1(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		
		if (args.length != 2) {
    		commandSender.sendMessage(StringUtils.COMMAND_FORCEKIT_USAGE);
    		return;
    	}
		
    	String kit = args[0].toLowerCase(), 
    			nick = args[1];
    	
    	if (!KitManager.getKits().containsKey(kit)) {
    		commandSender.sendMessage(StringUtils.COMMAND_FORCEKIT_KIT_NAO_EXISTE);
    		return;
    	}
    	
    	kit = Objects.requireNonNull(Objects.requireNonNull(KitManager.getKitInfo(kit))).getName();
    	
    	if ((nick.equalsIgnoreCase("todos")) || (nick.equals("*"))) {
    		
    		if (HardcoreGamesMain.getGameManager().isPreGame()) {
    			for (Player player : Bukkit.getOnlinePlayers()) {
    				 Gamer gamer = GamerManager.getGamer(player.getUniqueId());
    				 if (gamer == null) {
					     continue;
				     }
				     gamer.setKit1(kit);
    			}
    		} else {
    			for (Player player : Bukkit.getOnlinePlayers()) {
    				KitManager.handleKitSelect(player, true, kit);
    			}
    		}
     		commandSender.sendMessage(StringUtils.COMMAND_FORCEKIT_SUCESS_ALL_PLAYERS.replace("%kit%", kit));
    	} else {
    		Player target = Bukkit.getPlayer(nick);
    		if (target == null) {
    			commandSender.sendMessage(BukkitMessages.JOGADOR_OFFLINE);
    			return;
    		}
    		
    		if (HardcoreGamesMain.getGameManager().isPreGame()) {
    			GamerManager.getGamer(target.getUniqueId()).setKit1(kit);
    		} else {
    			KitManager.handleKitSelect(target, true, kit);
    		}
    		commandSender.sendMessage(StringUtils.COMMAND_FORCEKIT_SUCESS_PLAYER.replace("%kit%", kit).replace("%nick%", target.getName()));
    	}
	}
	
	@Command(name = "fkit2", aliases= {"forcekit2"}, groupsToUse= {Groups.MOD_PLUS})
	public void fkit2(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		
		if (args.length != 2) {
    		commandSender.sendMessage(StringUtils.COMMAND_FORCEKIT_USAGE);
    		return;
    	}
		
		if (!HardcoreGamesOptions.DOUBLE_KIT) {
			commandSender.sendMessage("§cEsta partida está habilitada apenas o primeiro KIT.");
			return;
		}
		
    	String kit = args[0].toLowerCase(), 
    			nick = args[1];
    	
    	if (!KitManager.getKits().containsKey(kit)) {
    		commandSender.sendMessage(StringUtils.COMMAND_FORCEKIT_KIT_NAO_EXISTE);
    		return;
    	}
    	
    	kit = Objects.requireNonNull(Objects.requireNonNull(KitManager.getKitInfo(kit))).getName();
    	
    	if ((nick.equalsIgnoreCase("todos")) || (nick.equals("*"))) {
    		
    		if (HardcoreGamesMain.getGameManager().isPreGame()) {
    			for (Player player : Bukkit.getOnlinePlayers()) {
    				 Gamer gamer = GamerManager.getGamer(player.getUniqueId());
    				 if (gamer == null) {
					     continue;
				     }
				     gamer.setKit2(kit);
    			}
    		} else {
    			for (Player player : Bukkit.getOnlinePlayers()) {
    				KitManager.handleKitSelect(player, false, kit);
    			}
    		}
     		commandSender.sendMessage(StringUtils.COMMAND_FORCEKIT_SUCESS_ALL_PLAYERS.replace("%kit%", kit));
    	} else {
    		Player target = Bukkit.getPlayer(nick);
    		if (target == null) {
    			commandSender.sendMessage(BukkitMessages.JOGADOR_OFFLINE);
    			return;
    		}
    		
    		if (HardcoreGamesMain.getGameManager().isPreGame()) {
    			GamerManager.getGamer(target.getUniqueId()).setKit2(kit);
    		} else {
    			KitManager.handleKitSelect(target, false, kit);
    		}
    		commandSender.sendMessage(StringUtils.COMMAND_FORCEKIT_SUCESS_PLAYER.replace("%kit%", kit).replace("%nick%", target.getName()));
    	}
	}
	
	@SuppressWarnings("deprecation")
	@Command(name = "skit", aliases= {"simplekit"}, groupsToUse= {Groups.MOD})
	public void skit(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		
		if (args.length == 0) {
			commandSender.sendMessage(StringUtils.COMMAND_SKIT_USAGE);
    	} else if (args.length == 1) {
    		if (args[0].equalsIgnoreCase("lista")) {
    			if (Skits.size() == 0) {
    				commandSender.sendMessage(StringUtils.COMMAND_SKIT_NENHUM_CRIADO);
    				return;
    			}
    					
    			StringBuilder skits = new StringBuilder();
    			for (String kits : Skits.keySet()) {
    				 if (skits.toString().equals("")) {
    					 if (Skits.size() == 1) {
    						 skits = new StringBuilder(kits);
    						 break;
    					 } else {
    						 skits = new StringBuilder(kits);
    					 }
    				 } else {
    					 skits.append(",").append(kits);
    				 }
    			}	
    			commandSender.sendMessage(StringUtils.COMMAND_SKIT_LISTA + skits);
    		} else {
    			commandSender.sendMessage(StringUtils.COMMAND_SKIT_USAGE);
    		}
    	} else if (args.length == 2) {
    		if (!args[0].equalsIgnoreCase("criar")) {
    			commandSender.sendMessage(StringUtils.COMMAND_SKIT_USAGE);
    			return;
    		}
    		String kit = args[1];
    		if (Skits.containsKey(kit)) {
    			commandSender.sendMessage(StringUtils.COMMAND_SKIT_JA_EXISTE);
    			return;
    		}
    		Player player = commandSender.getPlayer();
    		
    		Skits.put(kit, new InventoryStore(kit, 
    				player.getInventory().getArmorContents(), 
    				player.getInventory().getContents(),
    				(List<PotionEffect>) player.getActivePotionEffects()));
    		
    		commandSender.sendMessage(StringUtils.COMMAND_SKIT_CRIADO.replace("%nome%", kit));
    	} else if (args.length == 3) {
    		if (!args[0].equalsIgnoreCase("aplicar")) {
    			commandSender.sendMessage(StringUtils.COMMAND_SKIT_USAGE);
    			return;
    		}
    		String kit = args[1];
    		if (!Skits.containsKey(kit)) {
    			commandSender.sendMessage(StringUtils.COMMAND_SKIT_NAO_EXISTE);
    			return;
    		}
    		
			InventoryStore inv = Skits.get(kit);
			
    		if ((args[2].equalsIgnoreCase("todos")) || (args[2].equalsIgnoreCase("*"))) {
    			for (Player ons : Bukkit.getOnlinePlayers()) {
    				 Gamer gamer = GamerManager.getGamer(ons.getUniqueId());
    				 
    				 if (gamer.isPlaying()) {
    					 ons.getPlayer().setItemOnCursor(new ItemStack(0));
    					 ons.getInventory().setArmorContents(inv.getArmor());
    					 ons.getInventory().setContents(inv.getInv());
    					 ons.addPotionEffects(inv.getPotions());
    					 
    					 if (ons.getInventory().contains(Material.WOOL)) {
    						 ons.getInventory().setItem(ons.getInventory().first(Material.WOOL), null);
    						 KitManager.giveItensKit(ons, gamer.getKit1());
    					 }
    					 
    					 if (!PlayerAPI.isFull(ons.getInventory())) {
    						 ons.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
    					 }
    				 }
				}
    			commandSender.sendMessage(StringUtils.COMMAND_SKIT_SUCESSO_ALL_PLAYERS.replace("%nome%", kit));
    		} else {
    			Player target = Bukkit.getPlayer(args[2]);
    			if (target == null) {
    				commandSender.sendMessage(BukkitMessages.JOGADOR_OFFLINE);
    				return;
    			}
    			
    			target.getPlayer().setItemOnCursor(new ItemStack(0));
    			target.getInventory().setArmorContents(inv.getArmor());
    			target.getInventory().setContents(inv.getInv());
    			target.addPotionEffects(inv.getPotions());
				
				if (target.getInventory().contains(Material.WOOL)) {
					target.getInventory().setItem(target.getInventory().first(Material.WOOL), null);
					KitManager.giveItensKit(target, GamerManager.getGamer(target.getUniqueId()).getKit1());
				}
				
				target.updateInventory();
				
				if (!PlayerAPI.isFull(target.getInventory())) {
					target.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
					target.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
					target.updateInventory();
				}
				
    			commandSender.sendMessage(StringUtils.COMMAND_SKIT_SUCESSO_PLAYER.replace("%nome%", kit).replace("%nick%", target.getName()));
    		}
    	} else {
			commandSender.sendMessage(StringUtils.COMMAND_SKIT_USAGE);
    	}	
	}
	
	@Command(name = "start", aliases= {"iniciar"}, groupsToUse= {Groups.MOD})
	public void start(BukkitCommandSender commandSender, String label, String[] args) {
		if (HardcoreGamesMain.getGameManager().isPreGame()) {
			HardcoreGamesMain.getGameManager().getGameType().start();
		} else {
			commandSender.sendMessage(StringUtils.COMMAND_START_FAILED);
		}
	}
	
	@Command(name = "tempo", aliases= {"t"}, groupsToUse= {Groups.MOD})
	public void tempo(BukkitCommandSender commandSender, String label, String[] args) {
		if (args.length != 1) {
			commandSender.sendMessage(StringUtils.COMMAND_TEMPO_USAGE);
			return;
		}
		
		if (!StringUtility.isInteger(args[0])) {
			commandSender.sendMessage(StringUtils.COMMAND_TEMPO_USAGE);
			return;
		}
		
		int segundos = Integer.parseInt(args[0]);
		
		if (segundos <= 0) {
			commandSender.sendMessage(StringUtils.COMMAND_TEMPO_USAGE);
			return;
		}
		
		HardcoreGamesMain.getTimerManager().updateTime(segundos);
		
		Bukkit.broadcastMessage(StringUtils.TEMPO_CHANGED.replace("%tempo%", DateUtils.formatTime(segundos)));
	}
	
	@Command(name = "ffeast", aliases= {"forcefeast"}, groupsToUse= {Groups.MOD_PLUS})
	public void ffeast(BukkitCommandSender commandSender, String label, String[] args) {
		if(!HardcoreGamesMain.getGameManager().isGaming()) {
			commandSender.sendMessage(ChatColor.RED + " * A partida ainda não iniciou ou está no tempo de invencibilidade.");
			return;
		}

		if(StructuresManager.getFeast().isSpawned()) new PlayerCommand().feast(commandSender, label, args);
		else StructuresManager.getFeast().createFeast(StructuresManager.getValidLocation(true));
	}
	
	@Command(name = "arena", groupsToUse= {Groups.MOD})
	public void arena(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		
		if (args.length == 0) {
			commandSender.sendMessage(StringUtils.COMMAND_ARENA_USAGE);
		} else if (args.length == 1) {
    		if (args[0].equalsIgnoreCase("limpar")) {
    			limparArena(commandSender.getPlayer());
    		} else if (args[0].equalsIgnoreCase("final")) {
    			StructuresManager.getFinalBattle().create();
    		} else {
    			commandSender.sendMessage(StringUtils.COMMAND_ARENA_USAGE);
    		}
		} else if (args.length == 2) {
    		String largura = args[0], altura = args[1];
    		if ((!StringUtility.isInteger(largura)) || (!StringUtility.isInteger(altura))) {
    			commandSender.sendMessage(StringUtils.COMMAND_ARENA_USAGE);
    			return;
    		}
    		criarArena(commandSender.getPlayer(), commandSender.getPlayer().getLocation(), Integer.parseInt(largura), Integer.parseInt(altura));
		} else {
			commandSender.sendMessage(StringUtils.COMMAND_ARENA_USAGE);
		}
	}
	
	private static Location ponto_baixo, ponto_alto;
	
	public static void criarArena(Player p, final Location loc, int largura, int altura) {
		if (p != null) {
			p.sendMessage(StringUtils.CREATING_ARENA);
		}
		
        List<Location> cuboid = new ArrayList<>();
        
    	for (int bX = -largura; bX <= largura; bX++) {
             for (int bZ = -largura; bZ <= largura; bZ++) {
                  for (int bY = -1; bY <= altura; bY++) {
         	           if ((bY == altura) || (bY == -1)) {
        	                cuboid.add(loc.clone().add(bX, bY, bZ));
                       } else if ((bX == -largura) || (bZ == -largura) || (bX == largura) || (bZ == largura)) {
                            cuboid.add(loc.clone().add(bX, bY, bZ));
                       }
                  }
             }
    	}
    	
    	
		new BukkitRunnable() {
			
	    	boolean ended = false;
		  	int blockAtual = 0;
			final int max = cuboid.size() + 10;
	    	final int blocksPerTick = largura >= 30 ? 60 : 100;
		  	final Random random = new Random();
	    	
		  	@SuppressWarnings("deprecation")
			public void run() {
		  		if (ended) {
		  			cancel();
		  			if (p != null && p.isOnline()) {
		  				p.sendMessage(StringUtils.ARENA_CREATED);
		  			}
		  			cuboid.clear();
		  			return;
		  		}
			
		  		if (blockAtual >= max) {
		  			ended = true;
		  			return;
		  		}
			
		  		for (int i = 0; i < blocksPerTick; i++) {
		  			 try {
		  				 Location location = cuboid.get(blockAtual + i);
		  				 
		  				 if (location.getBlockY() == altura) {
		  					 WorldEditAPI.setAsyncBlock(location.getWorld(), location, random.nextBoolean() ? Material.BEDROCK.getId() : Material.GLOWSTONE.getId());
		  				 } else {
			  				 WorldEditAPI.setAsyncBlock(location.getWorld(), location, Material.BEDROCK.getId());
		  				 }
		  			 } catch (IndexOutOfBoundsException | NullPointerException e) {
						   e.printStackTrace();
					 }
				}
		  		this.blockAtual+=55;
		  	}
		}.runTaskTimer(BukkitMain.getInstance(), 1L, 1L);

		ponto_baixo = loc.clone().add(largura - 1, 0, largura - 1);
        Location PA = loc.clone().subtract(largura - 1, 0, largura - 1);
        PA.add(0, altura - 1, 0);
        ponto_alto = PA;
	}
	
	@SuppressWarnings("deprecation")
	public void limparArena(Player p) {
		if (ponto_alto == null) {
			p.sendMessage(StringUtils.ARENA_NOT_CLEANED);
			return;
		}
		
		for (Location location : getLocationsFromTwoPoints(ponto_baixo, ponto_alto)) {
			 WorldEditAPI.setAsyncBlock(location.getWorld(), location, Material.AIR.getId());
		}
		
		p.sendMessage(StringUtils.ARENA_CLEANED);
	}
	
	public List<Location> getLocationsFromTwoPoints(Location location1, Location location2) {
		List<Location> locations = new ArrayList<>();
		int topBlockX = (Math.max(location1.getBlockX(), location2.getBlockX()));
		int bottomBlockX = (Math.min(location1.getBlockX(), location2.getBlockX()));
		int topBlockY = (Math.max(location1.getBlockY(), location2.getBlockY()));
		int bottomBlockY = (Math.min(location1.getBlockY(), location2.getBlockY()));
		int topBlockZ = (Math.max(location1.getBlockZ(), location2.getBlockZ()));
		int bottomBlockZ = (Math.min(location1.getBlockZ(), location2.getBlockZ()));
		for (int x = bottomBlockX; x <= topBlockX; x++)
			for (int z = bottomBlockZ; z <= topBlockZ; z++)
				for (int y = bottomBlockY; y <= topBlockY; y++)
		     		locations.add(new Location(location1.getWorld(), x, y, z));

		return locations;
	}
	
	public List<Block> getBlocks(Location location1, Location location2) {
		List<Block> blocks = new ArrayList<>();
		for (Location loc : getLocationsFromTwoPoints(location1, location2)) {
			 Block b = loc.getBlock();
			 if (!b.getType().equals(Material.AIR))
			     blocks.add(b);
		}
		return blocks;
	}
}