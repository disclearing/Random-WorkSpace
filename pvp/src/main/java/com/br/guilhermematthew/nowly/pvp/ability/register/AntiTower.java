package com.br.guilhermematthew.nowly.pvp.ability.register;

import com.br.guilhermematthew.nowly.pvp.ability.Kit;
import org.bukkit.entity.Player;

public class AntiTower extends Kit {

	public AntiTower() {
		initialize(getClass().getSimpleName());
	}
	
	protected void clean(Player player) {}
}