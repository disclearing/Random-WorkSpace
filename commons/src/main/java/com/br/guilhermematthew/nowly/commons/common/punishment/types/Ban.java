package com.br.guilhermematthew.nowly.commons.common.punishment.types;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.common.utility.system.DateUtils;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Getter
public class Ban {

    private final String nick;
    private final String address;

    private final String bannedBy;
    private final String motive;
    private String bannedDate;
    private final long punishmentTime;

    private String unbannedBy;
    private String unbannedDate;
    private long unbannedTime;

    private boolean punished;

    public Ban(final String nick, final String address, final String bannedBy, final String motive, final long punishmentTime) {
        this.nick = nick;
        this.address = address;

        this.bannedBy = bannedBy;
        this.motive = motive;
        this.punishmentTime = punishmentTime;

        this.unbannedBy = "";
        this.unbannedDate = "";
        this.unbannedTime = 0L;
    }

    public Ban(final String nick, final String address, final String bannedBy, final String motive, final String bannedDate,
               final String unbannedBy, final String unbannedDate, final long punishmentTime, final long unbannedTime) {
        this.nick = nick;
        this.address = address;

        this.bannedBy = bannedBy;
        this.motive = motive;
        this.bannedDate = bannedDate;
        this.punishmentTime = punishmentTime;

        this.unbannedBy = unbannedBy;
        this.unbannedDate = unbannedDate;
        this.unbannedTime = unbannedTime;
    }

    public void ban() {
        if (!this.punished) {
            this.punished = true;

            this.bannedDate = DateUtils.getActualDate(false);

            CommonsGeneral.runAsync(() -> {
                try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
                    PreparedStatement insert = connection.prepareStatement(
                            "INSERT INTO bans(nick, address, data) VALUES (?, ?, ?);");

                    insert.setString(1, this.nick);
                    insert.setString(2, this.address);
                    insert.setString(3, buildData());
                    insert.execute();

                    insert.close();
                    insert = null;
                } catch (SQLException ex) {
                    CommonsGeneral.error("ban() : Ban.java -> " + ex.getLocalizedMessage());
                }
            });
        }
    }

    private String buildData() {
        JsonObject json = new JsonObject();

        json.addProperty("bannedBy", bannedBy);
        json.addProperty("motive", motive);
        json.addProperty("bannedDate", bannedDate);
        json.addProperty("punishmentTime", punishmentTime);

        json.addProperty("unbannedBy", unbannedBy);
        json.addProperty("unbannedDate", unbannedDate);
        json.addProperty("unbannedTime", unbannedTime);

        return json.toString();
    }

    public void unban(final String unbannedBy) {
        if (this.punished) {
            this.punished = false;

            this.unbannedBy = unbannedBy;
            this.unbannedDate = DateUtils.getActualDate(false);
            this.unbannedTime = System.currentTimeMillis();

            CommonsGeneral.runAsync(() -> {
                try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
                    PreparedStatement insert = connection.prepareStatement(
                            "UPDATE bans SET data=? WHERE nick='" + getNick() + "'");

                    insert.setString(1, buildData());
                    insert.execute();

                    insert.close();
                    insert = null;
                } catch (SQLException ex) {
                    CommonsGeneral.error("unban() : Ban.java -> " + ex.getLocalizedMessage());
                }
            });
        }
    }

    public boolean isExpired() {
        return !isPermanent() && getPunishmentTime() < System.currentTimeMillis();
    }

    public boolean isPermanent() {
        return getPunishmentTime() == 0;
    }
}