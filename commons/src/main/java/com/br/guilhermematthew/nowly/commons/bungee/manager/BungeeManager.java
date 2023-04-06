package com.br.guilhermematthew.nowly.commons.bungee.manager;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.manager.config.BungeeConfigurationManager;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class BungeeManager {

    private int actualMessageID, minutos;

    private BungeeConfigurationManager configManager;

    private boolean globalWhitelist;
    private List<String> whitelistPlayers;
    private ArrayList<String> messages;

    public BungeeManager() {
        this.configManager = new BungeeConfigurationManager();
        this.whitelistPlayers = new ArrayList<>();
        this.messages = new ArrayList<>();

        this.globalWhitelist = false;
    }

    public void init() {
        loadDefaults();
    }

    private void loadDefaults() {
        BungeeMain.runLater(() -> {
            BungeeMain.runAsync(() -> {

                try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM skins WHERE nick='0171'");
                    ResultSet result = preparedStatement.executeQuery();

                    if (!result.next()) {
                        PreparedStatement insert = connection.prepareStatement("INSERT INTO skins (nick, lastUse, value, signature, timestamp) VALUES (?, ?, ?, ?, ?)");

                        insert.setString(1, "0171");
                        insert.setString(2, String.valueOf(System.currentTimeMillis()));
                        insert.setString(3, "eyJ0aW1lc3RhbXAiOjE1NzUxNTE1Mjg5OTcsInByb2ZpbGVJZCI6IjM2NTBlMzZhZmU0ZjRmMGRiYTQ4NDAxY2VkYjE1MGUxIiwicHJvZmlsZU5hbWUiOiIwMTcxIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kYWFmOWZhZGQ3ZWQyMDAxZmUwYTlmZjI3ZmQxYmY3YjBhNjMyZjJlZmY3MGYxMjkzMGIzNGIzNWJlYWE4NjFkIn19fQ==");
                        insert.setString(4, "iEPj+++vKgJsMDXvgqjMZpag/Wzz/LLOjEK+OlUlf1Fn4vRFrfylMYlH+KopzX1TcdoY5vt1fEbgradTJEFUuFOXL7AI+RZYUoZ9mMbPpXn3Xbkhcw+Q5D9+EDV1WHVLXpnM3sMZPApMAGNMzOAUUChxQbz0HVrB3OjHtbbmkns2hecABaomRC9Gd4b8mWK/5u1gYUQEwF2I+VmJNuwkzOCUhMhMp4Z4RKg8vfePEXqi+cXD4p+phUU+CGxorHr3VakOI2RuGig8JQAI9L92q6C6yFL1j61nCUn1CVokHtGNFtLxE1ow6PuofLsWjR8+F0ksv/qk1jzYY3tucaXGuuz+QRiWTQiXGd+jp5oHFMx5IF77zU7naRJ4fNunhn3Z/AWQpV4v5huRemHe2QLSfPaICNe6cs9P11R/+EsEJs1R7cO9k3rfulMQPlKLHntI2vxVRnl3VqarD4r3o0sZJWfOp9g3xiKg3KTwx5d28zd7uZqmx+i2ien8Vz8J42/II8bGNP9GIxgbWAPRT2bERJca+lRwDnkS5CGA6QZB3euuWubBLPVV/oG7Q28Ij/MCMTOLoVrBIqM39punuEtULK57u/ERS4YkTIh7+j55tAl0lB9Vpo7Uu3yAPjOsG6OPMrZMUNgzi/PtC3KhWyYgFfGVUBNoIAdSuKs5C2JH1ps=");
                        insert.setString(5, String.valueOf(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(8)));

                        insert.executeUpdate();
                    }

                    preparedStatement.close();
                    result.close();

                    preparedStatement = connection.prepareStatement("SELECT * FROM global_whitelist WHERE identify='default'");
                    result = preparedStatement.executeQuery();

                    if (result.next()) {
                        this.globalWhitelist = result.getBoolean("actived");

                        for (String nick : StringUtility.formatStringToArrayWithoutSpace(result.getString("nicks"))) {
                            getWhitelistPlayers().add(nick.toLowerCase());
                        }
                    }

                    result.close();
                    preparedStatement.close();

                } catch (SQLException ex) {
                    BungeeMain.console("Ocorreu um erro ao tentar carregar coisas necessarias -> " + ex.getLocalizedMessage());
                }

                BungeeMain.setLoaded(true);
            });
        }, 2, TimeUnit.SECONDS);
    }

    @SuppressWarnings("deprecation")
    public void warnStaff(String message, Groups tag) {
        for (ProxiedPlayer proxiedPlayer : ProxyServer.getInstance().getPlayers()) {
            if (proxiedPlayer.hasPermission(CommonsConst.PERMISSION_PREFIX + ".receivewarn")) {
                if (BungeeMain.isValid(proxiedPlayer)) {
                    if (BungeeMain.getBungeePlayer(proxiedPlayer.getName()).getGroup().getLevel() >= tag.getLevel()) {
                        proxiedPlayer.sendMessage(message);
                    }
                }
            }
        }
    }
}