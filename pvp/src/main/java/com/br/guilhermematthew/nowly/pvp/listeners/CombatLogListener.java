package com.br.guilhermematthew.nowly.pvp.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.br.guilhermematthew.nowly.pvp.events.GamerDeathEvent;
import com.br.guilhermematthew.nowly.pvp.events.PlayerDamagePlayerEvent;
import com.br.guilhermematthew.nowly.pvp.manager.combatlog.CombatLogManager;
import com.br.guilhermematthew.nowly.pvp.manager.combatlog.CombatLogManager.CombatLog;
import com.br.guilhermematthew.nowly.pvp.manager.gamer.GamerManager;

public class CombatLogListener implements Listener {
	
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        
        Player player = (Player) event.getEntity();
        if (GamerManager.getGamer(player).isProtection()) {
            event.setCancelled(true);
        }
        player = null;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamage(PlayerDamagePlayerEvent event) {
    	if ((GamerManager.getGamer(event.getDamaged()).isProtection()) || (GamerManager.getGamer(event.getPlayer()).isProtection())) {
    		event.setCancelled(true);
    	}
    }
    
    public static boolean isProtected(Player player) {
    	return GamerManager.getGamer(player.getUniqueId()).isProtection();
    }
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public static void onPlayerDamageCombatLog(PlayerDamagePlayerEvent e) {
	    CombatLogManager.newCombatLog(e.getDamaged(), e.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent e) {
	    CombatLogManager.removeCombatLog(e.getEntity());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onQuit(PlayerQuitEvent event) {
	    checkCombatLog(event.getPlayer());
	}
	
	public static void checkCombatLog(Player player) {
	    CombatLog log = CombatLogManager.getCombatLog(player);
		
	    if (log.isFighting()) {
	    	
	        Player combatLogger = log.getCombatLogged();
	        
	        if (combatLogger != null) {
	            if (combatLogger.isOnline()) {
	            	CombatLogManager.removeCombatLog(combatLogger);
	            	
	            	Bukkit.getServer().getPluginManager().callEvent(new GamerDeathEvent(player, combatLogger));
	            }
	        }
	    }
	    
	    log = null;
	    
	    CombatLogManager.removeCombatLog(player);
	}
}