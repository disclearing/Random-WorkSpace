package com.br.guilhermematthew.nowly.commons.common.profile.addons;

import lombok.Getter;

@Getter
public enum Medals {

    BETA(1, "BETA", "§e", "❝"),
    CAFE(2, "CAFE", "§8", "☕");

    @Getter
    private final int id;

    @Getter
    private final String name;
    @Getter
    private final String color;
    @Getter
    private final String symbol;

    Medals(int id, String name, String color, String symbol) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.symbol = symbol;
    }

    public static Medals getMedalById(int id) {
        if (id == 0) return null; //FAST RESULT

        Medals medal = null;

        for (Medals medals : values()) {
            if (medals.getId() == id) {
                medal = medals;
                break;
            }
        }

        return medal;
    }

    public static Medals getMedalByName(String name) {
        Medals medal = null;

        for (Medals medals : values()) {
            if (medals.getName().equalsIgnoreCase(name)) {
                medal = medals;
                break;
            }
            if (medals.getName().startsWith(name)) {
                medal = medals;
                break;
            }
        }

        return medal;
    }

    public static Boolean existMedal(String name) {
        boolean existe = false;

        for (Medals medals : values()) {
            if (medals.getName().equalsIgnoreCase(name)) {
                existe = true;
                break;
            }
        }

        return existe;
    }
}