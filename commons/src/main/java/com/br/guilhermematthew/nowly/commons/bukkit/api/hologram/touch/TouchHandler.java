package com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.touch;

import com.br.guilhermematthew.nowly.commons.bukkit.api.hologram.Hologram;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public interface TouchHandler {

    void onTouch(@Nonnull Hologram hologram, @Nonnull Player player, @Nonnull TouchAction action);

}
