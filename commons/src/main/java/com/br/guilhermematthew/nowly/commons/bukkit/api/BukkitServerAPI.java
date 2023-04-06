package com.br.guilhermematthew.nowly.commons.bukkit.api;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMessages;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.profile.GamingProfile;
import com.br.guilhermematthew.nowly.commons.custompackets.PacketType;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketCustomAction;
import com.br.guilhermematthew.servercommunication.client.Client;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BukkitServerAPI {

    @Getter
    private static boolean registeredServer = false;

    public static void registerServer() {
        registeredServer = true;

        Client.getInstance().getClientConnection().sendPacket(new CPacketCustomAction(BukkitMain.getServerType(), BukkitMain.getServerID()).
                type(PacketType.BUKKIT_SEND_INFO).
                field("bukkit-register-server").
                fieldValue(Bukkit.getServer().getIp()).
                extraValue("" + Bukkit.getServer().getPort()));
    }

    public static void redirectPlayer(final Player player, final String server) {
        redirectPlayer(player, server, false);
    }

    public static boolean checkItem(ItemStack item, String display) {
        return (item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().startsWith(display));
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void redirectPlayer(final Player player, final String server, final boolean kick) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);

        player.sendPluginMessage(BukkitMain.getInstance(), "BungeeCord", out.toByteArray());

        player.sendMessage(BukkitMessages.CONNECTING);

        if (kick) {
            BukkitMain.runLater(() -> {
                if (player.isOnline()) {
                    if (server.equalsIgnoreCase("LobbyPvP") || (server.equalsIgnoreCase("LobbyHardcoreGames"))) {
                        player.sendMessage("§cOcorreu um erro ao tentar conectar-se ao Servidor: §7" + server);
                        redirectPlayer(player, "Lobby", true);
                    } else {
                        player.kickPlayer("§cOcorreu um erro ao tentar conectar-se ao servidor: " + server);
                    }
                }
            }, 40);
        }
    }

    @SuppressWarnings("unchecked")
    public static void unregisterCommands(String... commands) {
        try {
            Field firstField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            firstField.setAccessible(true);
            CommandMap commandMap = (CommandMap) firstField.get(Bukkit.getServer());
            Field secondField = commandMap.getClass().getDeclaredField("knownCommands");
            secondField.setAccessible(true);
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) secondField.get(commandMap);
            for (String command : commands) {
                if (knownCommands.containsKey(command)) {
                    knownCommands.remove(command);
                    List<String> aliases = new ArrayList<>();
                    for (String key : knownCommands.keySet()) {
                        if (!key.contains(":"))
                            continue;

                        String substr = key.substring(key.indexOf(":") + 1);
                        if (substr.equalsIgnoreCase(command)) {
                            aliases.add(key);
                        }
                    }
                    for (String alias : aliases) {
                        knownCommands.remove(alias);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removePlayerFile(UUID uuid) {
        World world = Bukkit.getWorlds().get(0);
        File folder = new File(world.getWorldFolder(), "playerdata");

        if (folder.exists() && folder.isDirectory()) {
            File file = new File(folder, uuid.toString() + ".dat");
            Bukkit.getScheduler().runTaskLaterAsynchronously(BukkitMain.getInstance(), () -> {
                if (file.exists() && !file.delete()) {
                    removePlayerFile(uuid);
                }
            }, 2L);
        }
    }

    public static void warnStaff(String message, Groups tag) {
        for (GamingProfile profiles : CommonsGeneral.getProfileManager().getGamingProfiles()) {
            if (profiles.getGroup().getLevel() >= tag.getLevel()) {
                Player target = Bukkit.getPlayer(profiles.getUniqueId());

                if (target != null) target.sendMessage(message);
            }
        }
    }

    public static Player getExactPlayerByNick(String nick) {
        Player finded = null;

        for (GamingProfile profiles : CommonsGeneral.getProfileManager().getGamingProfiles()) {
            if (profiles.getNick().equalsIgnoreCase(nick)) {
                finded = Bukkit.getPlayer(profiles.getUniqueId());
                break;
            }
        }

        return finded;
    }

    public static String getRealNick(Player target) {
        return CommonsGeneral.getProfileManager().getGamingProfile(target.getUniqueId()).getNick();
    }
}