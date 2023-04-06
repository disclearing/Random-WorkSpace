package com.br.guilhermematthew.nowly.hardcoregames.utility.schematic;

import java.io.File;
import java.io.IOException;

import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import org.bukkit.Location;

import com.br.guilhermematthew.nowly.commons.common.utility.system.Machine;

public class SchematicUtility {
	
	public static void spawnarSchematic(String nome, Location loc, boolean force) {
		File dir = new File(Machine.getDiretorio() + Machine.getSeparador() + "schematics");
		if (!dir.exists()) {
			dir.mkdir();
		}
		
		File file = new File(Machine.getDiretorio() + Machine.getSeparador() + "schematics", nome + ".schematic");
		
		if (file.exists()) {
			SchematicLoader schematic;
			
			HardcoreGamesMain.console("Tentando carregar a schematic '"+nome+"'...");
			
			try {
				schematic = new SchematicLoader(nome, file);
				
				schematic.paste(nome, loc, getBlockSpeed(nome), force);
			} catch (IOException e) {
				HardcoreGamesMain.console("Ocorreu um erro ao tentar carregar a schematic '"+nome+"' -> " + e.getLocalizedMessage());
			}
		} else {
			HardcoreGamesMain.console("SchematicLoader '"+nome+"' nao existe.");
		}
	}
	
	private static int getBlockSpeed(String type) {
		if (type.equalsIgnoreCase("feast")) {
			return 100;
		} else if (type.equalsIgnoreCase("minifeast")) {
			return 150;
		} else {
			return 100;
		}
	}
}