package com.br.guilhermematthew.nowly.commons.bungee.manager.config;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import lombok.Getter;

import java.util.Collections;

@Getter
public class BungeeConfigurationManager {

    private final BungeeConfiguration permissionsConfig;
    private final BungeeConfiguration bungeeConfig;

    public BungeeConfigurationManager() {
        this.permissionsConfig = new BungeeConfiguration("bungee-permissions");
        this.bungeeConfig = new BungeeConfiguration("bungee-config");
    }

    public void refreshConfig(final String configName) {
        if (configName.equalsIgnoreCase("bungee-permissions")) {

            boolean hasUpdate = false;

            for (Groups groups : Groups.values()) {
                if (groups != Groups.DEVELOPER) {

                    if (!permissionsConfig.getConfiguration().contains("permissions." + groups.getName().toLowerCase())) {
                        permissionsConfig.getConfiguration().set("permissions." + groups.getName().toLowerCase(), Collections.singletonList("teste.permission"));

                        hasUpdate = true;
                    } else {
                        groups.setPermissions(permissionsConfig.getConfiguration().getStringList(
                                "permissions." + groups.getName().toLowerCase()));
                    }

                }
            }


            if (hasUpdate) {
                getPermissionsConfig().save();
            }

            getPermissionsConfig().unload();

            Groups.DEVELOPER.setPermissions(Groups.ADMIN.getPermissions());
        } else if (configName.equalsIgnoreCase("bungee-config")) {

            boolean hasUpdate = false;

            for (ValuesBungeeConfig values : ValuesBungeeConfig.values()) {
                if (!getBungeeConfig().getConfiguration().contains(values.getKey())) {
                    getBungeeConfig().getConfiguration().set(values.getKey(), StringUtility.convertValue(values.getValue(), values.getClassExpected()));
                    hasUpdate = true;
                }
            }

            if (hasUpdate) {
                getBungeeConfig().save();
            }

            apply(configName);
        }
    }

    private void apply(String configName) {
        if (configName.equalsIgnoreCase("bungee-config")) {
            CommonsGeneral.getMySQL().setHost(getBungeeConfig().getConfiguration().getString("MySQL.Host"));
            CommonsGeneral.getMySQL().setDatabase(getBungeeConfig().getConfiguration().getString("MySQL.Database"));
            CommonsGeneral.getMySQL().setUsuario(getBungeeConfig().getConfiguration().getString("MySQL.Usuario"));
            CommonsGeneral.getMySQL().setSenha(getBungeeConfig().getConfiguration().getString("MySQL.Senha"));
            CommonsGeneral.getMySQL().setPorta(getBungeeConfig().getConfiguration().getString("MySQL.Porta"));

            BungeeMain.getManager().setMinutos(getBungeeConfig().getConfiguration().getInt("AutoMSG.Minutos"));
            BungeeMain.setSocketServerHost(getBungeeConfig().getConfiguration().getString("Socket.Host"));

            getBungeeConfig().unload();
        }
    }

    public enum ValuesBungeeConfig {

        MYSQL_HOST("MySQL.Host", "localhost", "String"),
        MYSQL_PORTA("MySQL.Porta", "3306", "String"),
        MYSQL_DATABASE("MySQL.Database", "dustmc", "String"),
        MYSQL_USUARIO("MySQL.Usuario", "root", "String"),
        MYSQL_SENHA("MySQL.Senha", "vooiid", "String"),

        AUTO_MSG_MINUTOS("AutoMSG.Minutos", "3", "Integer"),
        SOCKET_HOST("Socket.Host", "127.0.0.1", "String");

        @Getter
        private final String key;
        @Getter
        private final String value;
        @Getter
        private final String classExpected;

        ValuesBungeeConfig(String key, String value, String classExpected) {
            this.key = key;
            this.value = value;
            this.classExpected = classExpected;
        }
    }
}