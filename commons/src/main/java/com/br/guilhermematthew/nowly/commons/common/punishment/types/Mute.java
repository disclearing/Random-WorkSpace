package com.br.guilhermematthew.nowly.commons.common.punishment.types;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.common.utility.system.DateUtils;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Getter
public class Mute {

    private final String nick;

    private final String mutedBy;
    private final String motive;
    private String mutedDate;
    private final long punishmentTime;

    private String unmutedBy;
    private String unmutedDate;
    private long unmutedTime;

    private boolean punished;

    public Mute(final String nick, final String mutedBy, final String motive, final long punishmentTime) {
        this.nick = nick;

        this.mutedBy = mutedBy;
        this.motive = motive;
        this.punishmentTime = punishmentTime;

        this.unmutedBy = "";
        this.unmutedDate = "";
        this.unmutedTime = 0L;
    }

    public Mute(final String nick, final String mutedBy, final String motive, final String mutedDate,
                final String unmutedBy, final String unmutedDate, final long punishmentTime, final long unmutedTime) {
        this.nick = nick;

        this.mutedBy = mutedBy;
        this.motive = motive;
        this.mutedDate = mutedDate;
        this.punishmentTime = punishmentTime;

        this.unmutedBy = unmutedBy;
        this.unmutedDate = unmutedDate;
        this.unmutedTime = unmutedTime;
    }

    public void mute() {
        if (!this.punished) {
            this.punished = true;

            this.mutedDate = DateUtils.getActualDate(false);

            CommonsGeneral.runAsync(() -> {
                try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
                    PreparedStatement insert = connection
                            .prepareStatement("INSERT INTO mutes(nick, data) VALUES (?, ?);");

                    insert.setString(1, this.nick);
                    insert.setString(2, buildData());

                    insert.execute();

                    insert.close();
                    insert = null;
                } catch (SQLException ex) {
                    CommonsGeneral.error("mute() : Mute.java -> " + ex.getLocalizedMessage());
                }
            });
        }
    }

    private String buildData() {
        JsonObject json = new JsonObject();

        json.addProperty("mutedBy", mutedBy);
        json.addProperty("motive", motive);
        json.addProperty("mutedDate", mutedDate);
        json.addProperty("punishmentTime", punishmentTime);

        json.addProperty("unmutedBy", unmutedBy);
        json.addProperty("unmutedDate", unmutedDate);
        json.addProperty("unmutedTime", unmutedTime);

        return json.toString();
    }

    public void unmute(final String unmutedBy) {
        if (this.punished) {
            this.punished = false;

            this.unmutedBy = unmutedBy;
            this.unmutedDate = DateUtils.getActualDate(false);
            this.unmutedTime = System.currentTimeMillis();

            CommonsGeneral.runAsync(() -> {
                try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
                    PreparedStatement insert = connection
                            .prepareStatement("UPDATE mutes SET data=? WHERE nick='" + getNick() + "'");

                    insert.setString(1, buildData());
                    insert.execute();

                    insert.close();

                    insert = null;
                } catch (SQLException ex) {
                    CommonsGeneral.error("unmute() : Mute.java -> " + ex.getLocalizedMessage());
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