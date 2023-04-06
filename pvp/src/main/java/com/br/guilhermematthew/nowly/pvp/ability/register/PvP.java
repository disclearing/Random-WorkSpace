package com.br.guilhermematthew.nowly.pvp.ability.register;

import com.br.guilhermematthew.nowly.pvp.ability.Kit;
import org.bukkit.entity.Player;

public class PvP extends Kit {

	public PvP() {
		initialize(getClass().getSimpleName());
	}
	
	protected void clean(Player player) {}
}