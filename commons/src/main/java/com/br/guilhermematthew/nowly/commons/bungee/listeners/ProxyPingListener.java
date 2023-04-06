package com.br.guilhermematthew.nowly.commons.bungee.listeners;

import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.events.MotdCentralizer;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ProxyPingListener implements Listener
{
    private List<String> motds = Arrays.asList(
            "§d§lTODOS OS KITS GRATIS!",
            "§b§lMIX HG §e§lADICIONADO!",
            "§c§lNOVOS KITS NO HG!");
    public static HashMap<String, Integer> ping = new HashMap();

    @EventHandler(
            priority = -64
    )
    public void onProxyPing(ProxyPingEvent event) {
        String ipAddress = event.getConnection().getAddress().getHostString();
        if (ping.containsKey(ipAddress)) {
            ping.put(ipAddress, (Integer) ping.get(ipAddress) + 1);
        } else {
            ping.put(ipAddress, 1);
        }

        if ((Integer) ping.get(ipAddress) > 20) {
            event.setResponse((ServerPing) null);

        } else if (!BungeeMain.getManager().isGlobalWhitelist() == true) {
            String motd = ((String)this.motds.get((new Random()).nextInt(this.motds.size() - 1)));
            event.setResponse(new ServerPing(event.getResponse().getVersion(), new ServerPing.Players(60, BungeeCord.getInstance().getOnlineCount(), new ServerPing.PlayerInfo[]{new ServerPing.PlayerInfo("", "")}), MotdCentralizer.makeCenteredMotd("§2§lLEAGUE §8★ §aleaguemc.com.br") + "\n" + MotdCentralizer.makeCenteredMotd(motd), event.getResponse().getFaviconObject()));
        } else {
            event.setResponse(new ServerPing(event.getResponse().getVersion(), new ServerPing.Players(1, BungeeCord.getInstance().getOnlineCount(), new ServerPing.PlayerInfo[]{new ServerPing.PlayerInfo("", "")}), MotdCentralizer.makeCenteredMotd("§2§lLEAGUE §8★ §aleaguemc.com.br") + "\n" + MotdCentralizer.makeCenteredMotd("§c25/03 às 14:00"), event.getResponse().getFaviconObject()));
        }
    }

}
