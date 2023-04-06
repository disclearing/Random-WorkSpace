package com.br.guilhermematthew.nowly.commons.common.utility.mojang;

import com.google.gson.JsonElement;

import java.util.UUID;

public class UUIDParser {

    public static UUID parse(final JsonElement element) {
        return parse(element.getAsString());
    }

    public static UUID parse(final String string) {
        if (string != null && !string.isEmpty()) {
            if (string.matches("[\\da-fA-F]{32}")) {
                return UUID.fromString(string.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
            }
            if (string.matches("[\\da-fA-F]{8}-[\\da-fA-F]{4}-[34][\\da-fA-F]{3}-[89ab][\\da-fA-F]{3}-[\\da-fA-F]{12}")) {
                return UUID.fromString(string);
            }
        }

        return null;
    }
}
