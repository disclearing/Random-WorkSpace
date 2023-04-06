package com.br.guilhermematthew.nowly.pvp.ability.register;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.pvp.ability.Kit;
import com.br.guilhermematthew.nowly.pvp.events.PlayerDamagePlayerEvent;

public class Kangaroo extends Kit {

	private Set<Player> doubleJump;
	
	public Kangaroo() {
		initialize(getClass().getSimpleName());
		
		setItens(new ItemBuilder().type(Material.FIREWORK).name(
				getItemColor() + "Kit " + getName()).build());
		
		this.doubleJump = new HashSet<>();
	}

	public void eject(Player p) {
		if (doubleJump.contains(p)) {
			doubleJump.remove(p);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void Habilidade(PlayerInteractEvent e) {
		if ((!e.getAction().equals(Action.PHYSICAL)) &&
				(e.getPlayer().getItemInHand().getType().equals(Material.FIREWORK)) &&
				(hasAbility(e.getPlayer()))) {
			
		 	 e.setCancelled(true);
			 Player p = e.getPlayer();
			 
			 if (hasCooldown(p, "CombatLog")) {
				 p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3, 5), true);
				 p.sendMessage("§cVocê levou um hit recentemente, aguarde para usar a habilidade novamente.");
	 	    	 return;
			 }
			 
	 	     //DamageListener.addBypassVelocity(p);

			 if (!doubleJump.contains(p)) {
			org.bukkit.util.Vector velocity = p.getEyeLocation().getDirection();
			if (p.isSneaking()) {
				velocity = velocity.multiply(2.45F).setY(0.5F);
			} else {
				velocity = velocity.multiply(0.5F).setY(1.0F);
			}
			p.setFallDistance(-1.0F);
			p.setVelocity(velocity);
			doubleJump.add(p);
			velocity = null;

			  }
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void removeOnMove(PlayerMoveEvent e) {
		//if (!e.isRealMovement()) return;

		if (!doubleJump.contains(e.getPlayer()))
			return;
		if (!e.getPlayer().isOnGround())
			return;
		doubleJump.remove(e.getPlayer());
	}
	
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL && hasAbility(player)) {
                final double damage = event.getDamage();
                if (damage > 7.0D) {
                    event.setDamage(7.0D);
                } else {
                	event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(PlayerDamagePlayerEvent event) {
    	if (hasAbility(event.getDamaged())) {
    		addCooldown(event.getDamaged(), "CombatLog", 3);
    	}
    }

	@Override
	protected void clean(Player player) {
		if (doubleJump.contains(player)) {
			doubleJump.remove(player);
		}
	}
}