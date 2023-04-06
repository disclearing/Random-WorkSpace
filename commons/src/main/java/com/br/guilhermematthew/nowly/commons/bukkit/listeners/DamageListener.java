package com.br.guilhermematthew.nowly.commons.bukkit.listeners;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class DamageListener implements Listener {

    public static final HashMap<Material, Double> damageMaterial = new HashMap<>();
    public static boolean CRITICAL = true;
    public static int CHANCE_DE_CRITICAL = 30;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        Player damager = (Player) event.getDamager();

        double dano = 1.0;
        ItemStack itemStack = damager.getItemInHand();

        if (itemStack != null) {
            dano = damageMaterial.get(itemStack.getType());

            if (itemStack.containsEnchantment(Enchantment.DAMAGE_ALL)) {
                dano += itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
            }
        }

        for (PotionEffect effect : damager.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                dano += ((effect.getAmplifier() + 1) * 2);
            } else if (effect.getType().equals(PotionEffectType.WEAKNESS)) {
                dano -= (effect.getAmplifier() + 1);
            }
        }

        if (CRITICAL) {
            if (isCritical(damager)) {
                dano += 1.0D;
            }
        }

        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getType().equals(PotionEffectType.WEAKNESS)) {
                    dano += (effect.getAmplifier() + 1);
                }
            }
        }

        event.setDamage(dano);
    }

    @SuppressWarnings("deprecation")
    private boolean isCritical(final Player p) {
        return (p.getFallDistance() > 0.0F) && (!p.isOnGround()) &&
                (CommonsConst.RANDOM.nextInt(100) <= CHANCE_DE_CRITICAL) && (!p.hasPotionEffect(PotionEffectType.BLINDNESS));
    }
}