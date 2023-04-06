package com.br.guilhermematthew.nowly.pvp.ability.register;

import com.br.guilhermematthew.nowly.pvp.PvPMain;
import com.br.guilhermematthew.nowly.pvp.listeners.CombatLogListener;
import lombok.val;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.pvp.ability.Kit;
import org.bukkit.metadata.FixedMetadataValue;

public class Fisherman extends Kit {

	public Fisherman() {
		initialize(getClass().getSimpleName());
		
		setItens(new ItemBuilder().type(Material.FISHING_ROD).name(
				getItemColor() + "Kit " + getName()).unbreakable().build());
	}

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player)) return;

        val damager = e.getDamager();

        if(damager != null && damager.hasMetadata("fisherman")) {
            e.setDamage(0);

            damager.getMetadata("fisherman").stream().findFirst().ifPresent(rawMeta -> {
                val player = (Player) rawMeta.value();

                player.sendMessage(ChatColor.GREEN + "Você fisgou " + e.getEntity().getName());
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerFishListener(PlayerFishEvent event) {
        if(event.getState() == PlayerFishEvent.State.FISHING) {
            if(hasAbility(event.getPlayer())) event.getHook().setMetadata("fisherman", new FixedMetadataValue(PvPMain.getInstance(), event.getPlayer()));
        }

        if (event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY && event.getCaught() instanceof Player) {
            Player player = event.getPlayer();
            
            if (hasAbility(player) && !CombatLogListener.isProtected(player)) {
                Player caughtPlayer = (Player) event.getCaught();
                
                if (caughtPlayer != player) {
                    if (!CombatLogListener.isProtected(caughtPlayer)) {
                        caughtPlayer.teleport(player.getLocation());
                    }
                } else {
                	event.setCancelled(true);
                }
            }
        }
    }

	@Override
	protected void clean(Player player) {}
}