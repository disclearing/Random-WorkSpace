package com.br.guilhermematthew.nowly.hardcoregames.manager.structures;

import java.util.ArrayList;
import java.util.Arrays;

import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.manager.structures.types.Feast;
import com.br.guilhermematthew.nowly.hardcoregames.manager.structures.types.FinalBattle;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemChance;
import com.br.guilhermematthew.nowly.commons.bukkit.manager.configuration.impl.BukkitConfiguration;

import lombok.Getter;

public class StructuresManager {

	private static FinalBattle finalBattle;
	private static Feast feast;

	private static ArrayList<ItemChance> itensFeast = new ArrayList<>();
	private static ArrayList<ItemChance> itensMinifeast = new ArrayList<>();

	@Getter
	private static BukkitConfiguration chestConfig = new BukkitConfiguration("chests-hg", false);

	public static void loadItens() {
		chestConfig.load();
		
		boolean save = false;
		
		ItemBuilder itemBuilder = new ItemBuilder();
		
		if (chestConfig.getConfiguration().contains("minifeast.itens")) {
			for (String line : chestConfig.getConfiguration().getStringList("minifeast.itens")) {
				String separador[] = line.split(",");
				String material = separador[0].replace("material:", "");
				int quantidade = Integer.parseInt(separador[1].replace("amount:", ""));
				int durabilidade = Integer.parseInt(separador[2].replace("durability:", ""));
				int chance = Integer.parseInt(separador[3].replace("chance:", ""));

				Material m = null;

				try {
					m = Material.getMaterial(material);
				} catch (NullPointerException e) {
					HardcoreGamesMain.console("Material invalido no Mini-Feast foi encontrado. -> " + material);
				}

				if (m == null)
					continue;

				itensMinifeast.add(new ItemChance(itemBuilder.type(m).durability(durabilidade).build(), chance,
						(quantidade == 1 ? 0 : quantidade)));
			}
		} else {
			save = true;
			
			chestConfig.getConfiguration().set("minifeast.itens", Arrays.asList(
					"material:IRON_SWORD,amount:1,durability:0,chance:28",
					"material:EXP_BOTTLE,amount:15,durability:0,chance:45",
					"material:IRON_PICKAXE,amount:1,durability:0,chance:28",
					"material:POTION,amount:1,durability:16393,chance:30",
					"material:POTION,amount:1,durability:16417,chance:30",
					"material:INK_SACK,amount:35,durability:3,chance:39",
					"material:COOKED_BEEF,amount:35,durability:0,chance:39",
					"material:ENDER_PEARL,amount:10,durability:0,chance:45",
					"material:MONSTER_EGG,amount:7,durability:95,chance:34",
					"material:BONE,amount:12,durability:0,chance:35",
					"material:WEB,amount:10,durability:0,chance:25",
					"material:TNT,amount:10,durability:0,chance:25",
					"material:LAVA_BUCKET,amount:1,durability:0,chance:10"
					));
		}

		if (chestConfig.getConfiguration().contains("feast.itens")) {
			for (String line : chestConfig.getConfiguration().getStringList("feast.itens")) {
				String separador[] = line.split(",");
				String material = separador[0].replace("material:", "");
				int quantidade = Integer.parseInt(separador[1].replace("amount:", ""));
				int durabilidade = Integer.parseInt(separador[2].replace("durability:", ""));
				int chance = Integer.parseInt(separador[3].replace("chance:", ""));

				Material m = null;
				try {
					m = Material.getMaterial(material);
				} catch (NullPointerException e) {
					HardcoreGamesMain.console("Material invalido no Feast foi encontrado. -> " + material);
				}

				if (m == null)
					continue;

				itensFeast.add(new ItemChance(itemBuilder.type(m).durability(durabilidade).build(), chance,
						(quantidade == 1 ? 0 : quantidade)));
			}
		} else {
			save = true;
			
			chestConfig.getConfiguration().set("feast.itens", Arrays.asList(
					"material:DIAMOND_HELMET,amount:1,durability:0,chance:20",
					"material:DIAMOND_CHESTPLATE,amount:1,durability:0,chance:19",
					"material:DIAMOND_LEGGINGS,amount:1,durability:0,chance:19",
					"material:DIAMOND_BOOTS,amount:1,durability:0,chance:20",
					"material:DIAMOND_SWORD,amount:1,durability:0,chance:19",
					"material:DIAMOND_AXE,amount:1,durability:0,chance:19",
					"material:DIAMOND_PICKAXE,amount:1,durability:0,chance:19",
					"material:FLINT_AND_STEEL,amount:1,durability:0,chance:26",
					"material:WATER_BUCKET,amount:1,durability:0,chance:37",
					"material:LAVA_BUCKET,amount:1,durability:0,chance:35",
					"material:BOW,amount:1,durability:0,chance:40",
						
					"material:POTION,amount:1,durability:16418,chance:32",
					"material:POTION,amount:1,durability:16424,chance:32",
					"material:POTION,amount:1,durability:16420,chance:33",
					"material:POTION,amount:1,durability:16428,chance:31",
					"material:POTION,amount:1,durability:16426,chance:30",
					"material:POTION,amount:1,durability:16417,chance:32",
					"material:POTION,amount:1,durability:16419,chance:33",
					"material:POTION,amount:1,durability:16421,chance:34",
					
					"material:COOKED_BEEF,amount:30,durability:0,chance:40",
					"material:ENDER_PEARL,amount:12,durability:0,chance:31",
					"material:ENDER_PEARL,amount:5,durability:0,chance:21",
					"material:GOLDEN_APPLE,amount:15,durability:0,chance:30",
					"material:EXP_BOTTLE,amount:32,durability:0,chance:42",
					"material:WEB,amount:28,durability:0,chance:13",
					"material:TNT,amount:28,durability:0,chance:39",
					"material:DIAMOND,amount:12,durability:0,chance:13",
					"material:ARROW,amount:31,durability:0,chance:31"
					));
		}

		itemBuilder = null;
		
		if (save) {
			chestConfig.save();
		}
		
		chestConfig.unload();
		chestConfig = null;
	}

	public static Location getValidLocation(boolean fixY) {
		int x = getCoord(300), z = getCoord(300);

		World world = Bukkit.getWorld("world");
		Location loc = new Location(world, x, fixY ? 90 : world.getHighestBlockYAt(x, z) + 1, z);

		boolean localValido = false;

		while (!localValido) {
			if (loc.getBlockY() >= 120 || loc.getBlockY() <= 55 || x + z < 40) {
				x = getCoord(300);
				z = getCoord(300);
				loc = new Location(world, x, fixY ? 90 : world.getHighestBlockYAt(x, z) + 1, z);
			} else {
				localValido = true;
			}
		}

		return loc;
	}

	public static int getCoord(int range) {
		return CommonsConst.RANDOM.nextBoolean() ? CommonsConst.RANDOM.nextInt(range) : -CommonsConst.RANDOM.nextInt(range);
	}

	public static FinalBattle getFinalBattle() {
		if (finalBattle == null) {
			finalBattle = new FinalBattle();
		}
		return finalBattle;
	}

	public static Feast getFeast() {
		if (feast == null) {
			feast = new Feast();
		}
		return feast;
	}
	
	public static void addItensChestMinifeast(Chest chest) {	
		for (int i = 0; i < itensMinifeast.size(); i++)
			 if (CommonsConst.RANDOM.nextInt(100) < itensMinifeast.get(i).getChance()) {
				 int slot = CommonsConst.RANDOM.nextInt(chest.getBlockInventory().getSize());
				 chest.getBlockInventory().setItem(slot, itensMinifeast.get(i).getItem());
			 }
		chest.update();
	}
	
	public static void addChestItens(Chest chest) {	
		for (int i = 0; i < itensFeast.size(); i++) {
			 if (CommonsConst.RANDOM.nextInt(100) < itensFeast.get(i).getChance()) {
				 int slot = CommonsConst.RANDOM.nextInt(chest.getBlockInventory().getSize());
				 chest.getBlockInventory().setItem(slot, itensFeast.get(i).getItem());
			 }
		}
		chest.update();
	}
}