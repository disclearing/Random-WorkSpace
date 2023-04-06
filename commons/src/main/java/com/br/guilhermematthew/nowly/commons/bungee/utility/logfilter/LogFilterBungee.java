package com.br.guilhermematthew.nowly.commons.bungee.utility.logfilter;

import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;

import java.util.LinkedList;
import java.util.List;

public class LogFilterBungee {

    private static final List<InjectableFilter> filters = new LinkedList<>();

    public static void load(BungeeMain plugin) {
        filters.clear();

        filters.add(new Filters(plugin));

        for (InjectableFilter filter : filters) {
            filter.inject();
        }
    }

    public static void unload() {
        for (InjectableFilter filter : filters) {
            filter.reset();
        }
    }
}