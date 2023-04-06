package com.br.guilhermematthew.nowly.commons.common.serverinfo;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum ServerType {

    BUNGEECORD("BungeeCord", 2),
    LOGIN("Login", 2),
    LOBBY("Lobby", 2, "hub"),
    LOBBY_HARDCOREGAMES("LobbyHardcoreGames", 2, "LobbyHG", "lhg", "hubhg"),
    LOBBY_PVP("LobbyPvP", 2, "lpvp", "hubpvp", "LobbyPvP"),
    LOBBY_DUELS("LobbyDuels", 2, "lduel", "LobbyDuels", "hubduel"),

    DUELS_GLADIATOR("Gladiator", 2, "glad", "glading"),
    DUELS_SIMULATOR("Simulator", 2, "sim", "simulation"),

    PARTY_GAMES("PartyGames", 2, "pgames", "party", "gamesparty"),

    PVP_ARENA("Arena", 2),
    PVP_FPS("FPS", 2),
    PVP_LAVACHALLENGE("LavaChallenge", 2),

    GLADIATOR("Gladiator", 3, "glad"),

    HARDCORE_GAMES("HardcoreGames", 2, "hg", "HungerGames"),
    CHAMPIONS("Champions", 3, "champions", "champion"),
    EVENTO("Evento", 3, "evnt", "evento"),
    SKYWARS("SkyWars", 2, "sw"),
    BEDWARS("BedWars", 2, "bw"),
    THEBRIDGE("TheBridge", 2, "tb"),

    UNKNOWN("Unknown");

    private final String name;
    private final List<String> aliases;
    private final int secondsToStabilize;
    private final int secondsUpdateStatus;

    ServerType(String name, int secondsToStabilize, int secondsUpdateStatus, String... aliases) {
        this.name = name;
        this.aliases = Arrays.asList(aliases);
        this.secondsToStabilize = secondsToStabilize;
        this.secondsUpdateStatus = secondsUpdateStatus;
    }

    ServerType(String name, int secondsToStabilize) {
        this(name, secondsToStabilize, 2, "Unknown");
    }

    ServerType(String name) {
        this(name, 3, 2, "Unknown");
    }

    ServerType(String name, String... aliases) {
        this(name, 3, 2, aliases);
    }

    ServerType(String name, int secondsUpdateStatus, String... aliases) {
        this(name, 3, secondsUpdateStatus, aliases);
    }

    public static ServerType getServer(String serverName) {
        serverName = serverName.replaceAll("\\d", "");

        ServerType finded = ServerType.UNKNOWN;

        for (ServerType servers : ServerType.values()) {
            if (servers.getName().equalsIgnoreCase(serverName)) {
                finded = servers;
                break;
            }
            if (servers.containsAlias(serverName)) {
                finded = servers;
                break;
            }
            if (servers.getName().startsWith(serverName)) {
                finded = servers;
                break;
            }
        }
        return finded;
    }

    public boolean useActionItem() {
        if (isLobby()) return true;
        if (this == SKYWARS) return true;
        if (isPvP()) return true;
        if (this == GLADIATOR) return true;
        return isHardcoreGames();
    }

    public boolean useMenuListener() {
        if (isLobby()) return true;
        if (this == SKYWARS) return true;
        if (isPvP()) return true;
        return isHardcoreGames();
    }

    public boolean isLobby() {
        return this.getName().startsWith("Lobby");
    }

    public boolean isPvP() {
        return isPvP(false);
    }

    public boolean isPvP(boolean lobby) {
        if (lobby) {
            if (this == LOBBY_PVP) {
                return true;
            }
        }
        return this == PVP_ARENA || this == PVP_FPS || this == PVP_LAVACHALLENGE;
    }

    public boolean isHardcoreGames() {
        return isHardcoreGames(false);
    }

    public boolean isHardcoreGames(boolean lobby) {
        if (lobby) {
            if (this == LOBBY_HARDCOREGAMES) {
                return true;
            }
        }
        return this == HARDCORE_GAMES || this == EVENTO || this == CHAMPIONS;
    }

    public boolean containsAlias(String serverName) {
        if (getAliases() == null) return false;

        boolean finded = false;

        for (String alias : getAliases()) {
            if (alias.equalsIgnoreCase(serverName)) {
                finded = true;
                break;
            }
        }

        return finded;
    }

    public boolean useSuffixRank() {
        return isPvP(true) || isHardcoreGames(true);
    }
}