package me.anutley.titan.listeners;

import me.anutley.titan.Config;
import me.anutley.titan.Titan;
import me.anutley.titan.commands.Command;
import me.anutley.titan.util.PermissionUtil;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class CommandListener extends ListenerAdapter {

    private final ArrayList<Class<?>> commandClasses = new ArrayList<>();

    public CommandListener() {
        commandClasses.addAll(Titan.getRegisteredCommands());
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {

        String commandName = event.getName();

        if (event.getSubcommandGroup() != null) commandName += "." + event.getSubcommandGroup();
        if (event.getSubcommandName() != null) commandName += "." + event.getSubcommandName();


        Command command = null;
        Method commandMethod = null;

        for (Class<?> commandClass : commandClasses) {
            for (Method method : commandClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Command.class)) {
                    if (method.getAnnotation(Command.class).name().equalsIgnoreCase(commandName)) {
                        command = method.getAnnotation(Command.class);
                        commandMethod = method;
                        break;
                    }
                }
            }
        }

        if (commandMethod == null) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("Oops! This shouldn't be possible. Please contact " + event.getJDA().getUserById(Config.getInstance().get("BOT_OWNER")).getAsMention())
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
            return;
        }

        if (command.selfPermission() != Permission.UNKNOWN)
            if (!event.getGuild().getSelfMember().hasPermission(command.selfPermission())) {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("I do not have the required permission (`" + command.selfPermission() + "`)" + " to perform this action! Please contact a server admin if you think this is a mistake.")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).setEphemeral(true).queue();
                return;
            }


        if (!PermissionUtil.hasPermission(command.permission(), event.getMember())) {
            event.replyEmbeds(new EmbedBuilder()
                    .setAuthor("Missing Permissions!", null, event.getUser().getAvatarUrl())
                    .setColor(EmbedColour.NO.getColour())
                    .setDescription("You need the permission `" + command.permission() + "` or a suitable wildcard.")
                    .build()).setEphemeral(true).queue();
            return;
        }

        try {
            commandMethod.invoke(null, event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


}
