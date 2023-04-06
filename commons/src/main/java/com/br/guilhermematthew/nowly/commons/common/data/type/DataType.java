package com.br.guilhermematthew.nowly.commons.common.data.type;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public enum DataType {

    //ACCOUNT
    GROUP("membro", "String", "group", "VARCHAR(20)"),
    GROUP_TIME(0L, "Long", "grupo_time", "VARCHAR(100)"),
    GROUP_CHANGED_BY("", "String", "grupo_changed_by", "VARCHAR(20)"),
    CLAN("Nenhum", "String", "clan", "VARCHAR(20)"),
    FAKE("", "String", "fake", "VARCHAR(20)"),
    MEDAL(0, "Int", "medal", "int"),
    TAG("Membro", "String", "tag", "VARCHAR(64)"),
    DOUBLEXP(0, "Int", "double_xp_amount", "VARCHAR(100)"),
    DOUBLEXP_TIME(0L, "Long", "doublexp_time", "VARCHAR(100)"),
    DOUBLECOINS(0, "Int", "double_coins_amount", "int"),
    DOUBLECOINS_TIME(0L, "Long", "doublecoins_time", "VARCHAR(100)"),
    PERMISSIONS(new ArrayList<>(), "String", "permissions", "VARCHAR(2000)"),
    XP(0, "Int", "xp", "int"),
    COINS(0, "Int", "coins", "int"),
    CASH(0, "Int", "cash", "int"),
    LAST_IP("", "String", "last_ip", "VARCHAR(20)"),
    FIRST_LOGGED_IN(0L, "Long", "first_logged_in", "VARCHAR(100)"),
    LAST_LOGGED_IN(0L, "Long", "last_logged_in", "VARCHAR(100)"),
    LAST_LOGGED_OUT(0L, "Long", "last_logged_out", "VARCHAR(100)"),

    //HG
    HG_KILLS(0, "Int", "hg_kills", "int"),
    HG_DEATHS(0, "Int", "hg_deaths", "int"),
    HG_WINS(0, "Int", "hg_wins", "int"),
    HG_EVENT_WINS(0, "Int", "hg_events_wins", "int"),
    HG_EVENT_KILLS(0, "Int", "hg_events_kills", "int"),
    HG_EVENT_DEATHS(0, "Int", "hg_events_deaths", "int"),
    HG_DAILY_KIT("Nenhum", "String", "daily_kit", "VARCHAR(20)"),
    HG_DAILY_KIT_TIME(0L, "Long", "daily_kit_time", "VARCHAR(100)"),

    //GLADIATOR
    GLADIATOR_WINS(0, "Int", "gladiator_wins", "int"),
    GLADIATOR_LOSES(0, "Int", "gladiator_loses", "int"),
    GLADIATOR_WINSTREAK(0, "Int", "gladiator_winstreak", "int"),
    GLADIATOR_MAXWINSTREAK(0, "Int", "gladiator_max_killstreak", "int"),
    GLADIATOR_SLOTS("276:139:282:282:282:282:282:326:327:" +
            "306:307:308:309:281:351:351:351:274:" +
            "306:307:308:309:281:351:351:351:275:" +
            "327:327:282:282:282:282:282:282:5", "String", "slots", "VARCHAR(250)"),

    //KITPVP
    PVP_KILLS(0, "Int", "pvp_kills", "int"),
    PVP_DEATHS(0, "Int", "pvp_deaths", "int"),
    PVP_KILLSTREAK(0, "Int", "pvp_killstreak", "int"),
    PVP_MAXKILLSTREAK(0, "Int", "pvp_max_killstreak", "int"),
    FPS_KILLS(0, "Int", "fps_kills", "int"),
    FPS_DEATHS(0, "Int", "fps_deaths", "int"),
    FPS_KILLSTREAK(0, "Int", "fps_killstreak", "int"),
    FPS_MAXKILLSTREAK(0, "Int", "fps_max_killstreak", "int"),
    PVP_LAVACHALLENGE_FACIL(0, "Int", "pvp_lavachallenge_facil", "int"),
    PVP_LAVACHALLENGE_MEDIO(0, "Int", "pvp_lavachallenge_medio", "int"),
    PVP_LAVACHALLENGE_DIFICIL(0, "Int", "pvp_lavachallenge_dificil", "int"),
    PVP_LAVACHALLENGE_EXTREMO(0, "Int", "pvp_lavachallenge_extremo", "int"),
    PVP_LAVACHALLENGE_MORTES(0, "Int", "pvp_lavachallenge_mortes", "int"),

    //REGISTRO START
    REGISTRO_SENHA("", "String", "senha", "VARCHAR(150)"),
    REGISTRO_DATA("", "String", "registrado", "VARCHAR(25)"),
    //REGISTRO END

    //PREFERENCIAS START
    RECEIVE_PRIVATE_MESSAGES(true, "Boolean", "receiveMessages", "boolean"),
    RECEIVE_STAFFCHAT_MESSAGES(true, "Boolean", "receiveStaffchatMessages", "boolean"),
    RECEIVE_REPORTS(true, "Boolean", "receiveReports", "boolean"),
    PLAYERS_VISIBILITY(true, "Boolean", "playersVisibility", "boolean"),
    ANNOUNCEMENT_JOIN(true, "Boolean", "announcementJoin", "boolean"),
    CLAN_TAG_DISPLAY(true, "Boolean", "clanTagDisplay", "boolean");
    //PREFERENCIAS END;

    private final Object defaultValue;
    private final String classExpected;
    private final String field;
    private final String tableType;

    DataType(Object defaultValue, String classExpected, String field, String tableType) {
        this.defaultValue = defaultValue;
        this.field = field;
        this.classExpected = classExpected;
        this.tableType = tableType;
    }

    public static DataType getDataTypeByField(String field) {
        DataType finded = null;

        for (DataType datas : DataType.values()) {
            if (datas.getField().equalsIgnoreCase(field)) {
                finded = datas;
                break;
            }
        }

        return finded;
    }
}