package com.br.guilhermematthew.nowly.commons.bungee.skins;

import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.custompackets.PacketType;
import com.br.guilhermematthew.nowly.commons.custompackets.registry.CPacketCustomAction;
import com.br.guilhermematthew.servercommunication.server.Server;
import lombok.val;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.connection.LoginResult.Property;

public class SkinApplier {

    public static void handleSkin(PendingConnection pendingConnection) throws Exception {
        val textures = (Property) SkinStorage.getOrCreateSkinForPlayer(pendingConnection.getName());
        val handler = (InitialHandler) pendingConnection;
        if (textures == null) return;

        if (handler.isOnlineMode()) {
            handler.getLoginProfile().setProperties(new Property[]{textures});
        } else {
            val profile = getLoginProfile(pendingConnection.getName(), pendingConnection.getUniqueId().toString(), textures);
            ReflectionUtil.setObject(InitialHandler.class, pendingConnection, "loginProfile", profile);
        }
    }

    public static void fastApply(ProxiedPlayer proxiedPlayer, String skinToApply) {
        fastApply(proxiedPlayer, skinToApply, true);
    }

    public static void fastApply(ProxiedPlayer proxiedPlayer, String skinToApply, boolean sendPacket) {
        try {
            if (proxiedPlayer == null || proxiedPlayer.getServer() == null)
                return;

            Property textures = (Property) SkinStorage.getOrCreateSkinForPlayer(skinToApply);
            if (textures == null) return;

            val handler = (InitialHandler) proxiedPlayer.getPendingConnection();
            if (handler.isOnlineMode()) {
                handler.getLoginProfile().setProperties(new Property[]{textures});
            } else {
                val profile = getLoginProfile(handler.getName(), handler.getUniqueId().toString(), textures);
                ReflectionUtil.setObject(InitialHandler.class, handler, "loginProfile", profile);
            }

            if (sendPacket) {
                if (proxiedPlayer.getServer() == null)
                    return;

                Server.getInstance().sendPacket(proxiedPlayer.getServer().getInfo().getName(),
                        new CPacketCustomAction(proxiedPlayer.getUniqueId()).type(PacketType.BUKKIT_RECEIVE_SKIN_DATA)
                                .field("sendPacket")
                                .fieldValue(textures.getName())
                                .extraValue(textures.getValue())
                                .extraValue2(textures.getSignature()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void applySkin(ProxiedPlayer proxiedPlayer, String skinToApply) {
        BungeeMain.runAsync(() -> {
            try {
                if (proxiedPlayer == null || proxiedPlayer.getServer() == null)
                    return;

                Property textures = (Property) SkinStorage.getOrCreateSkinForPlayer(skinToApply);
                if (textures == null) return;

                val handler = (InitialHandler) proxiedPlayer.getPendingConnection();
                if (handler.isOnlineMode()) {
                    handler.getLoginProfile().setProperties(new Property[]{textures});
                } else {
                    val profile = getLoginProfile(handler.getName(), handler.getUniqueId().toString(), textures);
                    ReflectionUtil.setObject(InitialHandler.class, handler, "loginProfile", profile);
                }

                Server.getInstance().sendPacket(proxiedPlayer.getServer().getInfo().getName(),
                        new CPacketCustomAction(proxiedPlayer.getUniqueId()).type(PacketType.BUKKIT_RECEIVE_SKIN_DATA)
                                .field("sendPacket")
                                .fieldValue(textures.getName())
                                .extraValue(textures.getValue())
                                .extraValue2(textures.getSignature()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static LoginResult getLoginProfile(final String name, final String uniqueId, final Property textures) {
        return new LoginResult(uniqueId, name, new Property[]{textures});
    }

}