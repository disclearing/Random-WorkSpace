package com.br.guilhermematthew.nowly.commons.bukkit.manager.configuration;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.listeners.DamageListener;
import com.br.guilhermematthew.nowly.commons.bukkit.manager.configuration.impl.BukkitConfiguration;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import lombok.Getter;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collections;

@Getter
public class BukkitConfigurationManager {

    private final BukkitConfiguration permissionsConfiguration;
    private final BukkitConfiguration globalConfiguration;
    private final BukkitConfiguration damageConfiguration;

    public BukkitConfigurationManager() {
        this.permissionsConfiguration = new BukkitConfiguration("bukkit-permissions", true);
        this.globalConfiguration = new BukkitConfiguration("global-config", true);
        this.damageConfiguration = new BukkitConfiguration("damages", true);
    }

    public void init() {
        getPermissionsConfiguration().load();
        getGlobalConfiguration().load();
        getDamageConfiguration().load();
    }

    public void refreshConfig(String configName) {
        boolean hasUpdate = false;

        if (configName.equalsIgnoreCase("bukkit-permissions")) {
            for (Groups groups : Groups.values()) {
                if (groups != Groups.DEVELOPER) {

                    if (!permissionsConfiguration.getConfiguration().contains("permissions." + groups.getName().toLowerCase())) {

                        permissionsConfiguration.getConfiguration().set("permissions." + groups.getName().toLowerCase(),
                                (groups == Groups.MEMBRO ? Collections.singletonList("tag.membro") : Arrays.asList("tag.membro", "tag." + groups.getName().toLowerCase())));

                        hasUpdate = true;
                    } else {
                        groups.setPermissions(permissionsConfiguration.getConfiguration().getStringList(
                                "permissions." + groups.getName().toLowerCase()));
                    }
                }
            }


            if (hasUpdate) {
                getPermissionsConfiguration().save();
            }

            getPermissionsConfiguration().unload();

            Groups.DEVELOPER.setPermissions(Groups.ADMIN.getPermissions());
        } else if (configName.equalsIgnoreCase("global-config")) {
            for (ValuesGlobalConfig values : ValuesGlobalConfig.values()) {
                if (!getGlobalConfiguration().getConfiguration().contains(values.getKey())) {
                    getGlobalConfiguration().getConfiguration().set(values.getKey(),
                            StringUtility.convertValue(values.getValue(), values.getClassExpected()));
                    hasUpdate = true;
                }
            }

            if (hasUpdate) {
                getGlobalConfiguration().save();
            }
        } else if (configName.equalsIgnoreCase("damages")) {
            for (ValuesDano values : ValuesDano.values()) {
                if (!getDamageConfiguration().getConfiguration().contains(values.getKey())) {
                    getDamageConfiguration().getConfiguration().set(values.getKey(), StringUtility.
                            convertValue(values.getValue(), values.getClassExpected()));
                    hasUpdate = true;
                }
            }

            for (Material materiais : Material.values()) {
                String name = materiais.name().toLowerCase();
                if ((name.contains("sword")) || (name.contains("pickaxe")) || (name.contains("spade")) || (name.contains("axe"))) {
                    if (!getDamageConfiguration().getConfiguration().contains("dano.materiais." + name)) {
                        hasUpdate = true;
                        getDamageConfiguration().getConfiguration().set("dano.materiais." + name, 1.0D);
                    }
                }
            }

            if (hasUpdate) {
                getDamageConfiguration().save();
            }
        }


        apply(configName);
    }

    private void apply(String configName) {
        if (configName.equalsIgnoreCase("global-config")) {
            CommonsGeneral.getMySQL().setHost(getStringByGlobalConfig("MySQL.Host"));
            CommonsGeneral.getMySQL().setDatabase(getStringByGlobalConfig("MySQL.Database"));
            CommonsGeneral.getMySQL().setUsuario(getStringByGlobalConfig("MySQL.Usuario"));
            CommonsGeneral.getMySQL().setSenha(getStringByGlobalConfig("MySQL.Senha"));
            CommonsGeneral.getMySQL().setPorta(getStringByGlobalConfig("MySQL.Porta"));
        } else if (configName.equalsIgnoreCase("damages")) {
            DamageListener.CRITICAL = getDamageConfiguration().getConfiguration().getBoolean("dano.critical");
            DamageListener.CHANCE_DE_CRITICAL = getDamageConfiguration().getConfiguration().getInt("dano.critical_chance");

            for (Material materiais : Material.values()) {
                String name = materiais.name().toLowerCase();
                if ((!name.contains("sword")) && (!name.contains("pickaxe")) && (!name.contains("spade")) && (!name.contains("axe"))) {
                    DamageListener.damageMaterial.put(materiais, 1.0D);
                    continue;
                }
                DamageListener.damageMaterial.put(materiais, getDamageConfiguration().getConfiguration().getDouble("dano.materiais." + name));
            }
        }
    }

    private String getStringByGlobalConfig(String type) {
        return getGlobalConfiguration().getConfiguration().getString(type);
    }

    public enum ValuesGlobalConfig {

        MYSQL_HOST("MySQL.Host", "localhost", "String"),
        MYSQL_PORTA("MySQL.Porta", "3306", "String"),
        MYSQL_DATABASE("MySQL.Database", "database", "String"),
        MYSQL_USUARIO("MySQL.Usuario", "root", "String"),
        MYSQL_SENHA("MySQL.Senha", "senha", "String");

        @Getter
        private final String key;
        @Getter
        private final String value;
        @Getter
        private final String classExpected;

        ValuesGlobalConfig(String key, String value, String classExpected) {
            this.key = key;
            this.value = value;
            this.classExpected = classExpected;
        }
    }

    public enum ValuesDano {

        CRITICAL("dano.critical", "true", "Boolean"),
        CRITICAL_CHANCE("dano.critical_chance", "35", "Integer");

        @Getter
        private final String key;
        @Getter
        private final String value;
        @Getter
        private final String classExpected;

        ValuesDano(String key, String value, String classExpected) {
            this.key = key;
            this.value = value;
            this.classExpected = classExpected;
        }
    }
}