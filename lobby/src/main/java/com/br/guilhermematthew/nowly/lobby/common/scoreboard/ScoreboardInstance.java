package com.br.guilhermematthew.nowly.lobby.common.scoreboard;

import com.br.guilhermematthew.nowly.lobby.common.scoreboard.types.LobbyScoreboard;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ScoreboardInstance {

    private LobbyScoreboard lobbyBoard;

    public ScoreboardInstance() {
        setLobbyBoard(new LobbyScoreboard());
        getLobbyBoard().init();
    }

}
