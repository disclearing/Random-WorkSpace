package com.br.guilhermematthew.nowly.commons.common.connections.mysql;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import lombok.val;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLManager {

    public static int getPlayerPositionRanking(String name) {
        return getPlayerPosition("accounts", "xp", "nick", name);
    }

    public static int getPlayerPosition(String table, String field, String where, String name) {
        try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
            PreparedStatement preparedStatament = connection.prepareStatement(
                    "SELECT COUNT(*) FROM " + table + " WHERE data>'$." + field + "' > (SELECT data>'$." + field
                            + "' from " + table + " WHERE " + where + "='" + name + "')");

            ResultSet result = preparedStatament.executeQuery();

            if (!result.next()) {
                result.close();
                preparedStatament.close();
                return 0;
            }

            int pos = Integer.parseInt(result.getString("COUNT(*)")) + 1;

            result.close();
            preparedStatament.close();
            return pos;
        } catch (SQLException ex) {
            CommonsGeneral.console("Ocorreu um erro ao tentar obter uma posição. -> " + ex.getLocalizedMessage());
            return 0;
        }
    }

    public static int getPlayerPositionByColumn(String table, String field, String where, String name) {
        try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
            PreparedStatement preparedStatament = connection
                    .prepareStatement("SELECT COUNT(*) FROM " + table + " WHERE " + field + " > (SELECT " + field
                            + " from " + table + " WHERE " + where + "='" + name + "')");
            ResultSet result = preparedStatament.executeQuery();

            if (!result.next()) {
                result.close();
                preparedStatament.close();
                return 0;
            }

            int pos = Integer.parseInt(result.getString("COUNT(*)")) + 1;

            result.close();
            preparedStatament.close();
            return pos;
        } catch (SQLException ex) {
            CommonsGeneral.console("Ocorreu um erro ao tentar obter uma posiçao. -> " + ex.getLocalizedMessage());
            return 0;
        }
    }

    public static String getStringFromJSON(String table, String where, String whereValue, String field) {
        String string = "N/A";

        try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT data->'$." + field + "' FROM " + table + " WHERE " + where + "='" + whereValue + "'");

            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                string = result.getString("data->'$." + field + "'").replace("\"", "");
            }

            preparedStatement.close();
            result.close();
        } catch (SQLException ex) {
            CommonsGeneral.error("getString() : MySQLManager.Java -> " + ex.getLocalizedMessage());
        }

        return string;
    }

    public static String getString(String table, String where, String who, String toGet) {
        String string = "N/A";

        try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
            val statement = connection.prepareStatement("SELECT * FROM `" + table + "` WHERE `" + where + "`=?");
            statement.setString(1, who);
            val result = statement.executeQuery();

            if (result.next()) string = result.getString(toGet);

            statement.close();
            result.close();
        } catch (SQLException ex) {
            CommonsGeneral.error("getString() : MySQLManager.Java -> " + ex.getLocalizedMessage());
        }

        return string;
    }

    public static void deleteFromTable(String table, String where, String who) {
        containsAndUpdate(table, where, who, "DELETE FROM " + table + " WHERE " + where + "='" + who + "';");
    }

    public static boolean contains(String table, String where, String who) {
        boolean contains = false;

        try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM `" + table + "` WHERE `" + where + "`='" + who + "';");

            ResultSet result = preparedStatement.executeQuery();

            contains = result.next();

            preparedStatement.close();
            result.close();
        } catch (SQLException ex) {
            CommonsGeneral.error("contains() : MySQLManager.Java -> " + ex.getLocalizedMessage());
        }

        return contains;
    }

    public static boolean updateIfNotExists(String table, String where, String who, String update) {
        boolean updated = false;

        try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM `" + table + "` WHERE `" + where + "`='" + who + "';");

            ResultSet result = preparedStatement.executeQuery();

            if (!result.next()) {
                PreparedStatement updateStatement = connection.prepareStatement(update);

                updateStatement.executeUpdate();
                updateStatement.close();
                updated = true;
            }

            result.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            CommonsGeneral.error("updateIfNotExists() : MySQLManager.Java -> " + ex.getLocalizedMessage());
        }

        return updated;
    }

    public static void containsAndUpdate(String table, String where, String who, String update) {
        try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM `" + table + "` WHERE `" + where + "`=?");
            preparedStatement.setString(1, who);

            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                PreparedStatement updateStatement = connection.prepareStatement(update);

                updateStatement.executeUpdate();
                updateStatement.close();
            }

            result.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            CommonsGeneral.error("containsAndUpdate() : MySQLManager.Java -> " + ex.getLocalizedMessage());
        }
    }

    public static void updateValue(DataCategory dataCategory, DataType dataType, String value, String nick) {
        executeUpdate("UPDATE " + dataCategory.getTableName() + " SET " + dataType.getField() + "='" + value
                + "' where nick='" + nick + "';");
    }

    public static void updateStats(String table, String stats, String where, String who, String value) {
        executeUpdate("UPDATE " + table + " SET " + stats + "='" + value + "' where '" + where + "'='" + who + "';");
    }

    public static void updateStats(String table, String stats, String nick, String value) {
        executeUpdate("UPDATE " + table + " SET " + stats + "='" + value + "' where nick='" + nick + "';");
    }

    public static void executeUpdate(String... commands) {
        try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {

            //onHandleCommand
            for (String command : commands) {
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(command);
                    preparedStatement.executeUpdate();

                    preparedStatement.close();
                } catch (SQLException ex) {
                    CommonsGeneral.error("executeUpdate() : onHandleCommand : MySQLManager.Java -> " + ex.getLocalizedMessage());
                }
            }

        } catch (SQLException ex) {
            CommonsGeneral.error("executeUpdate() : MySQLManager.Java -> " + ex.getLocalizedMessage());
        }
    }
}