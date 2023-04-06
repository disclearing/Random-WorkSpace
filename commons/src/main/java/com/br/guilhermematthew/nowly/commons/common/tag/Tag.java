package com.br.guilhermematthew.nowly.commons.common.tag;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tag {

    public static final char[] chars = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    public static int globalLevels = 0;
    private String name, prefix, color, teamCharacter;
    private int level;
    private String[] aliases;

    public Tag(final String name, final String prefix, final String color, final String... aliases) {
        this.name = name;
        this.prefix = prefix;
        this.color = color;

        this.aliases = aliases;

        globalLevels++;
        this.level = globalLevels;
        this.teamCharacter = chars[33 - globalLevels] + "";
    }

    public Tag(final String name, final String prefix, final String color) {
        this(name, prefix, color, name);
    }

    public Tag(final String name, final String prefix) {
        this(name, prefix, prefix, name);
    }

    public boolean hasPrefix() {
        return !prefix.isEmpty() && prefix.length() > 1;
    }
}