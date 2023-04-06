package com.br.guilhermematthew.nowly.commons.common.command;

import java.util.UUID;

public interface CommandSender {

    UUID getUniqueId();

    boolean isPlayer();
}
