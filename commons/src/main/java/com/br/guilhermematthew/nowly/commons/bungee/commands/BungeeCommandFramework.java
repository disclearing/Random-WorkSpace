package com.br.guilhermematthew.nowly.commons.bungee.commands;

import com.br.guilhermematthew.nowly.commons.CommonsConst;
import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMain;
import com.br.guilhermematthew.nowly.commons.bungee.BungeeMessages;
import com.br.guilhermematthew.nowly.commons.common.command.CommandClass;
import com.br.guilhermematthew.nowly.commons.common.command.CommandFramework;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import lombok.val;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

public class BungeeCommandFramework implements CommandFramework {

    private final Map<String, Entry<Method, Object>> commandMap = new HashMap<String, Entry<Method, Object>>();
    private final Map<String, Entry<Method, Object>> completers = new HashMap<String, Entry<Method, Object>>();
    private final Plugin plugin;

    public BungeeCommandFramework(Plugin plugin) {
        this.plugin = plugin;
        this.plugin.getProxy().getPluginManager().registerListener(plugin, new BungeeCompleter());
    }

    @SuppressWarnings("deprecation")
    public boolean handleCommand(CommandSender sender, String label, String[] args) {
        StringBuilder line = new StringBuilder();

        line.append(label);

        for (int i = 0; i < args.length; i++) {
            line.append(" ").append(args[i]);
        }

        for (int i = args.length; i >= 0; i--) {
            StringBuilder buffer = new StringBuilder();
            buffer.append(label.toLowerCase());

            for (int x = 0; x < i; x++) {
                buffer.append(".").append(args[x].toLowerCase());
            }

            String cmdLabel = buffer.toString();
            if (commandMap.containsKey(cmdLabel)) {
                Entry<Method, Object> entry = commandMap.get(cmdLabel);
                Command command = entry.getKey().getAnnotation(Command.class);

                if (sender instanceof ProxiedPlayer) {
                    ProxiedPlayer p = (ProxiedPlayer) sender;

                    if (!BungeeMain.isValid(p)) {
                        p.sendMessage(BungeeMessages.VOCE_NAO_TEM_PERMISSãO_PARA_USAR_ESTE_COMANDO);
                        return true;
                    }

                    Groups tagPlayer = CommonsGeneral.getProfileManager().getGamingProfile(p.getName()).getGroup();

                    boolean semPermissao = true;
                    for (int uses = 0; uses < command.groupsToUse().length; uses++) {
                        Groups tag = command.groupsToUse()[uses];
                        if (tagPlayer.getLevel() >= tag.getLevel()) {
                            semPermissao = false;
                            break;
                        }
                    }

                    tagPlayer = null;

                    if (semPermissao) {
                        if (hasCommand(p, command.name().toLowerCase())) {
                            semPermissao = false;
                        }
                    }

                    if (semPermissao) {
                        p.sendMessage(BungeeMessages.VOCE_NAO_TEM_PERMISSãO_PARA_USAR_ESTE_COMANDO);
                        return true;
                    }
                    p = null;
                }

                if (command.runAsync()) {
                    CommonsGeneral.runAsync(() -> {
                        try {
                            entry.getKey().invoke(entry.getValue(), new BungeeCommandSender(sender), label, args);
                        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    try {
                        entry.getKey().invoke(entry.getValue(), new BungeeCommandSender(sender), label, args);
                    } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        }
        return true;
    }

    public boolean hasCommand(ProxiedPlayer proxiedPlayer, String command) {
        if (proxiedPlayer.hasPermission(CommonsConst.PERMISSION_PREFIX + ".cmd.all")) {
            return true;
        }
        return proxiedPlayer.hasPermission(CommonsConst.PERMISSION_PREFIX + ".cmd." + command);
    }

    @Override
    public void registerCommands(CommandClass cls) {
        for (Method m : cls.getClass().getMethods()) {
            if (m.getAnnotation(Command.class) != null) {
                Command command = m.getAnnotation(Command.class);
                if (m.getParameterTypes().length != 3
                        || !BungeeCommandSender.class.isAssignableFrom(m.getParameterTypes()[0])
                        && !String.class.isAssignableFrom(m.getParameterTypes()[1])
                        && !String[].class.isAssignableFrom(m.getParameterTypes()[2])) {
                    System.out.println("Unable to register command " + m.getName() + ". Unexpected method arguments");
                    continue;
                }
                registerCommand(command, command.name(), m, cls);
                for (String alias : command.aliases()) {
                    registerCommand(command, alias, m, cls);
                }
            } else if (m.getAnnotation(Completer.class) != null) {
                Completer comp = m.getAnnotation(Completer.class);
                if (m.getParameterTypes().length != 3
                        || m.getParameterTypes()[0] != ProxiedPlayer.class
                        && m.getParameterTypes()[1] != String.class
                        && m.getParameterTypes()[2] != String[].class) {
                    System.out.println(
                            "Unable to register tab completer " + m.getName() + ". Unexpected method arguments");
                    continue;
                }
                if (m.getReturnType() != List.class) {
                    System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected return type");
                    continue;
                }
                registerCompleter(comp.name(), m, cls);
                for (String alias : comp.aliases()) {
                    registerCompleter(alias, m, cls);
                }
            }
        }
    }

    /**
     * Registers all the commands under the plugin's help
     */

    private void registerCommand(Command command, String label, Method m, Object obj) {
        Entry<Method, Object> entry = new AbstractMap.SimpleEntry<>(m, obj);
        commandMap.put(label.toLowerCase(), entry);
        String cmdLabel = label.replace(".", ",").split(",")[0].toLowerCase();

        net.md_5.bungee.api.plugin.Command cmd;
        if (command.permission().isEmpty())
            cmd = new BungeeCommand(cmdLabel);
        else
            cmd = new BungeeCommand(cmdLabel, command.permission());
        plugin.getProxy().getPluginManager().registerCommand(plugin, cmd);
    }

    private void registerCompleter(String label, Method m, Object obj) {
        completers.put(label, new AbstractMap.SimpleEntry<Method, Object>(m, obj));
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    class BungeeCommand extends net.md_5.bungee.api.plugin.Command {

        protected BungeeCommand(String label) {
            super(label);
        }

        protected BungeeCommand(String label, String permission) {
            super(label, permission);
        }

        @Override
        public void execute(net.md_5.bungee.api.CommandSender sender, String[] args) {
            handleCommand(sender, getName(), args);
        }

    }

    public class BungeeCompleter implements Listener {

        @SuppressWarnings("unchecked")
        @EventHandler
        public void onTabComplete(TabCompleteEvent event) {
            if (!(event.getSender() instanceof ProxiedPlayer)) return;

            val pp = (ProxiedPlayer) event.getSender();

            if(!event.getCursor().contains(" ")) return;

            val split = event.getCursor().split(" ");

            val label = split[0].toLowerCase().substring(1);
            val args = split.length < 2 ? new String[]{} : Arrays.copyOfRange(split, 1, split.length);

            if (completers.containsKey(label)) {
                try {
                    val entry = completers.get(label);

                    event.getSuggestions().clear();
                    event.getSuggestions().addAll((List<String>) entry.getKey().invoke(entry.getValue(), pp, label, args));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}