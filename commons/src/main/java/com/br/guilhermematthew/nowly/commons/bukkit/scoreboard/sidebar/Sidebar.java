package com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.sidebar;

import com.br.guilhermematthew.nowly.commons.bukkit.scoreboard.addons.Line;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Getter
public class Sidebar {

    private final Scoreboard scoreboard;
    private Objective objective;

    private boolean hided = true;

    private final HashMap<String, Line> lines;

    public Sidebar(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        this.lines = new HashMap<>();

        show();
    }

    public void show() {
        if (!hided)
            return;

        hided = false;
        this.objective = scoreboard.registerNewObjective("sidebar", "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void hide() {
        if (hided)
            return;

        hided = true;

        for (int i = 1; i < 16; i++) {
            Team team = scoreboard.getTeam("sidebar-" + i);
            if (team != null) {
                team.unregister();
                team = null;
            }
        }

        Objective sidebar = scoreboard.getObjective("sidebar");
        if (sidebar != null) {
            sidebar.unregister();
            sidebar = null;
        }

        this.lines.clear();
        this.objective = null;
        sidebar = null;
    }

    public void showHealth() {
        if (this.scoreboard.getObjective("showhealth") == null) {
            Objective obj = this.scoreboard.registerNewObjective("showhealth", "health");
            obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
            obj.setDisplayName("§c❤");
        }
    }

    public void addBlankLine() {
        int size = this.lines.size();
        addLine("blank-line-" + size, randomChar(size));
    }

    public void addLine(String text) {
        int size = this.lines.size();
        addLine("random-line-" + size, text);
    }

    public void addLine(String name, String prefix, String suffix) {
        this.lines.put(name.toLowerCase(), new Line(name, prefix, suffix, (this.lines.size() + 1)));
    }

    public void update() {
        if (isHided()) {
            return;
        }

        int size = Math.min(16, lines.size());

        int added = 0;

        List<Line> lineList = new ArrayList<>(this.lines.values());

        lineList.sort(Comparator.comparing(Line::getLineId));

        for (Line line : lineList) {
            String name = line.getName();

            if (name.length() > 16) {
                name = name.substring(0, 16);
            }

            int slot = size - added;

            line.setLineId(slot);
            setText(slot, line.getPrefix(), line.getSuffix());
            added++;
        }
    }

    public void updateLine(String lineName, String text) {
        String prefix = "";
        String suffix = "";

        if (text.length() <= 16) {
            prefix = text;
        } else {
            prefix = text.substring(0, 16);

            suffix = ChatColor.getLastColors(prefix) + text.substring(16);

            if (suffix.length() > 16) {
                suffix = suffix.substring(0, 16);
            }
        }

        updateLine(lineName, prefix, suffix);
    }

    public void addLine(String name, String text) {
        String prefix = "", suffix = "";

        if (text.length() <= 16) {
            prefix = text;
        } else {
            prefix = text.substring(0, 16);

            suffix = ChatColor.getLastColors(prefix) + text.substring(16);

            if (suffix.length() > 16) {
                suffix = suffix.substring(0, 16);
            }
        }

        addLine(name, prefix, suffix);
    }


    public void updateLine(String lineName, String prefix, String suffix) {
        if (isHided()) {
            return;
        }

        Line line = this.lines.getOrDefault(lineName.toLowerCase(), null);

        if (line == null) {
            return;
        }

        line.setPrefix(prefix);
        line.setSuffix(suffix);

        setText(line.getLineId(), line.getPrefix(), line.getSuffix());

        line = null;
    }

    private void setText(int line, String prefix, String suffix) {
        Team team = getScoreboard().getTeam("sidebar-" + line);

        if (team == null) {
            team = createTeam(line);
        }

        team.setPrefix(prefix);
        team.setSuffix(suffix);

        suffix = null;
        prefix = null;
    }

    private Team createTeam(int line) {
        Team team = getScoreboard().registerNewTeam("sidebar-" + line);

        String score = ChatColor.values()[line - 1].toString();

        getObjective().getScore(score).setScore(line);

        if (!team.hasEntry(score)) {
            team.addEntry(score);
        }

        score = null;
        return team;
    }

    public void setTitle(String name) {
        objective.setDisplayName(name);
    }

    public String randomChar(int slot) {
        return ChatColor.values()[slot].toString() + ChatColor.RESET;
    }
}