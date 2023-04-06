package com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.types;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.Hologram;
import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.HologramAPI;
import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.PlayerTop;
import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.view.ViewHandler;
import com.br.guilhermematthew.nowly.commons.bukkit.manager.configuration.PluginConfiguration;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.profile.GamingProfile;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SimpleHologram {

    private String name, display;
    private DataCategory dataCategory;
    private DataType dataType;
    private List<PlayerTop> topList;
    private final ViewHandler defaultViewHandler = (hologram, player, string) -> {
        int topId = Integer.parseInt(hologram.getName().replace("top", "").replace("-", "").replace(getName(), ""));

        PlayerTop playerTop = getTopList().get(topId - 1);

        return string.replace("%position%", "" + topId).replace("%playerName%", playerTop.getPlayerName()).replace("%value%", "" + playerTop.getValue());
    };
    private boolean created, updating;
    private Plugin instance;

    public SimpleHologram(String name, String display, DataCategory dataCategory, DataType dataType, Plugin instance) {
        this.name = name;
        this.display = display;
        this.dataCategory = dataCategory;
        this.dataType = dataType;
        this.instance = instance;
        this.created = false;
        this.updating = false;
        this.topList = new ArrayList<>();
    }

    public void create() {
        create("world");
    }

    public void recreate() {
        if (isCreated()) {
            for (Hologram holograms : HologramAPI.getHolograms()) {
                String holoName = holograms.getName().toLowerCase();

                if (holoName.equalsIgnoreCase("titulo-" + getName().toLowerCase())) {
                    holograms.update();
                } else if ((holoName.startsWith("top")) && (holoName.contains(getName().toLowerCase())) && (holoName.contains("-"))) {
                    holograms.update();
                } else if (holoName.contains("blank") && holoName.contains(getName().toLowerCase())) {
                    holograms.update();
                }
            }
        }

        create();
    }

    public void create(final String worldName) {
        if (isCreated()) return;

        PluginConfiguration.createLocation(getInstance(), "hologramas." + getName().toLowerCase(), worldName);

        Location location = PluginConfiguration.getLocation(getInstance(), "hologramas." + getName().toLowerCase());
        assert location != null;

        World world = location.getWorld();

        double y = location.getY();

        Hologram title = HologramAPI.createHologram("titulo-" + getName().toLowerCase(), location,
                "§e§lTOP 10 §b§l" + getDisplay().toUpperCase() + "§7(1/1)");
        y = y -= 0.25;

        title.spawn();

        title.addLineBelow("blank-" + getName().toLowerCase(), "");

        y = y -= 0.25;

        Hologram top1 =
                HologramAPI.createHologram("top1-" + getName(), new Location(world, location.getX(), y, location.getZ()), "§e%position%. %playerName% §7- §e%value%");
        top1.addViewHandler(defaultViewHandler);

        top1.spawn();
        y = y -= 0.25;

        Hologram top2 =
                HologramAPI.createHologram("top2-" + getName(), new Location(world, location.getX(), y, location.getZ()), "§e%position%. %playerName% §7- §e%value%");
        top2.addViewHandler(defaultViewHandler);
        top2.spawn();
        y = y -= 0.25;

        Hologram top3 =
                HologramAPI.createHologram("top3-" + getName(), new Location(world, location.getX(), y, location.getZ()), "§e%position%. %playerName% §7- §e%value%");
        top3.addViewHandler(defaultViewHandler);
        top3.spawn();
        y = y -= 0.25;

        Hologram top4 =
                HologramAPI.createHologram("top4-" + getName(), new Location(world, location.getX(), y, location.getZ()), "§e%position%. %playerName% §7- §e%value%");
        top4.addViewHandler(defaultViewHandler);
        top4.spawn();
        y = y -= 0.25;

        Hologram top5 =
                HologramAPI.createHologram("top5-" + getName(), new Location(world, location.getX(), y, location.getZ()), "§e%position%. %playerName% §7- §e%value%");
        top5.addViewHandler(defaultViewHandler);
        top5.spawn();
        y = y -= 0.25;

        Hologram top6 =
                HologramAPI.createHologram("top6-" + getName(), new Location(world, location.getX(), y, location.getZ()), "§e%position%. %playerName% §7- §e%value%");
        top6.addViewHandler(defaultViewHandler);
        top6.spawn();
        y = y -= 0.25;

        Hologram top7 =
                HologramAPI.createHologram("top7-" + getName(), new Location(world, location.getX(), y, location.getZ()), "§e%position%. %playerName% §7- §e%value%");
        top7.addViewHandler(defaultViewHandler);
        top7.spawn();
        y = y -= 0.25;

        Hologram top8 =
                HologramAPI.createHologram("top8-" + getName(), new Location(world, location.getX(), y, location.getZ()), "§e%position%. %playerName% §7- §e%value%");
        top8.addViewHandler(defaultViewHandler);
        top8.spawn();
        y = y -= 0.25;

        Hologram top9 =
                HologramAPI.createHologram("top9-" + getName(), new Location(world, location.getX(), y, location.getZ()), "§e%position%. %playerName% §7- §e%value%");
        top9.addViewHandler(defaultViewHandler);
        top9.spawn();
        y = y -= 0.25;

        Hologram top10 =
                HologramAPI.createHologram("top10-" + getName(), new Location(world, location.getX(), y, location.getZ()), "§e%position%. %playerName% §7- §e%value%");
        top10.addViewHandler(defaultViewHandler);
        top10.spawn();

        setCreated(true);
    }

    public void updateValues() {
        this.updating = true;

        new Thread(() -> {
            List<PlayerTop> updated = new ArrayList<>();

            String fieldReference = "data>'$.\"" + getDataType().getField() + "\"" + "'";

            try (Connection connection = CommonsGeneral.getMySQL().getConnection()) {
                PreparedStatement preparedStatament = connection.prepareStatement(
                        "SELECT nick,data" + " FROM " + getDataCategory().getTableName() + " ORDER BY " + fieldReference + " DESC");

                ResultSet result = preparedStatament.executeQuery();

                int id = 0;

                while (result.next()) {
                    id++;

                    String playerName = result.getString("nick");

                    UUID uniqueId = CommonsGeneral.getUUIDFetcher().getOfflineUUID(playerName);

                    GamingProfile profile = CommonsGeneral.getProfileManager().getGamingProfile(uniqueId);

                    if (profile == null) {
                        profile = new GamingProfile(playerName, uniqueId);

                        if (getDataCategory() != DataCategory.ACCOUNT) {
                            if (!profile.getDataHandler().isCategoryLoaded(DataCategory.ACCOUNT)) {
                                profile.getDataHandler().load(DataCategory.ACCOUNT);
                            }
                        }

                        profile.getDataHandler().loadFromJSONString(getDataCategory(), result.getString("data"));
                    }

                    Groups group = profile.getGroup();
                    val top = new PlayerTop(group.getTag().getColor() + playerName, profile.getInt(getDataType()));

                    if(updated.isEmpty()) updated.add(top);
                    else if(!updated.get(updated.size() - 1).getPlayerName().equals(top.getPlayerName())) updated.add(top);
                }

                result.close();
                preparedStatament.close();

                while (id < 10) {
                    id++;
                    updated.add(new PlayerTop("§7Ninguém", 0));
                }

                topList.clear();

                updated.sort(PlayerTop::compareTo);
                topList.addAll(updated.subList(0, 10));
                recreate();

                updated.clear();

                this.updating = false;
            } catch (SQLException ex) {
                BukkitMain.console("§cOcorreu um erro ao tentar atualizar o TOP 10 '" + getName().toUpperCase() + "' -> " + ex.getLocalizedMessage());
                ex.printStackTrace();
            }
        }).start();
    }
}