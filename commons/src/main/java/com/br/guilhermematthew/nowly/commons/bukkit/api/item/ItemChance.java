package com.br.guilhermematthew.nowly.commons.bukkit.api.item;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class ItemChance {

    private int chance;
    private ItemStack item;
    private int randomStack;

    public ItemChance(ItemStack item, int chance, int randomStack) {
        this.item = item;
        this.chance = chance;
        this.randomStack = randomStack;
    }

    public ItemStack getItem() {
        if (randomStack != 0)
            return new ItemBuilder().type(item.getType()).durability(item.getDurability()).
                    amount(CommonsConst.RANDOM.nextInt(randomStack) + 1).build();
        return item;
    }
}