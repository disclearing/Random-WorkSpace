package com.br.guilhermematthew.nowly.commons.common.punishment;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.common.punishment.types.Ban;
import com.br.guilhermematthew.nowly.commons.common.punishment.types.Mute;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PunishmentHistoric {

    private final String nick;
    private final String address;

    private final List<Ban> banHistory;
    private final List<Mute> muteHistory;

    public PunishmentHistoric(final String nick, final String address) {
        this.banHistory = new ArrayList<>();
        this.muteHistory = new ArrayList<>();

        this.nick = nick;
        this.address = address;
    }

    public PunishmentHistoric(final String nick) {
        this(nick, "");
    }

    public void loadMutes() throws SQLException {
        this.muteHistory.clear();

        try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {

            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM mutes WHERE nick='" + getNick() + "'");

            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                JsonObject json = CommonsConst.PARSER.parse(result.getString("data")).getAsJsonObject();

                this.muteHistory.add(new Mute(result.getString("nick"), json.get("mutedBy").getAsString(),
                        json.get("motive").getAsString(), json.get("mutedDate").getAsString(),

                        json.get("unmutedBy").getAsString(), json.get("unmutedDate").getAsString(),

                        json.get("punishmentTime").getAsLong(), json.get("unmutedTime").getAsLong()));

                json = null;
            }

            result.close();
            preparedStatement.close();

            result = null;
            preparedStatement = null;

        }
    }

    public void loadBans() throws SQLException {
        loadBans(false);
    }

    public void loadBans(boolean fromAddress) throws SQLException {
        this.banHistory.clear();

        try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bans WHERE "
                    + (fromAddress ? "address='" + getAddress() : "nick='" + getNick()) + "'");

            ResultSet result = preparedStatement.executeQuery();

            int FINDEDS = 0;

            while (result.next()) {
                JsonObject json = CommonsConst.PARSER.parse(result.getString("data")).getAsJsonObject();

                this.banHistory.add(new Ban(result.getString("nick"), result.getString("address"),
                        json.get("bannedBy").getAsString(), json.get("motive").getAsString(),
                        json.get("bannedDate").getAsString(),

                        json.get("unbannedBy").getAsString(), json.get("unbannedDate").getAsString(),

                        json.get("punishmentTime").getAsLong(), json.get("unbannedTime").getAsLong()));

                json = null;
                FINDEDS++;
            }

            preparedStatement.close();
            result.close();

            preparedStatement = null;
            result = null;

            if (FINDEDS == 0 && !fromAddress) {
                loadBans(true);
            }
        }
    }

    public Ban getActualBan() {
        for (Ban ban : banHistory) {
            if (!ban.isPunished())
                continue;
            if (ban.isExpired())
                continue;
            return ban;
        }
        return null;
    }

    public Mute getActualMute() {
        for (Mute mute : muteHistory) {
            if (!mute.isPunished())
                continue;
            if (mute.isExpired())
                continue;
            return mute;
        }
        return null;
    }

    public List<Ban> getBanHistory() {
        return banHistory;
    }

    public List<Mute> getMuteHistory() {
        return muteHistory;
    }

    public String getNick() {
        return nick;
    }

    public String getAddress() {
        return address;
    }
}