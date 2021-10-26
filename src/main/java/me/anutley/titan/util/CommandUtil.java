package me.anutley.titan.util;

import me.anutley.titan.Titan;
import me.anutley.titan.commands.Command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class CommandUtil {

    public static ArrayList<Command> getCommands() {
        ArrayList<Command> commands = new ArrayList<>();

        for (Class<?> commandClass : Titan.getRegisteredCommands()) {
            for (Method method : commandClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Command.class)) {
                    commands.add(method.getAnnotation(Command.class));
                }
            }
        }

        return commands;
    }

    public static ArrayList<Command> getCommandsByCategory(String category) {

        ArrayList<Command> commands = new ArrayList<>(getCommands());
        ArrayList<Command> categoryCommands = new ArrayList<>();


        for (Command command : commands) {
            if (command.permission().split("\\.")[1].equals(category))
                categoryCommands.add(command);
        }

        categoryCommands.sort(Comparator.comparing(Command::name));
        return categoryCommands;
    }

    public static ArrayList<Command> getBaseCommandsByCategory(String category) {
        ArrayList<Command> commands = new ArrayList<>();

        for (Command command : getCommands()) {
            StringBuilder baseCommand = new StringBuilder();

            int index = 0;

            if (command.name().split("\\.")[0].equals("settings")) {
                baseCommand.append(command.name().split("\\.")[1]);
                index = 1;
            } else {
                baseCommand.append(command.name().split("\\.")[0]);
            }

            int finalIndex = index;
            if (command.permission().split("\\.")[1].equals(category)
                    && commands.stream().noneMatch(w -> Objects.equals(w.name().split("\\.")[finalIndex], baseCommand.toString())))
                commands.add(command);

        }

        commands.sort(Comparator.comparing(Command::name));
        return commands;
    }

    public static boolean isValidCategory(String category) {

        ArrayList<Command> commands = new ArrayList<>(getCommands());
        ArrayList<String> categories = new ArrayList<>();

        for (Command command : commands) {
            String commandCategory = command.permission().split("\\.")[1];
            if (!categories.contains(commandCategory))
                categories.add(commandCategory);
        }


        return categories.contains(category);
    }
}
