package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import java.util.HashSet;
import java.util.Set;

import com.br.guilhermematthew.nowly.commons.bukkit.utility.LocationUtil;
import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
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
import com.br.guilhermematthew.nowly.hardcoregames.events.player.PlayerDamagePlayerEvent;

public class Kangaroo extends Kit {

	private Set<Player> doubleJump;

	public Kangaroo() {
		initialize(getClass().getSimpleName());
		setUseInvincibility(true);
		
		setItens(new ItemBuilder().material(Material.FIREWORK).name(getItemColor() + "Kit " + getName()).build());

		this.doubleJump = new HashSet<>();
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if ((!event.getAction().equals(Action.PHYSICAL))
				&& (event.getPlayer().getItemInHand().getType().equals(Material.FIREWORK))
				&& (containsHability(event.getPlayer()))) {

			event.setCancelled(true);
			Player p = event.getPlayer();

			if (hasCooldown(p, "CombatLog")) {
				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3, 5), true);
				p.sendMessage("§cVocê levou um hit recentemente, aguarde para usar a habilidade novamente.");
				return;
			}

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
		if (!LocationUtil.isRealMovement(e.getFrom(), e.getTo()))
			return;

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
			if (event.getCause() == EntityDamageEvent.DamageCause.FALL && containsHability(player)) {
				final double damage = event.getDamage();
				if (damage > 7.0D) {
					event.setDamage(4.0D);
				} else {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDamage(PlayerDamagePlayerEvent event) {
		if (containsHability(event.getDamaged())) {
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