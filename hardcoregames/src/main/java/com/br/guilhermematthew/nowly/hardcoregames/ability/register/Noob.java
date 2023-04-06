package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Noob extends Kit {

    public Noob() {
        initialize(getClass().getSimpleName());

        setItens(new ItemBuilder().material(Material.STONE_AXE).name("§bMachado Noob").build());
        setItens(new ItemBuilder().material(Material.STONE_PICKAXE).name("§bPicareta Noob").build());
        setItens(new ItemBuilder().material(Material.STONE_HOE).name("§bFoice Noob").build());
        setItens(new ItemBuilder().material(Material.STONE_SWORD).name("§bEspada Noob").build());
        setItens(new ItemBuilder().material(Material.MUSHROOM_SOUP).amount(24).build());
    }

    @Override
    protected void clean(Player player) {
    }

}
