package com.br.guilhermematthew.nowly.hardcoregames.ability.register;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.br.guilhermematthew.nowly.hardcoregames.ability.Kit;
import com.br.guilhermematthew.nowly.hardcoregames.manager.gamer.GamerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.br.guilhermematthew.nowly.commons.bukkit.api.bossbar.BossBarAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent;
import com.br.guilhermematthew.nowly.commons.bukkit.custom.events.BukkitUpdateEvent.UpdateType;
import com.br.guilhermematthew.nowly.hardcoregames.events.player.PlayerDamagePlayerEvent;
import com.br.guilhermematthew.nowly.hardcoregames.events.player.PlayerRegisterKitEvent;

public class Madman extends Kit {

	public Madman() {
		initialize(getClass().getSimpleName());
		
		setCallEventRegisterPlayer(true);
	}

	private final ConcurrentHashMap<UUID, Integer> efeitoMadman = new ConcurrentHashMap<>();
	private final ArrayList<UUID> madmans = new ArrayList<>();

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSecond(BukkitUpdateEvent event) {
		if (event.getType() != UpdateType.SEGUNDO)
			return;

		if (madmans.size() > 0) {

			for (UUID uuidMadmans : madmans) {
				Player madman = Bukkit.getPlayer(uuidMadmans);

				if (madman != null) {
					List<Player> lista = getNearbyPlayers(madman, 15);

					if (lista.size() > 1) {
						for (Player perto : lista) {
							int efeito = lista.size() * 2;
							addEffect(perto, efeito);
						}
					}

					lista.clear();
					lista = null;

					madman = null;
				}
			}
		}

		if (efeitoMadman.size() > 0) {
			for (UUID uuidInfectados : efeitoMadman.keySet()) {
				removeEffect(uuidInfectados);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerDamage(PlayerDamagePlayerEvent event) {
		if (efeitoMadman.containsKey(event.getDamaged().getUniqueId())) {
			double dano = event.getDamage();
			event.setDamage(dano + ((dano / 100.0D) * efeitoMadman.get(event.getDamaged().getUniqueId())));
		}
	}

	@EventHandler
	public void onRegister(PlayerRegisterKitEvent event) {
		if (event.getKitName().equalsIgnoreCase(getName())) {
			if (!madmans.contains(event.getPlayer().getUniqueId())) {
				madmans.add(event.getPlayer().getUniqueId());
			}
		}
	}

	private void removeEffect(UUID u) {
		int effect = efeitoMadman.get(u);
		effect = effect - 10;
		efeitoMadman.put(u, effect);
		if (effect <= 0) {
			efeitoMadman.remove(u);
		}
	}

	private void addEffect(final Player player, int efeito) {
		int effect = (efeitoMadman.containsKey(player.getUniqueId()) ? efeitoMadman.get(player.getUniqueId()) : 0);

		if (effect == 0) {
			player.sendMessage("§cTem um §lMADMAN §cpor perto!");
		}

		effect = effect + (efeito + 10);

		BossBarAPI.send(player, "EFEITO DO MADMAN " + effect + "%", 5);

		efeitoMadman.put(player.getUniqueId(), effect);
	}

	private List<Player> getNearbyPlayers(Player p, int i) {
		List<Player> players = new ArrayList<Player>();
		List<Entity> entities = p.getPlayer().getNearbyEntities(i, i, i);

		for (Entity e : entities) {
			if (!(e instanceof Player)) {
				continue;
			}
			if (!GamerManager.getGamer(((Player) e).getUniqueId()).isPlaying()) {
				continue;
			}
			players.add((Player) e);
		}

		entities.clear();
		entities = null;
		return players;
	}

	@Override
	protected void clean(Player player) {
		if (efeitoMadman.containsKey(player.getUniqueId())) {
			efeitoMadman.remove(player.getUniqueId());
		}
	}
}