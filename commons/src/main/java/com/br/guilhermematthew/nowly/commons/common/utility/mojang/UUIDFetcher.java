package com.br.guilhermematthew.nowly.commons.common.utility.mojang;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.common.utility.Cache;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class UUIDFetcher {

    private static final HashMap<String, Cache> CACHE = new HashMap<>();
    private final List<String> apis = new ArrayList<>();

    public UUIDFetcher() {
        this.apis.add("https://api.mojang.com/users/profiles/minecraft/%s");
        this.apis.add("https://api.minetools.eu/uuid/%s");
    }

    public int getCacheSize() {
        return CACHE.size();
    }

    public void cleanCache() {
        CACHE.clear();
    }

    private UUID request(final String name) throws UUIDFetcherException {
        return this.request(0, this.apis.get(0), name);
    }

    public UUID getOfflineUUID(final String nick) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + nick).getBytes(Charsets.UTF_8));
    }

    private UUID request(int idx, String api, final String name) throws UUIDFetcherException {
        try {

            final URLConnection con = new URL(String.format(api, name)).openConnection();
            final JsonElement element = CommonsConst.PARSER.parse(new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8)));

            if (element instanceof JsonObject) {
                final JsonObject object = (JsonObject) element;

                if (object.has("error") && object.has("errorMessage")) {
                    throw new Exception(object.get("errorMessage").getAsString());
                }

                if (object.has("id")) {
                    return UUIDParser.parse(object.get("id"));
                }

                if (object.has("uuid")) {
                    final JsonObject uuid = object.getAsJsonObject("uuid");
                    if (uuid.has("formatted")) {
                        return UUIDParser.parse(object.get("formatted"));
                    }
                }
            }
        } catch (Exception e) {
            if (++idx < this.apis.size()) {
                api = this.apis.get(idx);
                return this.request(idx, api, name);
            } else {
                throw new UUIDFetcherException("Failed");
            }
        }
        return null;
    }

    public UUID getUUID(final String name) throws UUIDFetcherException {
        if (CACHE.containsKey(name)) return CACHE.get(name).getUUID();

        if (name.matches("\\w{3,16}")) {
            if (CACHE.containsKey(name)) return CACHE.get(name).getUUID();

            UUID uniqueId;

            try {
                uniqueId = request(name);

                if (uniqueId != null) {
                    CACHE.put(name, new Cache(name, uniqueId));
                    return uniqueId;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return UUIDParser.parse(name);
    }

    public void checkCache() {
        checkCache(null);
    }

    public void checkCache(Runnable callback) {
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
        if (callback != null) {
            callback.run();
        }
    }

    public static class UUIDFetcherException extends Exception {

        private static final long serialVersionUID = 1L;
        private final String reason;

        public UUIDFetcherException(String reason) {
            this.reason = reason;
        }

        public String getReason() {
            return reason;
        }

        public String getMessage() {
            return reason;
        }
    }
}