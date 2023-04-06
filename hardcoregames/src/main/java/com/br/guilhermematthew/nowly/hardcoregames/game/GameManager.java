package com.br.guilhermematthew.nowly.hardcoregames.game;

import com.br.guilhermematthew.nowly.commons.common.serverinfo.enums.GameStages;
import com.br.guilhermematthew.nowly.hardcoregames.base.GameType;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GameManager {

	private GameStages stage;
	private GameType gameType;
	
	public GameManager() {
		this.stage = GameStages.LOADING;
	}
	
	public boolean isLoading() {
		return stage.equals(GameStages.LOADING);
	}
	
	public boolean isPreGame() {
		return stage.equals(GameStages.WAITING);
	}
	
	public boolean isInvencibilidade() {
		return stage.equals(GameStages.INVINCIBILITY);
	}
	
	public boolean isGaming() {
		return stage.equals(GameStages.PLAYING);
	}
	
	public boolean isEnd() {
		return stage.equals(GameStages.END);
	}

	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}

	public boolean canLogin() {
		return !isLoading() && !isEnd();
	}
}