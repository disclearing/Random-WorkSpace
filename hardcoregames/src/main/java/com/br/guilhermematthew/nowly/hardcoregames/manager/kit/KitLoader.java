package com.br.guilhermematthew.nowly.hardcoregames.manager.kit;

import java.io.File;
import java.util.Arrays;

import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Material;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.manager.configuration.impl.BukkitConfiguration;
import com.br.guilhermematthew.nowly.commons.common.utility.ClassGetter;
import com.br.guilhermematthew.nowly.commons.common.utility.system.Machine;

import lombok.Getter;

public class KitLoader {
	
	@Getter
	private static BukkitConfiguration kitsConfig = new BukkitConfiguration("kits-hg", getDirectorty(), false);
	
	public static void load() {
		kitsConfig.load();
		
		boolean saveConfig = false;
		
		for (Class<?> classes : ClassGetter.getClassesForPackage(HardcoreGamesMain.getInstance(), 
				"com.br.guilhermematthew.nowly.hardcoregames.ability.register")) {
			String className = classes.getSimpleName();
			
			boolean add = false;
			
			try {
				 if (Kit.class.isAssignableFrom(classes) && (classes != Kit.class)) {
					 add = true;
					 
					 if (!kitsConfig.getConfiguration().contains("kits." + className + ".price")) {
						 kitsConfig.getConfiguration().set("kits." + className + ".price", 1000);
				    	 kitsConfig.getConfiguration().set("kits." + className + ".cooldown", 20);
				    	 kitsConfig.getConfiguration().set("kits." + className + ".icon.material", "CHEST");
				    	 kitsConfig.getConfiguration().set("kits." + className + ".icon.durability", 0);
				    	 kitsConfig.getConfiguration().set("kits." + className + ".icon.amount", 1);
				    	 kitsConfig.getConfiguration().set("kits." + className + ".icon.description", 
				    			 Arrays.asList("&bEdite a desc na", "&cConfig"));
						 saveConfig = true;
					 }
				 }
			 } catch (Exception ex) {
				 add = false;
				 CommonsGeneral.error("Ocorreu um erro ao tentar carregar um kit '"+className+"' > " + ex.getLocalizedMessage());
			 }
			
			
			if (add) {
				Material material = null;
		    	 
				try {
					material =  Material.getMaterial(kitsConfig.getConfiguration().getString("kits." + className + ".icon.material"));
				} catch (NullPointerException ex) {
			        HardcoreGamesMain.console("Material do Kit '" + className + "' está incorreto.");
			    	add = false;
				} finally {
					if (material == null) {
			    		add = false;
					}
				}
				
				if (add) {
					try {
						KitManager.getKits().put(className.toLowerCase(), (Kit) classes.newInstance());
					} catch (Exception ex) {
						HardcoreGamesMain.console("Ocorreu um erro ao tentar adicionar um Kit. ->" + ex.getLocalizedMessage());
					}
			     } else {
			    	 HardcoreGamesMain.console("Não foi possivel adicionar o Kit -> " + className);
			     }
			}
			
		}
		
		if (saveConfig) {
			kitsConfig.save();
		}
		
		kitsConfig.unload();
		kitsConfig = null;
	}

	private static File getDirectorty() {
		File dir = new File(Machine.getDiretorio());
		
		if (!dir.exists()) {
			dir.mkdir();
		}
		
		return dir;
	}
}