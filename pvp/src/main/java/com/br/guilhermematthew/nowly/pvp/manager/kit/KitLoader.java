package com.br.guilhermematthew.nowly.pvp.manager.kit;

import java.util.Arrays;

import com.br.guilhermematthew.nowly.pvp.PvPMain;
import com.br.guilhermematthew.nowly.pvp.ability.Kit;
import org.bukkit.Material;

import com.br.guilhermematthew.nowly.commons.bukkit.manager.configuration.impl.BukkitConfiguration;
import com.br.guilhermematthew.nowly.commons.common.utility.ClassGetter;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;

import lombok.Getter;

public class KitLoader {
	
	@Getter
	private static BukkitConfiguration kitsConfig = new BukkitConfiguration("kits", PvPMain.getInstance().getDataFolder(), false);
	
	@SuppressWarnings("deprecation")
	public static void load() {
		kitsConfig.load();
		
		boolean saveConfig = false;
		
		for (Class<?> classes : ClassGetter.getClassesForPackage(PvPMain
				.getInstance(), "com.br.guilhermematthew.nowly.pvp.ability.register")) {
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
				    			 Arrays.asList("&7Edite a desc na", "&7Config"));
						 saveConfig = true;
					 }
				 }
			 } catch (Exception ex) {
				 add = false;
				 PvPMain.console("Ocorreu um erro ao tentar carregar um kit '"+className+"' > " + ex.getLocalizedMessage());
			 }
			
			
			if (add) {
				Material material = null;
				
				try {
					if (StringUtility.isInteger(kitsConfig.getConfiguration().getString("kits." + className + ".icon.material"))) {
						material =  Material.getMaterial(Integer.valueOf(
								kitsConfig.getConfiguration().getString("kits." + className + ".icon.material")));
					} else {
						material =  Material.getMaterial(kitsConfig.getConfiguration().getString("kits." + className + ".icon.material"));
					}
				} catch (NullPointerException ex) {
					PvPMain.console("Material do Kit '" + className + "' está incorreto.");
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
						PvPMain.console("Ocorreu um erro ao tentar adicionar um Kit. ->" + ex.getLocalizedMessage());
					}
			     } else {
			    	 PvPMain.console("não foi possivel adicionar o Kit -> " + className);
			     }
			}
			
		}
		
		if (saveConfig) {
			kitsConfig.save();
		}
		
		kitsConfig.unload();
		kitsConfig = null;
	}
}