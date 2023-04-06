package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Scout extends Kit {

    public Scout() {
        initialize(getClass().getSimpleName());

        setItens(new ItemBuilder().type(Material.getMaterial(373)).durability(34).name(
                getItemColor() + "Kit " + getName()).build());
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) return;
        Player player = event.getPlayer();
        if ((player.getItemInHand().getType().equals(Material.getMaterial(373))) &&
                (containsHability(player))) {

            if (hasCooldown(player)) {
                sendMessageCooldown(player);
                return;
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30, 2));
            player.sendMessage("§aVocê recebeu velocidade por um tempo valido de 30 segundos!");
            addCooldown(player, getCooldownSeconds());
        }
    }

    @Override
    protected void clean(Player player) {

    }
}
