package com.br.guilhermematthew.nowly.commons.bungee.manager.premium;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PremiumMap {

    private boolean premium;
    private String nick;
    private UUID uniqueId;

    public PremiumMap(UUID uniqueId, String nick, boolean premium) {
        this.nick = nick;
        this.premium = premium;
        this.uniqueId = uniqueId;
    }
}