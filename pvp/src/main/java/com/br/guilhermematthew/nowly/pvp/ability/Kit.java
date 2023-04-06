package com.br.guilhermematthew.nowly.pvp.ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.br.guilhermematthew.nowly.commons.bukkit.api.cooldown.CooldownAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.cooldown.types.Cooldown;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.api.protocol.ProtocolGetter;
import com.br.guilhermematthew.nowly.pvp.PvPMain;
import com.br.guilhermematthew.nowly.pvp.manager.gamer.GamerManager;
import com.br.guilhermematthew.nowly.pvp.manager.kit.KitLoader;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class Kit implements Listener {

	private int amountUsing;
	private String name;
	private List<String> description;
	private ItemStack icon;
	private int price, cooldownSeconds;
	private ItemStack[] itens;
	
	private final String itemColor = "§b";
	
	public void initialize(String kitName) {
		setName(kitName);
		
		ItemStack icone = new ItemBuilder().type(Material.getMaterial(
				getKitsConfig().getString("kits." + getName() + ".icon.material"))).
				durability(getKitsConfig().getInt("kits." + getName() + ".icon.durability")).
				amount(getKitsConfig().getInt("kits." + getName() + ".icon.amount"))
				.build();

		setIcon(icone);
		
		setItens(new ItemStack(Material.AIR));
		
		setPrice(getKitsConfig().getInt("kits." + getName() + ".price"));
		setCooldownSeconds(getKitsConfig().getInt("kits." + getName() + ".cooldown"));
		updateDescription(getKitsConfig().getStringList("kits." + getName() + ".icon.description"));
		
		icone = null;
	}
	
	public void updateDescription(List<String> list) {
		this.description = list;
		
		ItemMeta meta = getIcon().getItemMeta();

		List<String> lore = new ArrayList<String>();

		for (String descriptionL : this.description) {
			 lore.add(descriptionL.replaceAll("&", "§"));
		}

		meta.setDisplayName("§a" + getName());
		meta.setLore(lore);

		getIcon().setItemMeta(meta);
		setAmountUsing(0);
		
		lore.clear();
		
		meta = null;
		lore = null;
	}
	
	//CLEAN PLAYER FROM HASHMAPS, ETC.
    protected abstract void clean(Player player);

	public void setItens(ItemStack... itens) {
		this.itens = itens;
	}
	
    protected boolean hasAbility(Player player) {
        return GamerManager.getGamer(player.getUniqueId()).containsKit(getName());
    }
    
    public void registerPlayer() {
    	this.amountUsing++;
    	
    	if (this.amountUsing == 1) {
            Bukkit.getPluginManager().registerEvents(this, PvPMain.getInstance());
        }
    }
    
    public void unregisterPlayer(Player player) {
        if (CooldownAPI.hasCooldown(player, getName())) {
            CooldownAPI.removeCooldown(player, getName());
        }
        
        clean(player);
        
        this.amountUsing--;
        
    	if (amountUsing == 0) {
    	    HandlerList.unregisterAll(this);
        }
    }
    
	protected boolean hasCooldown(Player player, final String cooldownName) {
        return CooldownAPI.hasCooldown(player, cooldownName);
    }

	protected boolean hasCooldown(Player player) {
        return CooldownAPI.hasCooldown(player, getName());
    }
    
    protected void sendMessageCooldown(Player player) {
    	CooldownAPI.sendMessage(player, getName());
    }
    
    protected void addCooldown(Player player, long time) {
        if (CooldownAPI.hasCooldown(player, getName())) {
            CooldownAPI.removeCooldown(player, getName());
        }
        
        CooldownAPI.addCooldown(player, new Cooldown(getName(), time, playerOn18(player)));
    }
    
    protected void addCooldown(Player player, String cooldownName, long time) {
        if (CooldownAPI.hasCooldown(player, cooldownName)) {
            CooldownAPI.removeCooldown(player, cooldownName);
        }
        
        CooldownAPI.addCooldown(player, new Cooldown(cooldownName, time, playerOn18(player)));
    }

	public boolean playerOn18(final Player player) {
		return ProtocolGetter.getVersion(player) > 5;
	}
	
	public FileConfiguration getKitsConfig() {
		return KitLoader.getKitsConfig().getConfiguration();
	}
	
	public boolean checkItem(ItemStack item, String display) {
		return (item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
		&& item.getItemMeta().getDisplayName().startsWith(display));
	}
}