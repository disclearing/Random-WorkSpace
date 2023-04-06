package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.bungee.account.BungeePlayer;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.data.category.DataCategory;
import com.br.guilhermematthew.nowly.commons.common.data.type.DataType;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import lombok.val;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReportCommand implements CommandClass {

    @SuppressWarnings("deprecation")
    @Command(name = "report", aliases = {"reportar", "rp"})
    public void execute(BungeeCommandSender commandSender, String ignored, String[] args) {
        if (!commandSender.isPlayer()) return;

        ProxiedPlayer proxiedPlayer = commandSender.getPlayer();

        if (args.length < 1) {
            proxiedPlayer.sendMessage(BungeeMessages.COMMAND_REPORT_USAGE);
            return;
        }

        if(args[0].equalsIgnoreCase("toggle") && proxiedPlayer.hasPermission(CommonsConst.PERMISSION_PREFIX + ".receivereport")) {
            val bp = BungeeMain.getBungeePlayer(proxiedPlayer.getName());

            if(bp.getBoolean(DataType.RECEIVE_REPORTS)) {
                bp.set(DataType.RECEIVE_REPORTS, false);
                proxiedPlayer.sendMessage(BungeeMessages.REPORT_DISABLED);
            } else {
                bp.set(DataType.RECEIVE_REPORTS, true);
                proxiedPlayer.sendMessage(BungeeMessages.REPORT_ENABLED);
            }

            BungeeMain.runAsync(() -> bp.getDataHandler().saveCategory(DataCategory.PREFERENCES));
            return;
        }

        if (args.length < 2) {
            proxiedPlayer.sendMessage(BungeeMessages.COMMAND_REPORT_USAGE);
            return;
        }

        BungeePlayer proxyPlayer = BungeeMain.getBungeePlayer(proxiedPlayer.getName());

        if (!proxyPlayer.podeReportar()) {
            proxiedPlayer.sendMessage(BungeeMessages.AGUARDE_PARA_REPORTAR_NOVAMENTE);
            return;
        }

        ProxiedPlayer reportado = ProxyServer.getInstance().getPlayer(args[0]);

        if ((reportado == null) || (reportado.getServer() == null)) {
            proxiedPlayer.sendMessage(BungeeMessages.JOGADOR_OFFLINE);
            return;
        }

        if (reportado == proxiedPlayer) {
            proxiedPlayer.sendMessage(BungeeMessages.VOCE_NAO_PODE_SE_REPORTAR);
            return;
        }

        proxiedPlayer.sendMessage(BungeeMessages.JOGADOR_REPORTADO_COM_SUCESSO);
        proxyPlayer.setLastReport(System.currentTimeMillis());

        notifyStaffers(proxiedPlayer.getName(), reportado.getName(), StringUtility.createArgs(1, args));
    }

    private void notifyStaffers(String from, String target, String cause) {
        val components = TextComponent.fromLegacyText(BungeeMessages.NEW_REPORT
                .replace("%from%", from)
                .replace("%target%", target)
                .replace("%cause%", cause));

        for (val component : components) {
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/go " + target));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§aClique para ver a lista de reports")));
        }

        ProxyServer.getInstance().getPlayers()
                .stream()
                .filter(pp -> pp.hasPermission(CommonsConst.PERMISSION_PREFIX + ".receivereport")
                        && BungeeMain.isValid(pp)
                        && BungeeMain.getBungeePlayer(pp.getName()).getBoolean(DataType.RECEIVE_REPORTS))
                .forEach(pp -> pp.sendMessage(components));
    }
}