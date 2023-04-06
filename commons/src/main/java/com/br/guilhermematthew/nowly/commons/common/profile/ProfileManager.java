package com.br.guilhermematthew.nowly.commons.common.profile;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ProfileManager {

    private final Map<UUID, GamingProfile> profiles = new ConcurrentHashMap<>();

    public void addGamingProfile(final UUID uniqueId, final GamingProfile profile) {
        profiles.put(uniqueId, profile);
    }

    public void removeGamingProfile(final UUID uniqueId) {
        profiles.remove(uniqueId);
    }

    public GamingProfile getGamingProfile(final String nick) {
        UUID uniqueId = CommonsGeneral.getUUIDFetcher().getOfflineUUID(nick);

        if (containsProfile(uniqueId)) return getGamingProfile(uniqueId);

        return null;
    }

    public GamingProfile getGamingProfile(final UUID uniqueId) {
        return profiles.get(uniqueId);
    }

    public boolean containsProfile(final UUID uniqueId) {
        return profiles.containsKey(uniqueId);
    }

    public Collection<GamingProfile> getGamingProfiles() {
        return profiles.values();
    }

    public boolean containsProfile(String name) {
        return containsProfile(CommonsGeneral.getUUIDFetcher().getOfflineUUID(name));
    }
}
