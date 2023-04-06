package com.br.guilhermematthew.nowly.commons.common.command;

import com.br.guilhermematthew.nowly.commons.CommonsGeneral;
import com.br.guilhermematthew.nowly.commons.common.group.Groups;
import com.br.guilhermematthew.nowly.commons.common.utility.ClassGetter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface CommandFramework {

    Object getPlugin();

    void registerCommands(CommandClass commandClass);

    default CommandFramework loadCommands(Object plugin, String packageName) {
        for (Class<?> commandClass : ClassGetter.getClassesForPackage(plugin, packageName))
            if (CommandClass.class.isAssignableFrom(commandClass)) {
                try {
                    registerCommands((CommandClass) commandClass.newInstance());
                } catch (Exception e) {
                    CommonsGeneral.console("Error when loading command from " + commandClass.getSimpleName() + "!");
                    e.printStackTrace();
                }
            }

        return this;
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Command {

        String name();

        Groups[] groupsToUse() default {Groups.MEMBRO};

        String permission() default "";

        String[] aliases() default {};

        String description() default "";

        String usage() default "";

        boolean runAsync() default false;
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Completer {
        String name();

        String[] aliases() default {};
    }
}
