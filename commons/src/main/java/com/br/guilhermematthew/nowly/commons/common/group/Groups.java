package com.br.guilhermematthew.nowly.commons.common.group;

import com.br.guilhermematthew.nowly.commons.common.tag.Tag;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum Groups {

    MEMBRO(new Tag("Membro", "", "§7", "Default")),

    VIP(new Tag("VIP", "VIP", "§a")),
    PRO(new Tag("Pro", "PRO", "§6")),
    ULTRA(new Tag("Ultra", "ULTRA", "§d")),
    LEAGUE(new Tag("League", "LEAGUE", "§a")),
    BETA(new Tag("Beta", "BETA", "§1")),

    PARTNER(new Tag("Partner", "PARTNER", "§b")),
    STREAMER(new Tag("Streamer", "STREAMER", "§3", "stream")),
    YOUTUBER(new Tag("Youtuber", "YT", "§b", "YT")),
    PRIME(new Tag("Prime", "Prime", "§3", "prime", "yt+", "ytplus")),
    TRIAL(new Tag("Trial", "TRIAL", "§d", "TRIAL")),
    MOD(new Tag("Mod", "MOD", "§5", "moderador")),
    MOD_PLUS(new Tag("Mod+", "MOD+", "§5", "moderador+")),
    DEVELOPER(new Tag("Developer", "DEV", "§4", "Dev", "Desenvolvedor")),

    ADMIN(new Tag("Admin", "ADMIN", "§4", "adm", "Administrador"));

    private final Tag tag;
    private List<String> permissions;

    Groups(Tag tag, List<String> permissions) {
        this.tag = tag;
        this.permissions = permissions;
    }

    Groups(Tag tag, String... permissions) {
        this.tag = tag;
        this.permissions = Arrays.asList(permissions);
    }

    public static Groups getGroup(String name) {
        Groups groupFinded = MEMBRO;

        for (Groups groups : values()) {
            if (groups.getTag().getName().equalsIgnoreCase(name)) {
                groupFinded = groups;
                break;
            }
            for (String aliases : groups.getTag().getAliases()) {
                if (aliases.equalsIgnoreCase(name)) {
                    groupFinded = groups;
                    break;
                }
            }
        }

        return groupFinded;
    }

    public static Boolean existGrupo(String grupo) {
        boolean existe = false;

        for (Groups groups : values()) {
            if (groups.getTag().getName().equalsIgnoreCase(grupo)) {
                existe = true;
                break;
            }
            for (String aliases : groups.getTag().getAliases()) {
                if (aliases.equalsIgnoreCase(grupo)) {
                    existe = true;
                    break;
                }
            }
        }
        return existe;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public int getLevel() {
        return getTag().getLevel();
    }

    public String getColor() {
        return getTag().getColor();
    }

    public String getName() {
        return getTag().getName();
    }
}