package com.br.guilhermematthew.nowly.login.manager.gamer;

import java.util.UUID;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Gamer {

	private Player player;
	private String nick;
	private UUID uniqueId;
	private boolean captchaConcluido, logado;
	private int tentativasFalhas, secondsCaptcha, secondsLogin, secondsConnect;
	private Long timestamp;
	
	private AuthenticationType authenticationType;
	
	public Gamer(Player player, AuthenticationType authenticationType) {
		setPlayer(player);
		setNick(player.getName());
		
		setAuthenticationType(authenticationType);
		
		setUniqueId(player.getUniqueId());
		
		setCaptchaConcluido(false);
		setLogado(false);
		setTentativasFalhas(0);
		
		setSecondsCaptcha(-2);
		setSecondsLogin(-1);
		setSecondsConnect(-1);
		
		setTimestamp(System.currentTimeMillis());
	}
	
	public void handleLogin(Player player) {
		setPlayer(player);
		
		refresh();
	}

	public void refresh() {
		setCaptchaConcluido(false);
		setLogado(false);
		setTentativasFalhas(0);
		setSecondsCaptcha(-2);
		setSecondsLogin(-1);
		setSecondsConnect(-1);
	}

	public void addSecondsCaptcha() {
		setSecondsCaptcha(getSecondsCaptcha() + 1);
	}

	public void addSecondsLogin() {
		setSecondsLogin(getSecondsLogin() + 1);
	}

	public void addSecondsConnect() {
		setSecondsConnect(getSecondsConnect() + 1);
	}
	
	public enum AuthenticationType {
		REGISTRAR, LOGAR
	}
}