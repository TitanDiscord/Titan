package me.anutley.titan.commands.utility;

import me.anutley.titan.commands.Command;
import me.anutley.titan.util.CommandUtil;
import me.anutley.titan.util.PermissionUtil;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HelpCommand {

    public static CommandData HelpCommandData = new CommandData("help", "Provides you with information about a command or command category")
            .addSubcommands(new SubcommandData("command", "Provides you with more information about a command")
                    .addOption(OptionType.STRING, "command", "The command (format: category.command), for example: fun.avatar, moderation.ban or tag.embed.create "))
            .addSubcommands(new SubcommandData("category", "Provides you with more information about a category")
                    .addOption(OptionType.STRING, "category", "The category you want to find more information about"))
            .addSubcommands(new SubcommandData("all", "Provides you with more information about all Titan's commands"));

    @Command(name = "help.command", description = "Provides you with more information about a command", permission = "command.utility.help.command")
    public static void commandHelpCommand(SlashCommandEvent event) {
        Map<String, Command> commands = new HashMap<>();

        for (Command command : CommandUtil.getCommands())
            commands.put(command.name(), command);

        String commandOption = event.getOption("command").getAsString().toLowerCase(Locale.ROOT);

        if (!commands.containsKey(commandOption)) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("`" + commandOption + "`" + " is not a valid command!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
            return;
        }

        event.replyEmbeds(
                new EmbedBuilder()
                        .setTitle("Command Name: `" + commandOption + "`")
                        .setColor(EmbedColour.NEUTRAL.getColour())
                        .addField("Category:", StringUtils.capitalize(commands.get(commandOption).permission().split("\\.")[1]), false)
                        .addField("Description:", commands.get(commandOption).description(), false)
                        .addField("Permission:", commands.get(commandOption).permission(), false)
                        .addField("Has Permission:",
                                PermissionUtil.hasPermission(commands.get(commandOption).permission(), event.getMember())
                                        ? "✅" : "❌", false)
                        .build()).queue();
    }

    @Command(name = "help.category", description = "Provides you with more information about a category", permission = "command.utility.help.category")
    public static void categoryHelpCommand(SlashCommandEvent event) {
        String category = event.getOption("category").getAsString().toLowerCase(Locale.ROOT);

        if (!CommandUtil.isValidCategory(category)) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("`" + category + "` is not a valid category!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
            return;
        }

        ArrayList<Command> commands = CommandUtil.getCommandsByCategory(category);
        int count = commands.size();
        int commandCount = 0;
        int pageNumber = 0;

        for (int i = 0; i < count; i += 25) {

            pageNumber++;
            EmbedBuilder builder = new EmbedBuilder();

            if (pageNumber == 1)
                builder.setTitle(WordUtils.capitalize(category) + " Commands")
                        .setDescription("To find out more about a specific command run `/help command <command>`");

            for (int j = 0; j < 25; j++) {
                if (commandCount >= count) break;

                Command command = commands.get(commandCount);

                builder.addField(command.name(), "`Description:` " + command.description() + "\n`Permission:` " + command.permission(), false);
                commandCount++;
            }

            if (pageNumber == 1)
                event.replyEmbeds(builder
                        .setColor(EmbedColour.NEUTRAL.getColour())
                        .build()).queue();
            else
                event.getChannel().sendMessageEmbeds(builder
                        .setColor(EmbedColour.NEUTRAL.getColour())
                        .build()).queue();

        }
    }

    @Command(name = "help.all", description = "Provides you with more information about all Titan's commands", permission = "command.utility.help.all")
    public static void allHelpCommand(SlashCommandEvent event) {

        StringBuilder funCommands = new StringBuilder();
        StringBuilder moderationCommands = new StringBuilder();
        StringBuilder settingsCommands = new StringBuilder();
        StringBuilder utilityCommands = new StringBuilder();


        for (Command command : CommandUtil.getBaseCommandsByCategory("fun"))
            funCommands.append(command.name().split("\\.")[0]).append("\n");

        for (Command command : CommandUtil.getBaseCommandsByCategory("moderation"))
            moderationCommands.append(command.name().split("\\.")[0]).append("\n");

        for (Command command : CommandUtil.getBaseCommandsByCategory("settings"))
            settingsCommands.append(command.name().split("\\.")[1]).append("\n");

        for (Command command : CommandUtil.getBaseCommandsByCategory("utility"))
            utilityCommands.append(command.name().split("\\.")[0]).append("\n");


        event.replyEmbeds(new EmbedBuilder()
                .setTitle("All Commands!")
                .setDescription("To find out more about a specific category run `/help category <category>`")
                .setColor(EmbedColour.NEUTRAL.getColour())
                .addField("Fun Commands:", funCommands.toString(), false)
                .addField("Moderation Commands:", moderationCommands.toString(), false)
                .addField("Settings Commands:", settingsCommands.toString(), false)
                .addField("Utility Commands:", utilityCommands.toString(), false)
                .build()).queue();
    }

}
