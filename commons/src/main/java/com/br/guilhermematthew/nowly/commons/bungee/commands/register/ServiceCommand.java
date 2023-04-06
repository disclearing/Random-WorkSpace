package com.br.guilhermematthew.nowly.commons.bungee.commands.register;

import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.commands.BungeeCommandSender;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework.Command;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.utility.string.StringUtility;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class ServiceCommand implements CommandClass {

    @SuppressWarnings("deprecation")
    @Command(name = "addserver", aliases = {"ds", "dsv"}, groupsToUse = {Groups.ADMIN})
    public void serverInfo(BungeeCommandSender commandSender, String label, String[] args) {
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                String serverName = args[1];

                if (ProxyServer.getInstance().getServers().containsKey(serverName)) {
                    ServerInfo info = ProxyServer.getInstance().getServerInfo(serverName);
                    if (info != null) {
                        if (info.getPlayers().size() == 0) {
                            commandSender.sendMessage("§a[DynamicServers] §f" + info.getName() + " §afoi removido. (§f" + info.getAddress().getAddress().getHostAddress() + ":" + info.getAddress().getPort() + ")");

                            ProxyServer.getInstance().getServers().remove(info.getName());
                        } else {
                            commandSender.sendMessage("§cNão é possivel remover um servidor com jogadores online.");
                        }
                        info = null;
                    } else {
                        commandSender.sendMessage("§cNão foi possível obter as informaçőes deste servidor.");
                    }
                } else {
                    commandSender.sendMessage("§cEste servidor não está registrado.");
                }

                serverName = null;
            } else {
                sendHelp(commandSender);
            }
        } else if (args.length == 4) {
            if (args[0].equalsIgnoreCase("create")) {
                String serverName = args[1];

                if (!ProxyServer.getInstance().getServers().containsKey(serverName)) {
                    String address = args[2];
                    String portString = args[3];

                    if (address.length() > 7) {
                        if (StringUtility.isInteger(portString)) {
                            int port = Integer.parseInt(portString);

                            if (BungeeMain.registerServer(serverName, address, port)) {
                                commandSender.sendMessage("§a[DynamicServers] §f" + serverName + " §afoi registrado. (§f" + address + ":" + port + ")");
                            } else {
                                commandSender.sendMessage("§cOcorreu um erro ao tentar registrar o servidor.");
                            }
                        } else {
                            commandSender.sendMessage("§cPorta invalida.");
                        }
                    } else {
                        commandSender.sendMessage("§cEndereço invalida.");
                    }
                    address = null;
                    portString = null;
                } else {
                    commandSender.sendMessage("§cEste servidor já está registrado.");
                }
                serverName = null;
            } else {
                sendHelp(commandSender);
            }
        } else {
            sendHelp(commandSender);
        }
    }

    private void sendHelp(BungeeCommandSender commandSender) {
        commandSender.sendMessage("§cUtilize: /dynamicservers create <Name> <Address> <Port>");
        commandSender.sendMessage("§cUtilize: /dynamicservers remove <Name>");
    }
}