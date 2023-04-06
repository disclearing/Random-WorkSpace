package com.br.guilhermematthew.nowly.pvp.manager.combatlog;

import com.br.guilhermematthew.nowly.pvp.PvPMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import lombok.Data;
import lombok.RequiredArgsConstructor;

public class CombatLogManager {

    private final static String COMBATLOG_PLAYER = "combatlog.player";
    private final static String COMBATLOG_EXPIRE = "combatlog.time";

    private final static long COMBATLOG_TIME = 5000L;

    public static void newCombatLog(Player damager, Player damaged) {
        setCombatLog(damager, damaged);
        setCombatLog(damaged, damager);
    }

	public static Player getLastHit(Player player) {
		Player finded = null;
		
	    CombatLog log = getCombatLog(player);
	    
	    if (log.isFighting()) {
	        Player combatLogger = log.getCombatLogged();
	        
	        if (combatLogger != null) {
	        	if (combatLogger.isOnline()) {
	        		finded = combatLogger;
	        	}
            }
	    }

        return finded;
	}
    
    public static void removeCombatLog(Player player) {
    	PvPMain plugin = PvPMain.getInstance();

        if (player.hasMetadata(COMBATLOG_PLAYER)) {
        	player.removeMetadata(COMBATLOG_PLAYER, plugin);
        }
        
        if (player.hasMetadata(COMBATLOG_EXPIRE)) {
            player.removeMetadata(COMBATLOG_EXPIRE, plugin);
        }
    }

    public static void setCombatLog(Player player1, Player player2) {
    	PvPMain plugin = PvPMain.getInstance();

        removeCombatLog(player1);

        player1.removeMetadata(COMBATLOG_PLAYER, plugin);
        player1.removeMetadata(COMBATLOG_EXPIRE, plugin);

        player1.setMetadata(COMBATLOG_PLAYER, new FixedMetadataValue(plugin, player2.getName()));
        player1.setMetadata(COMBATLOG_EXPIRE, new FixedMetadataValue(plugin, System.currentTimeMillis()));
    }

    public static CombatLog getCombatLog(Player player) {
        String playerName = "";
        
        long time = 0L;
        
        if (player.hasMetadata(COMBATLOG_PLAYER)) {
            playerName = player.getMetadata(COMBATLOG_PLAYER).get(0).asString();
        }
        
        if (player.hasMetadata(COMBATLOG_EXPIRE)) {
            time = player.getMetadata(COMBATLOG_EXPIRE).get(0).asLong();
        }
        
        return new CombatLog(Bukkit.getPlayer(playerName), time);
    }

    @Data
    @RequiredArgsConstructor
    public static class CombatLog {
    	
        private final Player combatLogged;
        private final long time;

        public boolean isFighting() {
            return System.currentTimeMillis() < time + COMBATLOG_TIME;
        }
    }
}