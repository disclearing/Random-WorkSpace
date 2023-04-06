package com.br.guilhermematthew.nowly.commons.common.serverinfo.enums;

public enum GameStages {

    UNKNOWN(0, "Unknown"),
    LOADING(1, "Carregando"),
    WAITING(2, "Waiting"),
    INVINCIBILITY(3, "Invincibility"),
    PLAYING(4, "Playing"),
    END(5, "End"),
    OFFLINE(0, "Offline");

    private final String nome;
    private final int id;

    GameStages(final int id, final String nome) {
        this.nome = nome;
        this.id = id;
    }

    public static GameStages getStage(String nome) {
        GameStages finded = GameStages.UNKNOWN;

        for (GameStages estagios : GameStages.values()) {
            if (nome.equalsIgnoreCase(estagios.getNome())) {
                finded = estagios;
                break;
            }
        }
        return finded;
    }

    public String getNome() {
        return nome;
    }

    public int getId() {
        return id;
    }
}