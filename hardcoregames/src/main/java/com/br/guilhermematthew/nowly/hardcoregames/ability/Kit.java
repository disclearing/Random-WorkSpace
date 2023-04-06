package com.br.guilhermematthew.nowly.hardcoregames.ability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.br.guilhermematthew.nowly.commons.bukkit.api.cooldown.CooldownAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.cooldown.types.Cooldown;
import com.br.guilhermematthew.nowly.commons.bukkit.api.item.ItemBuilder;
import com.br.guilhermematthew.nowly.commons.bukkit.api.protocol.ProtocolGetter;
import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesMain;
import com.br.guilhermematthew.nowly.hardcoregames.HardcoreGamesOptions;
import com.br.guilhermematthew.nowly.hardcoregames.events.game.GameStartedEvent;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;
import com.br.guilhermematthew.nowly.hardcoregames.manager.kit.KitLoader;
import com.br.guilhermematthew.nowly.hardcoregames.manager.kit.KitManager;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public abstract class Kit implements Listener {
	
	private String name;
	private List<String> description;
	private ItemStack icon;
	private int price, cooldownSeconds;
	private ItemStack[] itens;
	
	private final String itemColor = "§b";
	
	private boolean useInvincibility, callEventRegisterPlayer = false;
	private boolean listenerRegistred;
	private int amountUsing;
	
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
		setUseInvincibility(false);
		
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
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent event) {
		if (containsHability(event.getEntity())) {
			clean(event.getEntity());
		}
	}
	
	@EventHandler
	public void onStart(GameStartedEvent event) {
		setIcon(null);
	}
	
	public void setItens(ItemStack... itens) {
		this.itens = itens;
	}
	
	protected boolean containsHability(Player player) {
		return GamerManager.getGamer(player.getUniqueId()).containsKit(getName());
	}
	
    protected boolean useAbility(Player player) {
    	if (HardcoreGamesMain.getGameManager().isPreGame()) return false;
    	if (HardcoreGamesOptions.KITS_DISABLEDS) return false;
    	if (KitManager.getKitsDesativados().contains(getName().toLowerCase())) return false;
    	
    	if (HardcoreGamesMain.getGameManager().isInvencibilidade()) {
    		if (!isUseInvincibility()) {
    			return false;
    		}
    	}
    	
    	return containsHability(player);
    }
    
    public void registerListener() {
    	if (!listenerRegistred) {
    		listenerRegistred = true;
    		Bukkit.getPluginManager().registerEvents(this, HardcoreGamesMain.getInstance());
    		HardcoreGamesMain.console(getName() + " Listener has been registred!");
    	}
    }
    
	public void addUsing() {
		this.amountUsing++;
	}
    
    public void registerPlayer() {
    	this.amountUsing++;
    	
    	if (this.amountUsing == 1) {
    		if (!HardcoreGamesMain.getGameManager().isPreGame()) {
    			registerListener();
    		}
    	}
    }
    
    public void cleanPlayer(Player player) {
        CooldownAPI.removeAllCooldowns(player);
        
        clean(player);
    }
    
    public void unregisterPlayer(Player player) {
        CooldownAPI.removeAllCooldowns(player);
        
        clean(player);
        
        this.amountUsing--;
        
        if (this.amountUsing == 0) {
      		if (isListenerRegistred()) {
      			HardcoreGamesMain.console(getName() + " Listener has been unregistred!");
      			HandlerList.unregisterAll(this);
      			listenerRegistred = false;
      		}
        }
    }

    protected abstract void clean(Player player);

	protected boolean hasCooldown(Player player) {
        return CooldownAPI.hasCooldown(player, "Kit");
    }
    
    protected boolean hasCooldown(Player player, String cooldown) {
        return CooldownAPI.hasCooldown(player, cooldown);
    }
    
    protected void sendMessageCooldown(Player player) {
    	CooldownAPI.sendMessage(player, "Kit");
    }
    
    protected void sendMessageCooldown(Player player, String cooldown) {
    	CooldownAPI.sendMessage(player, cooldown);
    }
    
    protected void addCooldown(Player player, String cooldownName, long time) {
        if (CooldownAPI.hasCooldown(player, cooldownName)) {
            CooldownAPI.removeCooldown(player, cooldownName);
        }
        CooldownAPI.addCooldown(player, new Cooldown(cooldownName, time, playerOn18(player)));
    }
    
    protected void addCooldown(Player player, long time) {
        if (CooldownAPI.hasCooldown(player, "Kit")) {
            CooldownAPI.removeCooldown(player, "Kit");
        }
        CooldownAPI.addCooldown(player, new Cooldown("Kit", time, playerOn18(player)));
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