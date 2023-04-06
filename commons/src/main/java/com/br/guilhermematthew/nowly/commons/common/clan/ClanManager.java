package com.br.guilhermematthew.nowly.commons.common.clan;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class ClanManager {

    @Getter
    private static final HashMap<String, Clan> clans = new HashMap<>();

    public static void load(String name) {
        if (hasClanData(name)) return;

        try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
            PreparedStatement preparedStatament = connection.prepareStatement(
                    "SELECT * FROM clans WHERE (nome='" + name + "');");

            ResultSet result = preparedStatament.executeQuery();

            if (result.next()) {
                loadFromJSON(result.getString("nome"), CommonsConst.PARSER.parse(result.getString("data")).getAsJsonObject());
            }

            result.close();
            preparedStatament.close();
        } catch (SQLException ex) {
            CommonsGeneral.error("§cOcorreu um erro ao tentar carregar um clan... -> " + ex.getLocalizedMessage());
        }
    }

    private static void loadFromJSON(final String name, final JsonObject json) {
        if (json == null) return;

        Clan clan = hasClanData(name) ? getClan(name) : new Clan(name, json.get("tag").getAsString());

        clan.setOwner(json.get("dono").getAsString());
        clan.setTag(json.get("tag").getAsString());

        clan.setAdminList(StringUtility.formatStringToArrayWithoutSpace(json.get("admins").getAsString()));
        clan.setMemberList(StringUtility.formatStringToArrayWithoutSpace(json.get("membros").getAsString()));
        clan.setPremium(json.get("premium").getAsBoolean());

        if (!hasClanData(name)) {
            putClan(name, clan);
        }

        clan = null;
    }

    public static void removeClanData(String nome) {
        clans.remove(nome);
    }

    public static Clan getClan(String name) {
        if (!clans.containsKey(name)) {
            load(name);
        }

        return clans.get(name.toLowerCase());
    }

    public static void putClan(String name, Clan clan) {
        clans.put(name.toLowerCase(), clan);
    }

    public static void unloadClan(String name) {
        clans.remove(name.toLowerCase());
    }

    public static boolean hasClanData(String nome) {
        return clans.containsKey(nome.toLowerCase());
    }

    public static void saveClan(String clanName) {
        saveClan(clanName, false);
    }

    public static void saveClan(String clanName, boolean remove) {
        if (!hasClanData(clanName)) return;

        BungeeMain.runAsync(() -> {
            final Clan clan = getClan(clanName);

            try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
                PreparedStatement preparedStatament = connection.prepareStatement(
                        "UPDATE clans SET data=? WHERE nome='" + clanName + "'");

                preparedStatament.setString(1, clan.getJSON().toString());
                preparedStatament.execute();

                preparedStatament.close();
                preparedStatament = null;
            } catch (SQLException ex) {
                CommonsGeneral.error("Ocorreu um erro ao tentar salvar as estatistícas do clan " + clanName + " > " + ex.getLocalizedMessage());
            }
        });

        if (remove) {
            removeClanData(clanName);
        }
    }
}