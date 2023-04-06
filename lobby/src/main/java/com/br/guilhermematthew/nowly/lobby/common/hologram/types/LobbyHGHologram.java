package com.br.guilhermematthew.nowly.lobby.common.hologram.types;

import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.types.SimpleHologram;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.lobby.LobbyMain;
import com.br.guilhermematthew.nowly.lobby.common.hologram.HologramCommon;

import lombok.Getter;

public class LobbyHGHologram extends HologramCommon {

	@Getter 
	private SimpleHologram kills, wins, killsEvent, winsEvent;
	
	@Override
	public void create() {
		this.kills = new SimpleHologram("kills", "KILLS HG", DataCategory.HARDCORE_GAMES, DataType.HG_KILLS, LobbyMain.getInstance());
		this.wins = new SimpleHologram("wins", "WINS HG", DataCategory.HARDCORE_GAMES, DataType.HG_WINS, LobbyMain.getInstance());
		
		this.killsEvent = new SimpleHologram("killsEvent", "KILLS SET-HG", DataCategory.HARDCORE_GAMES, DataType.HG_EVENT_KILLS, LobbyMain.getInstance());
		this.winsEvent = new SimpleHologram("winsEvent", "WINS SET-HG", DataCategory.HARDCORE_GAMES, DataType.HG_EVENT_WINS, LobbyMain.getInstance());
		
		this.kills.create();
		this.wins.create();
		
		this.winsEvent.create();
		this.killsEvent.create();
		
		update();
	}
	
	public void update() {
		this.kills.updateValues();
		this.wins.updateValues();
		
		this.killsEvent.updateValues();
		this.winsEvent.updateValues();
	}
	
	public void recreate() {
		
	}
}