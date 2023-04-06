package io.github.guilhermematthew.anticheat.listeners;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bukkit.BukkitMain;
import com.br.guilhermematthew.nowly.commons.bukkit.account.BukkitPlayer;
import com.br.guilhermematthew.nowly.commons.common.profile.GamingProfile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.function.Predicate;

@Getter
public class ConstantListeners implements Listener {

    private String nameAt;
    @Setter
    private boolean alertCommon;
    @Setter
    private int maxAlerts = 10;

    public ConstantListeners() {
        nameAt = getClass().getSimpleName().replace("ConstantListeners", "");
    }

    public void alert(Player player) {
        alert(player, 0);
    }

    public void alert(Player player, int cpsCount) {
        broadcast(gamingProfile -> gamingProfile.isStaffer(), "§9Anticheat> §fO jogador §d" + player.getName() + "§f está usando §c" + getNameAt()
                + (cpsCount > 0 ? " §4(" + cpsCount + " cps)" : "") + " §7(" + 1 + "/" + maxAlerts + ")!");
    }

    public void broadcast(Predicate<? super GamingProfile> filter, String message) {
        CommonsGeneral.getProfileManager().getGamingProfiles().stream()
                .filter(gamingProfile -> gamingProfile.isStaffer())
                .forEach(gamingProfile -> gamingProfile.sendMessage(message));
    }


}
