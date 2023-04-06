package com.br.guilhermematthew.nowly.commons.common.utility.skin;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.common.connections.mysql.MySQLManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class SkinFetcher {

    public static Skin fetchSkinByNick(String nick, String uuid, boolean defaultSkin) {
        try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
            PreparedStatement preparedStatament = connection
                    .prepareStatement("SELECT * FROM skins where nick='" + nick + "'");

            ResultSet result = preparedStatament.executeQuery();

            if (!result.next()) {
                CommonsGeneral.console("Skin de '" + nick + "' nao existe no banco de Dados.");

                result.close();
                preparedStatament.close();

                fetchSkinAndPut(nick, uuid);

                if (defaultSkin) {
                    return fetchSkinByNick("0171", "3650e36a-fe4f-4f0d-ba48-401cedb150e1", false);
                }
                return null;
            }

            String value = result.getString("value"), signature = result.getString("signature");

            result.close();
            preparedStatament.close();

            return new Skin(nick, value, signature);
        } catch (SQLException ex) {
            CommonsGeneral.console("Ocorreu um erro ao tentar obter a skin.");
            return null;
        }
    }

    public static void fetchSkinAndPut(String nick, String uuid) {
        new Thread(() -> {
            try {
                URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/"
                        + uuid.replaceAll("-", "") + "?unsigned=false");
                URLConnection uc = url.openConnection();
                uc.setUseCaches(false);
                uc.setDefaultUseCaches(false);
                uc.addRequestProperty("User-Agent", "Mozilla/5.0");
                uc.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
                uc.addRequestProperty("Pragma", "no-cache");

                String json = new Scanner(uc.getInputStream(), "UTF-8").useDelimiter("\\A").next();
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(json);
                JSONArray properties = (JSONArray) ((JSONObject) obj).get("properties");
                for (int i = 0; i < properties.size(); i++) {
                    try {
                        JSONObject property = (JSONObject) properties.get(i);
                        String value = (String) property.get("value");
                        String signature = property.containsKey("signature") ? (String) property.get("signature")
                                : null;

                        if (MySQLManager.updateIfNotExists("skins", "nick", nick,
                                "INSERT INTO skins(nick,lastUse,value,signature,timestamp) VALUES" + " ('" + nick
                                        + "', '" + System.currentTimeMillis() + "', '" + value + "','" + signature
                                        + "','" + (System.currentTimeMillis() + 60 * 60 * 24 * 3 * 1000) + "');")) {

                            CommonsGeneral.console("Skin '" + nick + "' foi adicionada ao Banco de Dados.");
                        }

                    } catch (Exception e) {
                        CommonsGeneral.console("Ocorreu um erro ao tentar parcelar as propriedades...");
                    }
                }
            } catch (Exception e) {
                CommonsGeneral.console(
                        "§cOcorreu um erro ao tentar conectar-se ao link para obter as informaçőes da Skin -> " + nick);
            }
        }).start();
    }
}