package com.br.guilhermematthew.nowly.commons.bungee.skins;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.manager.premium.PremiumMapManager;
import com.br.guilhermematthew.nowly.commons.bungee.skins.MojangAPI.SkinRequestException;
import com.br.guilhermematthew.nowly.commons.common.connections.mysql.MySQLManager;
import com.br.guilhermematthew.nowly.commons.common.utility.Cache;
import com.br.guilhermematthew.nowly.commons.common.utility.skin.Skin;
import com.google.common.collect.Lists;
import lombok.val;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SkinStorage {

    private static final HashMap<String, Cache> CACHE = new HashMap<>();
    public static final HashMap<String, String> PLAYER_SKIN = new HashMap<>();
    private static Class<?> property;
    private static Object DEFAULT_SKIN = null;

    static {
        try {
            property = Class.forName("com.mojang.authlib.properties.Property");
        } catch (Exception e) {
            try {
                property = Class.forName("net.md_5.bungee.connection.LoginResult$Property");
            } catch (Exception ex) {
                try {
                    property = Class.forName("net.minecraft.util.com.mojang.authlib.properties.Property");
                } catch (Exception exc) {
                    System.out.println(
                            "[SkinsRestorer] Could not find a valid Property class! Plugin will not work properly");
                }
            }
        }
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

        if (callback != null) {
            callback.run();
        }
    }

    public static Object getDefaultSkin() {
        if (DEFAULT_SKIN == null)
            DEFAULT_SKIN = getSkinData("0171");

        return DEFAULT_SKIN;
    }

    public static void removeFromHash(final String nick) {
        PLAYER_SKIN.remove(nick);
    }

    public static Object createProperty(String name, String value, String signature) {
        try {
            return ReflectionUtil.invokeConstructor(property,
                    new Class<?>[]{String.class, String.class, String.class}, name, value, signature);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object createSkinIfNotCreated(final String nick) {
        try {
            return getOrCreateSkinForPlayer(nick);
        } catch (SkinRequestException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getOrCreateSkinForPlayer(final String name) throws SkinRequestException {
        String skin = getPlayerSkin(name);

        if (skin == null) skin = name.toLowerCase();

        Object textures = getSkinData(skin);

        if (textures != null) return textures;

        val premium = PremiumMapManager.getPremiumMap(name);
        if (premium != null && !premium.isPremium()) textures = getDefaultSkin();

        if (textures != null) return textures;

        String uuid = "N/A";
        val data = PremiumMapManager.getPremiumMap(name);
        if (data != null) uuid = String.valueOf(data.getUniqueId()).replaceAll("-", "");

        final String sname = skin;
        if (uuid.equals("N/A")) {
            uuid = MojangAPI.getUUID(sname);
        }

        textures = MojangAPI.getSkinProperty(uuid);

        setSkinData(sname, textures);
        return textures;
    }

    public static void createSkin(String name) throws SkinRequestException {
        Object textures;

        String uuid = "N/A";
        val data = PremiumMapManager.getPremiumMap(name);
        if (data != null) uuid = String.valueOf(data.getUniqueId()).replaceAll("-", "");

        try {
            if (uuid.equals("N/A")) {
                uuid = MojangAPI.getUUID(name);
            }

            textures = MojangAPI.getSkinProperty(uuid);

            setSkinData(name, textures);
        } catch (SkinRequestException e) {
            throw new SkinRequestException(e.getReason());
        } catch (Exception e2) {
            throw new SkinRequestException("aguarde");
        }
    }

    public static String getPlayerSkin(String name) {
        name = name.toLowerCase();

        if (PLAYER_SKIN.containsKey(name)) return PLAYER_SKIN.get(name);

        try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
            PreparedStatement preparedStatament = connection
                    .prepareStatement("SELECT * FROM playerSkin where nick='" + name + "';");

            ResultSet result = preparedStatament.executeQuery();

            if (!result.next()) {
                result.close();
                preparedStatament.close();
                PLAYER_SKIN.put(name, name);
                return name;
            }

            String skinUsing = result.getString("skin");
            result.close();
            preparedStatament.close();

            PLAYER_SKIN.put(name, skinUsing);
            return skinUsing;
        } catch (SQLException ex) {
            CommonsGeneral.console("Ocorreu um erro ao tentar obter a skin do jogador -> " + ex.getLocalizedMessage());
        }
        return name;
    }

    public static Object getSkinData(String name) {
        name = name.toLowerCase();

        Skin cachedSkin = null;

        if (CACHE.containsKey(name)) {
            cachedSkin = (Skin) CACHE.get(name).getValue1();
        }

        if (cachedSkin != null) {
            return createProperty("textures", cachedSkin.getValue(), cachedSkin.getSignature());
        }

        try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
            PreparedStatement preparedStatament = connection
                    .prepareStatement("SELECT * FROM skins where nick='" + name + "';");
            ResultSet result = preparedStatament.executeQuery();

            if (!result.next()) {
                result.close();
                preparedStatament.close();
                return null;
            }

            String value = result.getString("value"), signature = result.getString("signature"),
                    timestamp = result.getString("timestamp");

            result.close();
            preparedStatament.close();

            if (isOld(Long.parseLong(timestamp))) {
                Object skin = MojangAPI.getSkinProperty(MojangAPI.getUUID(name));
                if (skin != null) {
                    SkinStorage.setSkinData(name, skin);
                }
            } else {
                CACHE.put(name, new Cache(name, new Skin(name, value, signature), 3));
            }
            return createProperty("textures", value, signature);
        } catch (SQLException ex) {
            BungeeMain.console("Ocorreu um erro ao tentar obter a data de uma skin (SQL) -> " + ex.getLocalizedMessage());
            ex.printStackTrace();
        } catch (SkinRequestException ex) {
            ex.printStackTrace();
        }

        return getSkinData("0171");
    }

    public static boolean isOld(long timestamp) {
        return timestamp + TimeUnit.MINUTES.toMillis(1584) <= System.currentTimeMillis();
    }

    public static void removePlayerSkin(String name) {
        name = name.toLowerCase();

        PLAYER_SKIN.remove(name);

        MySQLManager.deleteFromTable("playerSkin", "nick", name);
    }

    public static void removeSkinData(String name) {
        final String name1 = name.toLowerCase();

        CACHE.remove(name);

        CommonsGeneral.runAsync(() -> MySQLManager.deleteFromTable("skins", "nick", name1));
    }

    public static void setPlayerSkin(String name, String skin) {
        name = name.toLowerCase();

        PLAYER_SKIN.put(name, skin);

        try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
            PreparedStatement preparedStatament = connection
                    .prepareStatement("SELECT * FROM playerSkin where nick='" + name + "';");
            ResultSet result = preparedStatament.executeQuery();

            if (!result.next()) {
                result.close();
                preparedStatament.close();

                PreparedStatement set = connection
                        .prepareStatement("INSERT INTO playerSkin (nick, skin) VALUES (?, ?)");

                set.setString(1, name);
                set.setString(2, skin);

                set.executeUpdate();
                set.close();
                return;
            }

            result.close();
            preparedStatament.close();

            PreparedStatement set = connection
                    .prepareStatement("UPDATE playerSkin SET skin=? where nick='" + name + "'");

            set.setString(1, skin);

            set.executeUpdate();
            set.close();
        } catch (SQLException ex) {
            CommonsGeneral.error("Erro ao setar skin de um jogador no MySQL -> " + ex.getLocalizedMessage());
        }
    }

    public static void setSkinData(String name, Object textures) {
        String name1 = name.toLowerCase();

        String value, signature, timestamp;

        try {
            value = (String) ReflectionUtil.invokeMethod(textures, "getValue");
            signature = (String) ReflectionUtil.invokeMethod(textures, "getSignature");
            timestamp = String.valueOf(System.currentTimeMillis());
        } catch (Exception ex) {
            return;
        }

        CACHE.put(name1, new Cache(name1, new Skin(name1, value, signature), 3));

        try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
            PreparedStatement preparedStatament = connection
                    .prepareStatement("SELECT * FROM skins where nick='" + name1 + "';");
            ResultSet result = preparedStatament.executeQuery();

            if (!result.next()) {
                result.close();
                preparedStatament.close();

                PreparedStatement set = connection.prepareStatement(
                        "INSERT INTO skins (nick, lastUse, value, signature, timestamp) VALUES (?, ?, ?, ?, ?)");

                set.setString(1, name1);
                set.setString(2, "" + System.currentTimeMillis());
                set.setString(3, value);
                set.setString(4, signature);
                set.setString(5, timestamp);

                set.executeUpdate();
                set.close();
                return;
            }

            result.close();
            preparedStatament.close();

            PreparedStatement set = connection.prepareStatement(
                    "UPDATE skins SET lastUse=?, value=?, signature=?, timestamp=? where nick='" + name1 + "'");

            set.setString(1, "" + System.currentTimeMillis());
            set.setString(2, value);
            set.setString(3, signature);
            set.setString(4, timestamp);

            set.executeUpdate();
            set.close();
        } catch (SQLException ex) {
            CommonsGeneral.console("Erro ao setar skin no MySQL -> " + ex.getLocalizedMessage());
        }
    }

    public void checkCache() {
        checkCache(null);
    }
}