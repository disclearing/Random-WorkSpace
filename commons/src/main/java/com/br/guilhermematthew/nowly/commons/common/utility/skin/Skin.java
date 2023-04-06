package com.br.guilhermematthew.nowly.commons.common.utility.skin;

import lombok.Getter;

@Getter
public class Skin {

    private final String value, signature;
    private final String name;

    public Skin(String name, String value, String signature) {
        this.name = name;
        this.value = value;
        this.signature = signature;
    }
}