package com.br.guilhermematthew.nowly.commons.common.profile;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.common.data.Data;
import com.br.guilhermematthew.nowly.commons.common.data.DataHandler;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.profile.token.AcessToken;
import com.br.guilhermematthew.nowly.commons.common.profile.token.AcessTokenListener;
import com.br.guilhermematthew.nowly.commons.common.punishment.PunishmentHistoric;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import com.br.guilhermematthew.servercommunication.client.Client;
import com.br.guilhermematthew.servercommunication.common.packet.CommonPacket;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.UUID;

@Getter
@Setter
public class GamingProfile {

    private String nick;
    private UUID uniqueId;
    private String address;

    private DataHandler dataHandler;

    private AcessToken acessToken;
    private AcessTokenListener tokenListener;

    private PunishmentHistoric punishmentHistoric;

    public GamingProfile(final String nick, final String address, final UUID uniqueId) {
        this.nick = nick;
        this.uniqueId = uniqueId;
        this.address = address;

        this.dataHandler = new DataHandler(nick, uniqueId);
    }

    public GamingProfile(final String nick, final UUID uniqueId) {
        this(nick, "Unknown", uniqueId);
    }

    public Groups getGroup() {
        return Groups.getGroup(getString(DataType.GROUP));
    }

    public boolean isStaffer() {
        return getGroup().getLevel() >= Groups.PRIME.getLevel();
    }

    public boolean isVIP() {
        return getGroup().getLevel() > Groups.MEMBRO.getLevel();
    }

    public void sendPacket(final CommonPacket packet) {
        if (!CommonsGeneral.getPluginInstance().isBukkit()) return;

        Client.getInstance().getClientConnection().sendPacket(packet);
    }

    public PunishmentHistoric getPunishmentHistoric() {
        if (punishmentHistoric == null) {
            this.punishmentHistoric = new PunishmentHistoric(getNick(), getAddress());
        }
        return punishmentHistoric;
    }

    public boolean isExpired(final DataType dataType) {
        return isExpired(false, dataType);
    }

    public boolean isExpired(boolean update, final DataType dataType) {
        if (getLong(dataType) != 0L) {
            if (System.currentTimeMillis() > getLong(dataType)) {
                if (update) {
                    getData(dataType).setValue(0L);
                }
                return true;
            }
        }
        return false;
    }

    public boolean containsFake() {
        return !getString(DataType.FAKE).equals("");
    }

    public Data getData(DataType dataType) {
        return getDataHandler().getData(dataType);
    }

    public int getInt(DataType dataType) {
        return getDataHandler().getInt(dataType);
    }

    public String getIntFormatted(DataType dataType) {
        return StringUtility.formatValue(getDataHandler().getInt(dataType));
    }

    public String getString(DataType dataType) {
        return getDataHandler().getString(dataType);
    }

    public Boolean getBoolean(DataType dataType) {
        return getDataHandler().getBoolean(dataType);
    }

    public Long getLong(DataType dataType) {
        return getDataHandler().getLong(dataType);
    }

    public void remove(DataType dataType) {
        remove(dataType, 1);
    }

    public void remove(DataType dataType, int value) {
        getDataHandler().getData(dataType).remove(value);
    }

    public void add(DataType dataType) {
        add(dataType, 1);
    }

    public void add(DataType dataType, int value) {
        getDataHandler().getData(dataType).add(value);
    }

    public void set(DataType dataType, Object value) {
        getDataHandler().getData(dataType).setValue(value);
    }
}