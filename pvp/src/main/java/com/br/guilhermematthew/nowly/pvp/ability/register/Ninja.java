package com.br.guilhermematthew.nowly.pvp.ability.register;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.br.guilhermematthew.nowly.pvp.ability.Kit;
import com.br.guilhermematthew.nowly.pvp.events.GamerDeathEvent;
import com.br.guilhermematthew.nowly.pvp.events.PlayerDamagePlayerEvent;
import com.br.guilhermematthew.nowly.pvp.listeners.CombatLogListener;

import lombok.Getter;

public class Ninja extends Kit {

    private final Map<Player, NinjaHit> ninja;
	
	public Ninja() {
		initialize(getClass().getSimpleName());
		this.ninja = new HashMap<>();
	}
	
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDamagePlayerEvent(PlayerDamagePlayerEvent event) {
        if (!CombatLogListener.isProtected(event.getPlayer()) && !CombatLogListener.isProtected(event.getDamaged()) && 
        		hasAbility(event.getPlayer())) {
           
        	NinjaHit ninjaHit = ninja.get(event.getPlayer());
            if (ninjaHit == null) {
                ninjaHit = new NinjaHit(event.getDamaged());
                ninja.put(event.getPlayer(), ninjaHit);
            } else {
                ninjaHit.setTarget(event.getDamaged());
            }
        }
    }
    
    @EventHandler
    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        final Player player = event.getPlayer();
        
        if (event.isSneaking() && this.ninja.containsKey(player) && !CombatLogListener.isProtected(player) && hasAbility(player)) {
        	
        	NinjaHit hit = this.ninja.get(player);
            if (hit == null)
                return;
            
            if (hit.getTargetExpires() < System.currentTimeMillis())
                return;
            
            Player target = hit.getTarget();
            if (target.isOnline() && !CombatLogListener.isProtected(target)) {
                if (player.getLocation().distance(target.getLocation()) > 40) {
                	player.sendMessage("§cJogador muito longe!");
                    return;
                }
                
                if (!hasCooldown(player)) {
                    player.teleport(target.getLocation());

                    addCooldown(player, getCooldownSeconds());

                    player.getWorld().playSound(player.getLocation().add(0.0, 0.5, 0.0), Sound
                            .ENDERMAN_TELEPORT, 0.3F, 1.0F);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerWarpDeathEvent(GamerDeathEvent event) {
    	clean(event.getPlayer());
    	
        if (event.hasKiller()) {
        	clean(event.getKiller());
        }
    }

	protected void clean(Player player) {
        this.ninja.remove(player);
	}
	
    @Getter
    private static class NinjaHit {

        private Player target;
        private long targetExpires;

        public NinjaHit(Player target) {
            this.target = target;
            this.targetExpires = System.currentTimeMillis() + 15000;
        }

        public void setTarget(Player player) {
            this.target = player;
            this.targetExpires = System.currentTimeMillis() + 15000;
        }

    }
}