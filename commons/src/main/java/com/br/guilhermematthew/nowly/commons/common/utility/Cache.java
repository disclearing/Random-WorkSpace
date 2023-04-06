package com.br.guilhermematthew.nowly.commons.common.utility;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class Cache {

    private String name;

    private Object value1;
    private Object value2;

    private long createdTime;
    private long expireAfter;
    private int used;

    public Cache(String name, Object value1, Object value2, int expireAfter) {
        this.name = name;
        this.value1 = value1;
        this.value2 = value2;
        this.createdTime = System.currentTimeMillis();
        this.used = 0;
        this.expireAfter = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(expireAfter);
    }

    public Cache(String name, Object value1) {
        this(name, value1, null, 1);
    }

    public Cache(String name, Object value1, int expireAfter) {
        this(name, value1, null, expireAfter);
    }

    public Cache(String name, Object value1, Object value2) {
        this(name, value1, value2, 1);
    }

    public Boolean getBoolean1() {
        used++;
        this.expireAfter = expireAfter + TimeUnit.MINUTES.toMillis(5);
        return (Boolean) value1;
    }

    public Boolean getBoolean2() {
        used++;
        this.expireAfter = expireAfter + TimeUnit.MINUTES.toMillis(5);
        return (Boolean) value2;
    }

    public String getString1() {
        used++;
        this.expireAfter = expireAfter + TimeUnit.MINUTES.toMillis(5);
        return (String) value1;
    }

    public String getString2() {
        used++;
        this.expireAfter = expireAfter + TimeUnit.MINUTES.toMillis(5);
        return (String) value2;
    }

    public UUID getUUID() {
        used++;
        this.expireAfter = expireAfter + TimeUnit.MINUTES.toMillis(5);
        return (UUID) value1;
    }

    public boolean isExpired() {
        return expireAfter < System.currentTimeMillis();
    }
}