package com.br.guilhermematthew.nowly.commons.bungee.manager.premium;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.common.connections.mysql.MySQLManager;
import com.br.guilhermematthew.nowly.commons.common.utility.Cache;
import com.br.guilhermematthew.nowly.commons.common.utility.mojang.UUIDFetcher.UUIDFetcherException;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.val;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PremiumMapManager {

    private static final HashMap<String, Cache> CACHE = new HashMap<>();

    @Getter
    private final static List<String> changedNicks = new ArrayList<>();
    private static final String[] tabelasToUpdate = {"accounts", "hardcoregames", "kitpvp", "gladiator", "premium_map",
            "bans", "mutes"};

    public static boolean load(String nick) throws UUIDFetcherException {
        boolean premium = false;

        UUID uuidProfile = null;

        try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
            PreparedStatement preparedStatament = connection
                    .prepareStatement("SELECT * FROM `premium_map` WHERE nick='" + nick + "'");

            ResultSet result = preparedStatament.executeQuery();

            if (result.next()) {
                premium = result.getBoolean("premium");
                uuidProfile = UUID.fromString(result.getString("uuid"));
            } else {
                UUID uuid = CommonsGeneral.getUUIDFetcher().getUUID(nick);
                premium = uuid != null;

                uuidProfile = (uuid != null ? uuid
                        : UUID.nameUUIDFromBytes(("OfflinePlayer:" + nick).getBytes(Charsets.UTF_8)));

                if (premium) {
                    if (hasChangedNick(uuidProfile, nick)) {
                        result.close();
                        preparedStatament.close();
                        changedNicks.add(nick);
                        return true;
                    }
                }

                if (!changedNicks.contains(nick)) {
                    PreparedStatement insert = connection
                            .prepareStatement("INSERT INTO premium_map(nick, uuid, premium) VALUES (?, ?, ?)");

                    insert.setString(1, nick);
                    insert.setString(2, String.valueOf(uuidProfile));
                    insert.setBoolean(3, premium);

                    insert.executeUpdate();
                    insert.close();
                }
            }

            result.close();
            preparedStatament.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        CACHE.put(nick.toLowerCase(), new Cache(nick, new PremiumMap(uuidProfile, nick, premium), 3));
        return true;
    }

    public static boolean hasChangedNick(UUID uuid, String newNick) {
        try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
            PreparedStatement preparedStatament = connection
                    .prepareStatement("SELECT * FROM `premium_map` WHERE uuid='" + uuid.toString() + "'");

            ResultSet result = preparedStatament.executeQuery();

            if (result.next()) {
                final String olderNick = result.getString("nick");

                result.close();
                preparedStatament.close();

                if (!olderNick.equalsIgnoreCase(newNick)) {
                    updateNick(uuid, olderNick, newNick);
                }
                return true;
            }
            result.close();
            preparedStatament.close();
            return false;
        } catch (SQLException ex) {
            return false;
        }
    }

    private static void updateNick(final UUID uuid, final String oldNick, final String newNick) {
        CACHE.put(newNick.toLowerCase(), new Cache(newNick, new PremiumMap(uuid, newNick, true), 3));

        if (!oldNick.equalsIgnoreCase(newNick)) {
            BungeeMain.console("[PremiumMap] detectou a mudança de Nick de " + oldNick + " para " + newNick + "!");

            CommonsGeneral.runAsync(() -> {
                for (String table : tabelasToUpdate) {
                    MySQLManager.containsAndUpdate(table, "nick", oldNick,
                            "UPDATE " + table + " SET nick='" + newNick + "' where nick='" + oldNick + "';");
                }
            });

            MySQLManager.containsAndUpdate("registros", "nick", oldNick,
                    "DELETE FROM registros WHERE nick='" + oldNick + "';");
        }
    }

    public static void removePremiumMap(String nickTarget) {
        CACHE.remove(nickTarget.toLowerCase());
    }

    public static int getCrackedAmount() {
        int crackedAmount = 0;

        for (Cache cache : CACHE.values()) {
            if (cache != null && cache.getValue1() != null && !((PremiumMap)cache.getValue1()).isPremium()) {
                crackedAmount++;
            }
        }

        return crackedAmount;
    }

    public static int getPremiumAmount() {
        int premiumAmont = 0;

        for (Cache cache : CACHE.values()) {
            if (cache != null && cache.getValue1() != null && ((PremiumMap)cache.getValue1()).isPremium()) {
                premiumAmont++;
            }
        }

        return premiumAmont;
    }

    public static PremiumMap getPremiumMap(String nick) {
        val data = CACHE.get(nick.toLowerCase());
        if (data == null) return null;

        return (PremiumMap) data.getValue1();
    }

    public static boolean containsMap(String nick) {
        return CACHE.containsKey(nick.toLowerCase());
    }

    public static long getPremiumMaps() {
        return CACHE.size();
    }

    public static void checkCache() {
        checkCache(null);
    }

    public static void checkCache(Runnable callback) {
        List<String> keys = Lists.newArrayList();

        for (Cache cache : CACHE.values()) {
            if (cache.isExpired()) {
                keys.add(cache.getName());
            }
        }

        if (keys.size() != 0) {
            for (String key : keys) {
                CACHE.remove(key);
            }
        }

        keys.clear();
        keys = null;

        if (callback != null) {
            callback.run();
        }
    }
}