package com.br.guilhermematthew.nowly.lobby.common.hologram.types;

import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.types.SimpleHologram;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.lobby.LobbyMain;
import com.br.guilhermematthew.nowly.lobby.common.hologram.HologramCommon;

import lombok.Getter;

public class LobbyPvPHologram extends HologramCommon {

	@Getter 
	private SimpleHologram kills, maxKillStreak;
	
	@Override
	public void create() {
		this.kills = new SimpleHologram("kills", "ARENA KILLS", DataCategory.KITPVP, DataType.PVP_KILLS, LobbyMain.getInstance());
		this.maxKillStreak = new SimpleHologram("killstreak", "ARENA MAXSTREAKS", DataCategory.KITPVP, DataType.PVP_MAXKILLSTREAK, LobbyMain.getInstance());
		
		this.kills.create();
		this.maxKillStreak.create();
		
		update();
	}
	
	public void update() {
		this.kills.updateValues();
		this.maxKillStreak.updateValues();
	}
}