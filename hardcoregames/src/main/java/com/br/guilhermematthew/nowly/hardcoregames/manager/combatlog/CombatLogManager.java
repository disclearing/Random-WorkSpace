package com.br.guilhermematthew.nowly.hardcoregames.manager.combatlog;

import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import lombok.Data;
import lombok.RequiredArgsConstructor;

public class CombatLogManager {

    private final static String COMBATLOG_PLAYER = "combatlog.player";
    private final static String COMBATLOG_EXPIRE = "combatlog.time";

    private final static long COMBATLOG_TIME = 5000L; // Miliseconds

    public static void newCombatLog(Player damager, Player damaged) {
        setCombatLog(damager, damaged);
        setCombatLog(damaged, damager);
    }
    
	public static Player getLastHit(Player p) {
		Player finded = null;

		CombatLog log = CombatLogManager.getCombatLog(p);

		if (log.isFighting()) {

			Player combatLogger = log.getCombatLogged();

			if (combatLogger != null) {
				if (combatLogger.isOnline()) {
					CombatLogManager.removeCombatLog(p);
					finded = combatLogger;
				}
				combatLogger = null;
			}
		}

		log = null;

		return finded;
	}
    
    public static void removeCombatLog(Player player) {
        HardcoreGamesMain plugin = HardcoreGamesMain.getInstance();

        if (player.hasMetadata(COMBATLOG_PLAYER))
            player.removeMetadata(COMBATLOG_PLAYER, plugin);
        
        if (player.hasMetadata(COMBATLOG_EXPIRE))
            player.removeMetadata(COMBATLOG_EXPIRE, plugin);
    }

    private static void setCombatLog(Player player1, Player player2) {
    	HardcoreGamesMain plugin = HardcoreGamesMain.getInstance();

        removeCombatLog(player1);

        player1.setMetadata(COMBATLOG_PLAYER, new FixedMetadataValue(plugin, player2.getName()));
        player1.setMetadata(COMBATLOG_EXPIRE, new FixedMetadataValue(plugin, System.currentTimeMillis()));
    }

    public static CombatLog getCombatLog(Player player) {
        String playerName = "";
        
        long time = 0L;
        
        if (player.hasMetadata(COMBATLOG_PLAYER))
            playerName = player.getMetadata(COMBATLOG_PLAYER).get(0).asString();
        
        if (player.hasMetadata(COMBATLOG_EXPIRE))
            time = player.getMetadata(COMBATLOG_EXPIRE).get(0).asLong();
        
        Player combatLogged = Bukkit.getPlayer(playerName);
        return new CombatLog(combatLogged, time);
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