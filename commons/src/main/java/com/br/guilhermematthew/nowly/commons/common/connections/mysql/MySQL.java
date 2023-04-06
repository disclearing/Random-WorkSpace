package com.br.guilhermematthew.nowly.commons.common.connections.mysql;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQL {

    private HikariDataSource dataSource;

    @Getter
    @Setter
    private String host, porta, database, usuario, senha;

    public void openConnection() {
        CommonsGeneral.console("§eTentando estabelecer conexao com o MySQL...");

        if (dataSource != null)
            throw new IllegalStateException("Hikari already initialized");

        HikariConfig configuration = new HikariConfig();
        configuration.setJdbcUrl("jdbc:mysql://" + host + ":3306/" + "leaguemc" + "?characterEncoding=utf8&useSSL=false&useConfigs=maxPerformance");
        configuration.setUsername("root");
        configuration.setPassword(senha);

        configuration.setMaximumPoolSize(10);
        configuration.setMaxLifetime(3000000L);
        configuration.setLeakDetectionThreshold(50000000L);

        configuration.addDataSourceProperty("cachePrepStmts", "true");
        configuration.addDataSourceProperty("prepStmtCacheSize", "250");
        configuration.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(configuration);
    }

    public void createTables() {
        String[] categorysList = new String[DataCategory.values().length];

        int id = 0;
        for (DataCategory categorys : DataCategory.values()) {
            categorysList[id] = categorys.buildTableQuery();
            id++;
        }

        CommonsGeneral.runAsync(() -> {
            MySQLManager.executeUpdate(categorysList);

            MySQLManager.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS global_whitelist(identify varchar(20), actived boolean, nicks text);",
                    "CREATE TABLE IF NOT EXISTS accounts_to_delete(nick varchar(20), timestamp varchar(100));",
                    "CREATE TABLE IF NOT EXISTS clans(nome varchar(20), data JSON);",
                    "CREATE TABLE IF NOT EXISTS premium_map(nick varchar(20), uuid varchar(100), premium boolean);",
                    "CREATE TABLE IF NOT EXISTS playerSkin(nick varchar(16), skin varchar(16));",
                    "CREATE TABLE IF NOT EXISTS bans(nick varchar(20), address varchar(100), data JSON);",
                    "CREATE TABLE IF NOT EXISTS mutes(nick varchar(20), data JSON);",
                    "CREATE TABLE IF NOT EXISTS skins(nick varchar(16), lastUse varchar(100), value text, signature text, timestamp text);");
        });
    }

    public void closeConnection() {
        try {
            if (getConnection() != null) {
                getConnection().close();

                dataSource.close();
                dataSource = null;

                CommonsGeneral.console("§aConexao com o MySQL encerrada.");
            }
        } catch (SQLException ex) {
            CommonsGeneral.error("closeConnection() : MySQL.Java -> " + ex.getLocalizedMessage());
        }
    }

    public boolean isConnected() {
        try {
            getConnection();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) openConnection();

        return dataSource.getConnection();
    }
}