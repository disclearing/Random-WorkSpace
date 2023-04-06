package com.br.guilhermematthew.nowly.commons.bungee.utility.logfilter;

import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Filters extends PropagatingFilter {

    private final String[] messages = {"hasconnected", "hasdisconnected", "disconnectedwith", "nstoprocess!", "readtimedout",
            "connectionresetbypeer", "pinging", "initialhandler", "connectionwasaborted", "pluginviolation", "<->",
            "warning", "upstreambridge", "ioexception", "illegalthread", "bungeesecuritymanager", "plugin", "performed",
            "restricted", "action", "pinghandler", "peer", "readaddress", "(..)", "native", "upstream", "pinged", "remoto",
            "packet!", "to_server", "with:{1}", "[{0}]disconnectedwith:{1}", "{0}hasdisconnected", "{0}-badpacketid,aremodsinuse!?{1}",
            "{0}-corruptedframe:{1}", "{0}", "{0}-{1}:{2}", "{2}", "{1}", "event{0}", "mstoprocess!"};

    public Filters(BungeeMain plugin) {
        super(plugin.getProxy().getLogger());
    }

    Filters(Logger logger) {
        super(logger);
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        String message = record.getMessage().toLowerCase();

        boolean canLog = true;

        if (message.length() > 2) {
            if (message.contains("license")) {
                return true;
            }
            if (message.contains("commons")) {
                return true;
            }
            if (message.contains(" ")) {
                message = message.replaceAll(" ", "");
            }

            for (String filteredsMsgs : messages) {
                if (message.contains(filteredsMsgs)) {
                    canLog = false;
                    break;
                }
                if (message.equalsIgnoreCase(filteredsMsgs)) {
                    canLog = false;
                    break;
                }
                if (message.startsWith(filteredsMsgs)) {
                    canLog = false;
                    break;
                }
                if (message.endsWith(filteredsMsgs)) {
                    canLog = false;
                    break;
                }
            }
        }
        return canLog;
    }
}