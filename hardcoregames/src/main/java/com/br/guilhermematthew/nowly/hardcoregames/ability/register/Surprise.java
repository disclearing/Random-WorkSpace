package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.entity.Player;

public class Surprise extends Kit {

	public Surprise() {
		initialize(getClass().getSimpleName());
	}

	@Override
	protected void clean(Player player) {
	}
}