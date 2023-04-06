package com.br.guilhermematthew.nowly.login.commands;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import com.br.guilhermematthew.nowly.commons.bukkit.api.BukkitServerAPI;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.api.title.TitleAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.commands.BukkitCommandSender;
import com.br.guilhermematthew.nowly.commons.bukkit.manager.configuration.PluginConfiguration;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.utility.system.DateUtils;
import com.br.guilhermematthew.nowly.commons.custompackets.PacketType;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketCustomAction;
import com.br.guilhermematthew.nowly.login.LoginMain;
import com.br.guilhermematthew.nowly.login.LoginMessages;
import com.br.guilhermematthew.nowly.login.manager.gamer.Gamer;
import com.br.guilhermematthew.nowly.login.manager.gamer.Gamer.AuthenticationType;
import com.br.guilhermematthew.servercommunication.client.Client;

public class ServerCommand implements CommandClass {

	public static ArrayList<UUID> autorizados = new ArrayList<>();
	
	@Command(name = "login", aliases= {"logar"})
	public void login(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
		if (args.length != 1) {
			commandSender.sendMessage(LoginMessages.LOGIN_COMMAND_USAGE);
			return;
		}
		
		Player player = commandSender.getPlayer();
		
		if (!CommonsGeneral.getProfileManager().containsProfile(player.getUniqueId())) {
			player.kickPlayer("§cOcorreu um erro, tente novamente.");
			return;
		}
		
		Gamer gamer = LoginMain.getManager().getGamer(player);
		
		if (!gamer.isCaptchaConcluido()) {
			player.sendMessage(LoginMessages.VOCE_NAO_COMPLETOU_O_CAPTCHA);
			return;
		}
		
		if (gamer.getAuthenticationType() == AuthenticationType.REGISTRAR) {
			player.sendMessage(LoginMessages.VOCE_NAO_POSSUI_UM_REGISTRO);
			return;
		}
		
		if (gamer.isLogado()) {
			player.sendMessage(LoginMessages.VOCE_JA_ESTA_AUTENTICADO);
			return;
		}
		
		String senha = args[0];
		
		if (!format(senha).equalsIgnoreCase(BukkitMain.getBukkitPlayer(player.getUniqueId()).getString(DataType.REGISTRO_SENHA))) {
			gamer.setTentativasFalhas(gamer.getTentativasFalhas() + 1);
			
			if (gamer.getTentativasFalhas() == 3) {
				player.kickPlayer(LoginMessages.ERROU_A_SENHA_3_VEZES);
			} else {
				player.sendMessage(LoginMessages.SENHA_INCORRETA);
			}
			return;
		}
		
		//TitleAPI.sendTitle(player, "§aAutenticado!", "§faguarde...", 0, 0, 6);
		
		player.sendMessage(LoginMessages.LOGADO_COM_SUCESSO);
		player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
		
		gamer.setLogado(true);
		
		Client.getInstance().getClientConnection().sendPacket(new CPacketCustomAction(player.getName()).type(PacketType.BUKKIT_SEND_INFO).field("player-authenticated"));

		LoginMain.runLater(() -> {
			if (player.isOnline()) {
				BukkitServerAPI.redirectPlayer(player, "Lobby");
				//player.getInventory().setItem(0,
				//		new ItemBuilder().type(Material.COMPASS).name("§aClique para conectar-se ao Lobby").glow().build());
			}
		}, 20);
	}
	
	@Command(name = "register", aliases= {"registrar"})
	public void register(BukkitCommandSender commandSender, String label, String[] args) {
		if (!commandSender.isPlayer()) {
			return;
		}
	
		if (args.length != 2) {
			commandSender.sendMessage(LoginMessages.REGISTER_COMMAND_USAGE);
			return;
		}
		
		
		Player player = commandSender.getPlayer();
		
		if (!CommonsGeneral.getProfileManager().containsProfile(player.getUniqueId())) {
			player.kickPlayer("§cOcorreu um erro, tente novamente.");
			return;
		}
		
		Gamer gamer = LoginMain.getManager().getGamer(player);
		
		if (!gamer.isCaptchaConcluido()) {
			player.sendMessage(LoginMessages.VOCE_NAO_COMPLETOU_O_CAPTCHA);
			return;
		}
		
		if (gamer.getAuthenticationType() == AuthenticationType.LOGAR) {
			player.sendMessage(LoginMessages.VOCE_JA_POSSUI_UM_REGISTRO);
			return;
		}
		
		if (gamer.isLogado()) {
			player.sendMessage(LoginMessages.VOCE_JA_ESTA_AUTENTICADO);
			return;
		}
		
		String senha = args[0], 
				senha1 = args[1];
		
		if (senha.length() < 4) {
			player.sendMessage(LoginMessages.SENHA_MUITO_PEQUENA);
			return;
		}

		if (senha.length() > 20) {
			player.sendMessage(LoginMessages.SENHA_MUITO_GRANDE);
			return;
		}

		if (!senha.equals(senha1)) {
			player.sendMessage(LoginMessages.SENHA_NAO_SAO_IGUAIS);
			return;
		}
		
		String data = DateUtils.getActualDate(false);
		
		BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
		bukkitPlayer.set(DataType.REGISTRO_SENHA, format(senha));
		bukkitPlayer.set(DataType.REGISTRO_DATA, data);
		
		//TitleAPI.sendTitle(player, "§aRegistrado", "§fcom sucesso!", 0, 0, 6);
		
		player.sendMessage(LoginMessages.REGISTRADO_COM_SUCESSO);
		player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
		gamer.setLogado(true);

		registrar(player, format(senha), data);
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
				PluginConfiguration.setLocation(LoginMain.getInstance(), "spawn", player);
				LoginMain.setSpawn(PluginConfiguration.getLocation(LoginMain.getInstance(), "spawn"));
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
			player.setGameMode(GameMode.ADVENTURE);
		} else {
			autorizados.add(player.getUniqueId());
			player.sendMessage("§aVocê ativou o modo construçăo.");
			player.setGameMode(GameMode.CREATIVE);
		}
	}
	
	private void registrar(Player player, String senha, String dataRegistro) {
		Client.getInstance().getClientConnection().sendPacket(new CPacketCustomAction(player.getName()).type(PacketType.BUKKIT_SEND_INFO).field("player-authenticated"));
		
		LoginMain.runAsync(() -> {
			BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());

			bukkitPlayer.set(DataType.REGISTRO_SENHA, senha);
			bukkitPlayer.set(DataType.REGISTRO_DATA, dataRegistro);

			try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
				PreparedStatement insert = connection.prepareStatement(bukkitPlayer.
						getDataHandler().createInsertIntoStringQuery(DataCategory.REGISTER));
				
				insert.execute();
				insert.close();

				bukkitPlayer.getDataHandler().saveCategory(DataCategory.REGISTER);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
		
		LoginMain.runLater(() -> {
			if (player.isOnline()) {
				BukkitServerAPI.redirectPlayer(player, "Lobby");
				//player.getInventory().setItem(0,
				//		new ItemBuilder().type(Material.COMPASS).name("§aClique para conectar-se ao Lobby").glow().build());
			}
		}, 20);
	}
	
	public static void kickPlayer(Player player, String mensagem) {
		BukkitMain.runLater(() -> {player.kickPlayer(mensagem);});
	}
	
	public static String format(String string) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(string.getBytes());
		    StringBuffer sb = new StringBuffer();
		    for (int i = 0; i < array.length; i++) {
		         sb.append(Integer.toHexString(array[i] & 0xFF | 0x100).substring(1, 3));
		    }
		    return sb.toString();
		} catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {}
		return null;
	}
}