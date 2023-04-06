package com.br.guilhermematthew.nowly.pvp.ability.register;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import com.br.guilhermematthew.nowly.pvp.PvPMain;
import com.br.guilhermematthew.nowly.pvp.ability.Kit;
import com.br.guilhermematthew.nowly.pvp.events.PlayerDamagePlayerEvent;

public class Anchor extends Kit {
	
	public Anchor() {
		initialize(getClass().getSimpleName());
	}
	
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(PlayerDamagePlayerEvent event) {
    	if (hasAbility(event.getDamaged())) {
    		handle(event.getDamaged(), event.getPlayer());
    	} else if (hasAbility(event.getPlayer())) {
    		handle(event.getDamaged(), event.getPlayer());
    	}
    }
	
	public void handle(Player player1, Player player2) {
		 player1.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
 		 player2.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
 		 
		 player1.playSound(player1.getLocation(), Sound.ANVIL_LAND, 1, 1);
		 player2.playSound(player2.getLocation(), Sound.ANVIL_LAND, 1, 1);
 		 
		 PvPMain.runLater(() -> {
			 player1.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
	 		 player2.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
		 }, 1L);
	}

	@Override
	protected void clean(Player player) {}
}