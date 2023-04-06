package com.br.guilhermematthew.nowly.pvp.ability.register;

import java.util.HashMap;
import java.util.Map;

import com.br.guilhermematthew.nowly.commons.bukkit.utility.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSnowball;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.pvp.ability.Kit;
import com.br.guilhermematthew.nowly.pvp.events.PlayerDamagePlayerEvent;

import net.minecraft.server.v1_8_R3.EntityFishingHook;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntitySnowball;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;

public class Grappler extends Kit {

	public Grappler() {
		initialize(getClass().getSimpleName());
		
		setItens(new ItemBuilder().type(Material.LEASH).name(getItemColor() + "Kit " + getName()).build());
	}
	
	private final Map<Player, Cordinha> hooks = new HashMap<>();
	private final HashMap<Player, Long> leftClickGrappler = new HashMap<Player, Long>();
	private final HashMap<Player, Long> rightClickGrappler = new HashMap<Player, Long>();
	
	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent event) {
		if (hooks.containsKey(event.getPlayer())) {
	 	    hooks.get(event.getPlayer()).remove();
	 	    hooks.remove(event.getPlayer());
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if (!LocationUtil.isRealMovement(event.getFrom(), event.getTo())) return;
		
		if ((hooks.containsKey(event.getPlayer())) && (!event.getPlayer().getItemInHand().getType().equals(Material.LEASH))) {
	 	    hooks.get(event.getPlayer()).remove();
			 hooks.remove(event.getPlayer());
		}
	}
	 	  
	@EventHandler
	public void onLeash(PlayerLeashEntityEvent e) {
		if (hasAbility(e.getPlayer())) {
			e.setCancelled(true);
			e.getPlayer().updateInventory();
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getAction() == Action.PHYSICAL) return;

		Player p = e.getPlayer();
	 	if ((p.getItemInHand().getType().equals(Material.LEASH)) && 
	 			(hasAbility(p))) {
	 		
	 	     e.setCancelled(true);
			 if (hasCooldown(p, "CombatLog")) {
				 p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3, 5), true);
				 p.sendMessage("§cVocê levou um hit recentemente, aguarde para usar a habilidade novamente.");
	 	    	 return;
			 }
	 	     
	 	     if ((e.getAction() == Action.LEFT_CLICK_AIR) || (e.getAction() == Action.LEFT_CLICK_BLOCK)) {
				  if (leftClickGrappler.containsKey(p) && leftClickGrappler.get(p) > System.currentTimeMillis())
					  return;
				  
	 	          if (hooks.containsKey(p)) 
	 	            hooks.get(p).remove();
	 	         
	 	          Cordinha nmsHook = new Cordinha(p.getWorld(), ((CraftPlayer)p).getHandle());
	 	          nmsHook.spawn(p.getEyeLocation().add(p.getLocation().getDirection().getX(), p.getLocation().getDirection().getY(), p.getLocation().getDirection().getZ()));
	 	          
	 	          nmsHook.move(p.getLocation().getDirection().getX() * 3.0D,
	 	        		  p.getLocation().getDirection().getY() * 3.0D, 
	 	        		  p.getLocation().getDirection().getZ() * 3.0D);
	 	          
	 	          hooks.put(p, nmsHook);
	 			  leftClickGrappler.put(p, System.currentTimeMillis() + 250L);
	 	     } else {
	 	    	 if (!hooks.containsKey(p))
	 	             return;
	 	    	 
			     if (rightClickGrappler.containsKey(p) && rightClickGrappler.get(p) > System.currentTimeMillis())
					 return;
	 	    	 
	 	         if (!((Cordinha)hooks.get(p)).isHooked()) 
	 	             return;
	 	          
	 	         //DamageListener.addBypassVelocity(p);
	 	         rightClickGrappler.put(p, System.currentTimeMillis() + 150L);
	 	         double d = hooks.get(p).getBukkitEntity().getLocation().distance(p.getLocation());
	 	         double t = d;
	 	         double v_x = (1.0D + 0.07000000000000001D * t) * (hooks.get(p).getBukkitEntity().getLocation().getX() - p.getLocation().getX()) / t;
	 	         double v_y = (1.0D + 0.03D * t) * (hooks.get(p).getBukkitEntity().getLocation().getY() - p.getLocation().getY()) / t;
	 	         double v_z = (1.0D + 0.07000000000000001D * t) * (hooks.get(p).getBukkitEntity().getLocation().getZ() - p.getLocation().getZ()) / t;
	 	         Vector v = p.getVelocity();
	 	         v.setX(v_x);
	 	         v.setY(v_y);
	 	         v.setZ(v_z);
	 	         p.setVelocity(v);
	 	         p.playSound(p.getLocation(), Sound.STEP_GRAVEL, 10.0F, 10.0F);
	 	     }
	 	}
	}
	
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(PlayerDamagePlayerEvent event) {
    	if (hasAbility(event.getDamaged())) {
    		addCooldown(event.getDamaged(), "CombatLog", 3);
    	}
    }
	
    public class Cordinha extends EntityFishingHook {

    	private Snowball sb;
        private EntitySnowball controller;
        public EntityHuman owner;
        public Entity hooked;
        public boolean lastControllerDead, isHooked;
      
        public Cordinha(org.bukkit.World world, EntityHuman entityhuman) {
        	super(((CraftWorld)world).getHandle(), entityhuman);
            this.owner = entityhuman;
        }
      
        protected void c() {}
      
        public void t_() {
        	
        if ((!this.lastControllerDead) && (this.controller.dead))
             ((Player)this.owner.getBukkitEntity()).sendMessage("§aSua corda prendeu em algo.");
        
        this.lastControllerDead = this.controller.dead;
        for (Entity entity : this.controller.world.getWorld().getEntities()) {
             if (!(entity instanceof LivingEntity)) {
            	  continue;
             }	 
             if ((!(entity instanceof Firework)) && (entity.getEntityId() != getBukkitEntity().getEntityId()) && 
             (entity.getEntityId() != this.owner.getBukkitEntity().getEntityId()) && 
             (entity.getEntityId() != this.controller.getBukkitEntity().getEntityId()) && (
             (entity.getLocation().distance(this.controller.getBukkitEntity().getLocation()) < 2.0D) || (((entity instanceof Player)) && 
             (((Player)entity).getEyeLocation().distance(this.controller.getBukkitEntity().getLocation()) < 2.0D)))) {
              this.controller.die();
              this.hooked = entity;
              this.isHooked = true;
              this.locX = entity.getLocation().getX();
              this.locY = entity.getLocation().getY();
              this.locZ = entity.getLocation().getZ();
              this.motX = 0.0D;
              this.motY = 0.04D;
              this.motZ = 0.0D;
             }
        }
        try {
          this.locX = this.hooked.getLocation().getX();
          this.locY = this.hooked.getLocation().getY();
          this.locZ = this.hooked.getLocation().getZ();
          this.motX = 0.0D;
          this.motY = 0.04D;
          this.motZ = 0.0D;
          this.isHooked = true;
        } catch (Exception e) {
        	if (this.controller.dead)
                this.isHooked = true;
            this.locX = this.controller.locX;
            this.locY = this.controller.locY;
            this.locZ = this.controller.locZ;
        }
        }
      
        public void die() {}
      
        public void remove() {
        	super.die();
        }
      
        public void spawn(Location location) {
        	this.sb = ((Snowball)this.owner.getBukkitEntity().launchProjectile(Snowball.class));
            this.sb.setVelocity(this.sb.getVelocity().multiply(2.25D));
            this.controller = ((CraftSnowball)this.sb).getHandle();
        
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[] { this.controller.getId() });
        
            for (Player p : Bukkit.getOnlinePlayers()) 
                 ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);

            ((CraftWorld)location.getWorld()).getHandle().addEntity(this);
        }
      
        public boolean isHooked() {
        	return this.isHooked;
        }
      
        public void setHookedEntity(Entity damaged) {
        	this.hooked = damaged;
        }
    }

	@Override
	protected void clean(Player player) {
		if (hooks.containsKey(player)) {
	 	    hooks.get(player).remove();
	 	    hooks.remove(player);
		}

		leftClickGrappler.remove(player);

		rightClickGrappler.remove(player);
	}
}