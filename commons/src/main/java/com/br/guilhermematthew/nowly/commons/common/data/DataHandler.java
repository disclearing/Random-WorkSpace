package com.br.guilhermematthew.nowly.commons.common.data;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.common.PluginInstance;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import com.br.guilhermematthew.nowly.commons.custompackets.PacketType;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketCustomAction;
import com.br.guilhermematthew.servercommunication.client.Client;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class DataHandler {

    private final String nick;
    private final UUID uniqueId;

    private final Map<DataType, Data> datas;
    private final Map<DataCategory, Boolean> loadedCategories;

    public DataHandler(final String nick, final UUID uniqueId) {
        this.nick = nick;
        this.uniqueId = uniqueId;

        this.datas = new ConcurrentHashMap<>();
        this.loadedCategories = new ConcurrentHashMap<>();

        for (DataCategory dataCategory : DataCategory.values()) {
            this.loadedCategories.put(dataCategory, false);
        }
    }

    public void load(final DataCategory... dataCategory) throws SQLException {
        for (int i = 0; i < dataCategory.length; i++) {
            DataCategory current = dataCategory[i];

            try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM `" + current.getTableName() + "` WHERE `nick`='" + getNick() + "';");

                ResultSet result = preparedStatement.executeQuery();

                if (result.next()) {
                    loadFromJSONString(current, result.getString("data"));
                } else {
                    if (current.create()) {
                        insertOnTable(current);

                        datas.put(DataType.FIRST_LOGGED_IN, new Data(System.currentTimeMillis()));

                        for (DataType dataType : current.getDataTypes()) {
                            if (dataType == DataType.FIRST_LOGGED_IN) {
                                datas.put(dataType, new Data(System.currentTimeMillis()));
                            } else {
                                datas.put(dataType, new Data(dataType.getDefaultValue()));
                            }
                        }
                    }

                    getLoadedCategories().put(current, true);
                }

                preparedStatement.close();
                result.close();
            }
        }
    }

    private void insertOnTable(final DataCategory category) {
        CommonsGeneral.runAsync(() -> {
            try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
                PreparedStatement insert = connection.prepareStatement(createInsertIntoStringQuery(category));

                insert.execute();
                insert.close();

                insert = null;
            } catch (SQLException e) {
                CommonsGeneral.error("load() : insertOnTable() : DataHandler.java -> " + e.getLocalizedMessage());
            }
        });
    }

    public void saveCategorys(final DataCategory... dataCategorys) {
        try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
            for (int i = 0; i < dataCategorys.length; i++) {
                DataCategory dataCategory = dataCategorys[i];

                try {
                    PreparedStatement save = connection.prepareStatement(
                            "UPDATE " + dataCategory.getTableName() + " SET data=? where nick='" + getNick() + "'");

                    save.setString(1, createJSON(dataCategory, false).toString());

                    save.execute();
                    save.close();

                    save = null;
                } catch (SQLException ex) {
                    CommonsGeneral.error("Ocorreu um erro ao tentar salvar a categoria '" + dataCategory.getTableName()
                            + "' de " + getNick() + " -> " + ex.getLocalizedMessage());
                }
            }
        } catch (SQLException ex) {
            CommonsGeneral.error("Ocorreu um erro ao tentar abrir um pool de conexao " + ex.getLocalizedMessage());
        }

        sendCategoryToBungeecord(dataCategorys);
    }

    public void saveCategory(final DataCategory dataCategory) {
        try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
            PreparedStatement save = connection.prepareStatement(
                    "UPDATE " + dataCategory.getTableName() + " SET data=? where nick='" + getNick() + "'");

            save.setString(1, createJSON(dataCategory, false).toString());

            save.execute();
            save.close();

            save = null;
        } catch (SQLException ex) {
            CommonsGeneral.error("Ocorreu um erro ao tentar salvar a categoria '" + dataCategory.getTableName()
                    + "' de " + getNick() + " -> " + ex.getLocalizedMessage());
        }

        sendCategoryToBungeecord(dataCategory);
    }

    public void sendCategoryToBungeecord(final DataCategory... dataCategorys) {
        if (CommonsGeneral.getPluginInstance() == PluginInstance.BUKKIT) {

            CPacketCustomAction PACKET = new CPacketCustomAction(nick, uniqueId)
                    .type(PacketType.BUKKIT_SEND_UPDATED_DATA);

            int id = 1;

            for (int i = 0; i < dataCategorys.length; i++) {
                DataCategory dataCategory = dataCategorys[i];
                PACKET.getJson().addProperty("dataCategory-" + id, buildJSON(dataCategory, true).toString());
                id++;
            }

            Client.getInstance().getClientConnection().sendPacket(PACKET);
        }
    }

    public void loadFromJSON(final DataCategory dataCategory, final JsonObject json) {
        if (json == null)
            return;

        for (Entry<String, JsonElement> entry : json.entrySet()) {
            final DataType data = DataType.getDataTypeByField(entry.getKey());

            if (data == null) {
                continue;
            }

            if (!getDatas().containsKey(data)) {
                datas.put(data, new Data(data.getDefaultValue()));
            }

            final String classExpected = data.getClassExpected();

            if (data == DataType.PERMISSIONS) {
                getData(data).setValue(StringUtility.formatStringToArrayWithoutSpace(entry.getValue().getAsString()));
            } else {
                if (classExpected.equalsIgnoreCase("Boolean")) {
                    getData(data).setValue(entry.getValue().getAsBoolean());
                } else if (classExpected.equalsIgnoreCase("Int")) {
                    getData(data).setValue(entry.getValue().getAsInt());
                } else if (classExpected.equalsIgnoreCase("Long")) {
                    getData(data).setValue(entry.getValue().getAsLong());
                } else {
                    getData(data).setValue(entry.getValue().getAsString());
                }
            }
        }

        getLoadedCategories().put(dataCategory, true);

        for (DataType data : dataCategory.getDataTypes()) {
            if (!getDatas().containsKey(data)) {
                datas.put(data, new Data(data.getDefaultValue())); // FIX NOVAS COISAS NO JSON
            }
        }
    }

    public void loadFromJSONString(final DataCategory dataCategory, final String jsonString) {
        if (jsonString == null)
            return;

        if (jsonString.isEmpty())
            return;

        loadFromJSON(dataCategory, CommonsConst.PARSER.parse(jsonString).getAsJsonObject());
    }

    /**
     * SENDO USADO PARA ENVIAR CATEGORIAS ENTRE SERVER-CLIENT
     */
    public JsonObject buildJSON(final DataCategory dataCategory, boolean checkIfIsNotLoadedAndLoad) {
        JsonObject json = createJSON(dataCategory, checkIfIsNotLoadedAndLoad);
        json.addProperty("dataCategory-name", dataCategory.getTableName());
        return json;
    }

    public JsonObject createJSON(final DataCategory dataCategory, boolean checkIfIsNotLoadedAndLoad) {
        JsonObject json = new JsonObject();

        boolean loaded = isCategoryLoaded(dataCategory);

        if (checkIfIsNotLoadedAndLoad && !loaded) {
            try {
                load(dataCategory);
            } catch (SQLException ex) {
            }
        }

        for (DataType dataType : dataCategory.getDataTypes()) {
            if (dataType == DataType.PERMISSIONS) {
                json.addProperty(dataType.getField(),
                        loaded ? StringUtility.formatStringToArrayWithoutSpace(getData(dataType).getList()) : "");
            } else {
                if (dataType.getClassExpected().equalsIgnoreCase("Boolean")) {
                    json.addProperty(dataType.getField(), loaded ? getData(dataType).getBoolean() : false);
                } else if (dataType.getClassExpected().equalsIgnoreCase("Int")) {
                    json.addProperty(dataType.getField(), loaded ? getData(dataType).getInt() : 0);
                } else if (dataType.getClassExpected().equalsIgnoreCase("Long")) {
                    json.addProperty(dataType.getField(), loaded ? getData(dataType).getLong() : 0L);
                } else {
                    json.addProperty(dataType.getField(),
                            loaded ? getData(dataType).getString() : "" + dataType.getDefaultValue());
                }
            }
        }
        return json;
    }

    public void loadDefault(final DataCategory dataCategory) {
        for (DataType dataType : dataCategory.getDataTypes()) {
            if (!isCategoryLoaded(dataCategory)) {
                datas.put(dataType, new Data(dataType.getDefaultValue()));
            }
        }
    }

    public void reset() {
        for (DataCategory dataCategory : DataCategory.values()) {
            if (isCategoryLoaded(dataCategory)) {
                for (DataType data : dataCategory.getDataTypes()) {
                    datas.put(data, new Data(data.getDefaultValue()));
                }
                getLoadedCategories().put(dataCategory, false);
            }
        }
    }

    public String getIntFormatted(DataType dataType) {
        return StringUtility.formatValue(getInt(dataType));
    }

    public List<DataCategory> getListDataCategorysLoadeds() {
        List<DataCategory> list = new ArrayList<>();

        for (DataCategory datas : DataCategory.values()) {
            if (isCategoryLoaded(datas)) {
                list.add(datas);
            }
        }
        return list;
    }

    public void remove(DataType dataType) {
        remove(dataType, 1);
    }

    public void remove(DataType dataType, int value) {
        getData(dataType).remove(value);
    }

    public void add(DataType dataType) {
        add(dataType, 1);
    }

    public void add(DataType dataType, int value) {
        getData(dataType).add(value);
    }

    public void set(DataType dataType, Object value) {
        getData(dataType).setValue(value);
    }

    public boolean isCategoryLoaded(DataCategory category) {
        return this.loadedCategories.get(category);
    }

    public Data getData(DataType dataType) {
        return this.datas.get(dataType);
    }

    public int getInt(DataType dataType) {
        return this.datas.get(dataType).getInt();
    }

    public String getString(DataType dataType) {
        return this.datas.get(dataType).getString();
    }

    public Boolean getBoolean(DataType dataType) {
        return this.datas.get(dataType).getBoolean();
    }

    public Long getLong(DataType dataType) {
        return this.datas.get(dataType).getLong();
    }

    public String createInsertIntoStringQuery(final DataCategory category) {
        return "INSERT INTO `" + category.getTableName() + "` (`nick`, `data`) VALUES ('" + getNick() + "', '"
                + createJSON(category, false).toString() + "');";
    }

    public void sendCategoryToBungeecord(final List<DataCategory> list) {
        CPacketCustomAction PACKET = new CPacketCustomAction(nick, uniqueId).type(PacketType.BUKKIT_SEND_UPDATED_DATA);

        for (int i = 0; i < list.size(); i++) {
            PACKET.getJson().addProperty("dataCategory-" + (i + 1), buildJSON(list.get(i), true).toString());
        }

        Client.getInstance().getClientConnection().sendPacket(PACKET);
    }
}