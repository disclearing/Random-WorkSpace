package com.br.guilhermematthew.nowly.pvp.ability.register;

import java.util.HashSet;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockIterator;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.pvp.ability.Kit;

public class Flash extends Kit {

	public Flash() {
		initialize(getClass().getSimpleName());
		
		setItens(new ItemBuilder().type(Material.REDSTONE_TORCH_ON).name(getItemColor() + "Kit " + getName()).build());
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getAction() == Action.PHYSICAL) return;

		if (hasAbility(e.getPlayer()) && checkItem(e.getItem(), getItemColor() + "Kit " + getName())) {
			if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				e.setCancelled(true);
				e.getPlayer().updateInventory();
				return;
			}
			
			if (e.getAction() != Action.RIGHT_CLICK_AIR) {
				return;
			}
			
			e.setCancelled(true);
			
			Player p = e.getPlayer();
			
			if (hasCooldown(p)) {
				sendMessageCooldown(p);
				return;
			}
			Block b = e.getPlayer().getTargetBlock((HashSet<Byte>) null, 25).getLocation().getBlock();

			if (b.getType() == Material.AIR) {
				p.sendMessage("§cEscolha outro bloco para se teleportar.");
				return;
			}
			BlockIterator list = new BlockIterator(p.getEyeLocation(), 0, 100);
			while (list.hasNext()) {
				p.getWorld().playEffect(list.next().getLocation(), Effect.ENDER_SIGNAL, 100);
			}
			p.teleport(b.getLocation().clone().add(0, 1.5, 0));
			p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
			addCooldown(p, getCooldownSeconds());
		}
	}

	@Override
	protected void clean(Player player) {
		// TODO Auto-generated method stub
		
	}
}