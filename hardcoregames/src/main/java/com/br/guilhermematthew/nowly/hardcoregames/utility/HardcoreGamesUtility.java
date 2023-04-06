package com.br.guilhermematthew.nowly.hardcoregames.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.bukkit.api.player.PlayerAPI;
import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.StringUtils;
import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesOptions;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.Gamer;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;

public class HardcoreGamesUtility {

	@SuppressWarnings("deprecation")
	public static int handleRelog(Player player, Location loc) {
		List<ItemStack> drop = new ArrayList<>();
		drop.addAll(Arrays.asList(player.getInventory().getContents()));
		drop.addAll(Arrays.asList(player.getInventory().getArmorContents()));
		drop.add(player.getItemOnCursor());

		int taskID = Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(HardcoreGamesMain.getInstance(),
				new Runnable() {
					public void run() {
						Gamer gamer = GamerManager.getGamer(player.getUniqueId());

						if ((!player.isOnline()) && (gamer.isRelogar())) {
							gamer.setPlaying(false);
							gamer.setEliminado(true);
							gamer.setRelogar(false);

							PlayerAPI.dropItems(player, drop, loc);

							Bukkit.broadcastMessage(StringUtils.PLAYER_DEMOROU_PARA_RELOGAR
									.replace("%nick%", player.getName()).replace("%kit%", gamer.getKit1())
									.replace("%restantes%", "" + GamerManager.getAliveGamers().size()));

							HardcoreGamesMain.getGameManager().getGameType().checkWin();

							drop.clear();
						}
					}
				}, 20 * 45);

		return taskID;
	}
	
	public static boolean availableToSpec(final Player player) {
		if (HardcoreGamesOptions.SPEC_FREE_OPTION) return true;
		if (!HardcoreGamesOptions.SPEC_OPTION) return false;
		
		return player.hasPermission("hungergames.espectar");
	}
	
	public static void strikeLightning(Location coords) {
		coords.getWorld().strikeLightningEffect(coords);
		
		Block block = coords.getBlock();
		block.setType(Material.AIR);
		
		block = null;
	}
	
	public static Location getRandomLocation(int maximo) {
		int x = CommonsConst.RANDOM.nextInt(maximo) + maximo,
				z = CommonsConst.RANDOM.nextInt(maximo) + maximo;
		
		if (CommonsConst.RANDOM.nextBoolean()) {
			x = -x;
		}
		
		if (CommonsConst.RANDOM.nextBoolean()) {
			z = -z;
		}
		
		final World world = (World) Bukkit.getServer().getWorlds().get(0);
		
		return new Location(world, x + 0.500, (world.getHighestBlockYAt(x, z) + 1), z + 0.500);
	}
	
	public static Player getRandomPlayer(Player player) {
		Player target = null;
		
        for (Player playerTarget : Bukkit.getOnlinePlayers()) {
        	 Gamer gamer = GamerManager.getGamer(playerTarget.getUniqueId());
        	 
        	 if (gamer != null) {
        		 if (gamer.isPlaying()) {
        			 if (playerTarget != null) {
                         if (!playerTarget.equals(player)) {
                      	     if (playerTarget.getLocation().distance(player.getLocation()) >= 15.0D) {
                      	    	 if (target == null) {
                      				 target = playerTarget;
                      	    	 } else {
                      				  double distanciaAtual = target.getLocation().distance(player.getLocation());
                      				  double novaDistancia = playerTarget.getLocation().distance(player.getLocation());
                      				  if (novaDistancia < distanciaAtual) {
                      					  target = playerTarget;
                      					  if (novaDistancia <= 50) {
                      						  break;
                      					  }
                      				  }
                      	    	 }
                      	     }
                         }
                     }
        		 }
        	 }
        		 
        	 gamer = null;
        }
		return target;
	}
	
	public static void spawnRandomFirework(Location loc) {
		Firework fireWork = (Firework)loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		FireworkMeta fireWorkMeta = fireWork.getFireworkMeta();
		
		Random r = CommonsConst.RANDOM;
		
		int rt = r.nextInt(4) + 3;
		
		FireworkEffect.Type fireWorkType = FireworkEffect.Type.BALL;
		
		if (rt == 1) {
			fireWorkType = FireworkEffect.Type.BALL;
		} else if (rt == 2) {
			fireWorkType = FireworkEffect.Type.BALL_LARGE;
		} else if (rt == 3) {
			fireWorkType = FireworkEffect.Type.BURST;
		} else if (rt == 4) {
			fireWorkType = FireworkEffect.Type.CREEPER;
		} else if (rt == 5) {
			fireWorkType = FireworkEffect.Type.STAR;
		} else if (rt > 5) {
			fireWorkType = FireworkEffect.Type.BALL_LARGE;
		}
		
		Color c1 = Color.RED;
		Color c2 = Color.LIME;
		Color c3 = Color.SILVER;		    
		
		FireworkEffect effect = FireworkEffect.builder().flicker(
				r.nextBoolean()).withColor(c1).withColor(c2).withFade(c3).with(fireWorkType).trail(r.nextBoolean()).build();
		
		fireWorkMeta.addEffect(effect);
		
		int rp = r.nextInt(2) + 1;
		fireWorkMeta.setPower(rp);
		fireWork.setFireworkMeta(fireWorkMeta);
		
		fireWork = null;
		r = null;
		fireWorkMeta = null;
		fireWorkType = null;
		c1 = null;
		c2 = null;
		c3 = null;
	}
}