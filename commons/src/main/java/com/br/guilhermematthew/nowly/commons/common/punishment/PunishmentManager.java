package com.br.guilhermematthew.nowly.commons.common.punishment;

import com.br.guilhermematthew.nowly.commons.common.punishment.types.Ban;
import com.br.guilhermematthew.nowly.commons.common.utility.Cache;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;

public class PunishmentManager {

    private static final HashMap<String, Cache> CACHE = new HashMap<>();

    public static Ban getBanCache(String nick) {
        if (CACHE.containsKey(nick.toLowerCase())) {
            return (Ban) CACHE.get(nick.toLowerCase()).getValue1();
        }

        return null;
    }

    public static void addCache(String nick, Ban ban) {
        CACHE.put(nick.toLowerCase(), new Cache(nick, ban, 1));
    }

    public static void removeCache(String nick) {
        CACHE.remove(nick.toLowerCase());
    }

    public static void checkCache() {
        checkCache(null);
    }

    public static void checkCache(Runnable callback) {
        List<String> keys = Lists.newArrayList();

        for (Cache cache : CACHE.values()) {
            if (cache.isExpired()) {
                keys.add(cache.getName());
            }
        }

        if (keys.size() != 0) {
            for (String key : keys) {
                CACHE.remove(key);
            }
        }

        keys.clear();
        keys = null;

        if (callback != null) {
            callback.run();
        }
    }
}