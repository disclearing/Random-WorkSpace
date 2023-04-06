package com.br.guilhermematthew.nowly.commons.common.data.category;

import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import lombok.Getter;

import static com.br.guilhermematthew.nowly.commons.common.data.type.DataType.*;

@Getter
public enum DataCategory {

    ACCOUNT("accounts", GROUP, GROUP_TIME, GROUP_CHANGED_BY, CLAN, MEDAL, TAG, PERMISSIONS, FAKE, DOUBLEXP, DOUBLEXP_TIME, DOUBLECOINS, DOUBLECOINS_TIME,
            XP, COINS, CASH, LAST_IP, FIRST_LOGGED_IN, LAST_LOGGED_IN, LAST_LOGGED_OUT),

    KITPVP("kitpvp", PVP_KILLS, PVP_DEATHS, PVP_KILLSTREAK, PVP_MAXKILLSTREAK,
            FPS_KILLS, FPS_DEATHS, FPS_KILLSTREAK, FPS_MAXKILLSTREAK,
            PVP_LAVACHALLENGE_FACIL, PVP_LAVACHALLENGE_MEDIO, PVP_LAVACHALLENGE_DIFICIL, PVP_LAVACHALLENGE_EXTREMO, PVP_LAVACHALLENGE_MORTES),

    GLADIATOR("gladiator", GLADIATOR_WINS, GLADIATOR_LOSES, GLADIATOR_WINSTREAK, GLADIATOR_MAXWINSTREAK, GLADIATOR_SLOTS),

    HARDCORE_GAMES("hardcoregames", HG_KILLS, HG_DEATHS, HG_WINS, HG_EVENT_WINS, HG_EVENT_KILLS, HG_EVENT_DEATHS, HG_DAILY_KIT, HG_DAILY_KIT_TIME),

    PREFERENCES("preferences", RECEIVE_PRIVATE_MESSAGES, RECEIVE_STAFFCHAT_MESSAGES, RECEIVE_REPORTS, PLAYERS_VISIBILITY,
            ANNOUNCEMENT_JOIN, CLAN_TAG_DISPLAY),

    REGISTER("registros", REGISTRO_SENHA, REGISTRO_DATA);

    private final String tableName;
    private final DataType[] dataTypes;

    DataCategory(String tableName, DataType... dataTypes) {
        this.tableName = tableName;
        this.dataTypes = dataTypes;
    }

    public static DataCategory getCategoryByName(String name) {
        DataCategory finded = DataCategory.ACCOUNT;

        for (DataCategory datas : DataCategory.values()) {
            if (datas.getTableName().equalsIgnoreCase(name)) {
                finded = datas;
                break;
            }
        }

        return finded;
    }

    public boolean create() {
        return this != REGISTER;
    }

    public String buildTableQuery() {
        return "CREATE TABLE IF NOT EXISTS `" + getTableName() + "` (nick varchar(20), data JSON);";
    }
}