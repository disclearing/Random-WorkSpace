package com.br.guilhermematthew.nowly.hardcoregames.manager.structures.types;

import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.hardcoregames.manager.structures.StructuresManager;
import com.br.guilhermematthew.nowly.hardcoregames.utility.schematic.SchematicUtility;

public class MiniFeast {

	public static void create() {
		Location location = StructuresManager.getValidLocation(false);
		
		SchematicUtility.spawnarSchematic("minifeast", location, false);
		
		int reduceX = CommonsConst.RANDOM.nextInt(60) + 1;
		int reduceZ = CommonsConst.RANDOM.nextInt(40) + 1;
		int upX     = CommonsConst.RANDOM.nextInt(40) + 1;
		int upZ     = CommonsConst.RANDOM.nextInt(70) + 1;
		
		int x = location.getBlockX() + upX,
				z = location.getBlockZ() + upZ,
				x1 = location.getBlockX() - reduceX,
				z1 = location.getBlockZ() - reduceZ;
		
		Bukkit.broadcastMessage(StringUtils.MINIFEAST_SPAWNED.replace("%x%", "" + x).replace("%z%", "" + z).replace(
				"%x1%", "" + x1).replace("%z1%", "" + z1));
		
		HardcoreGamesMain.console("MiniFeast spawnou em -> " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
		
		location = null;
	}
}