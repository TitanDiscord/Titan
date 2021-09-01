package me.anutley.titan.commands.utility;

import me.anutley.titan.Titan;
import me.anutley.titan.commands.Command;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class HelpCommand extends Command {

    public static CommandData HelpCommandData = new CommandData("help", "Provides you with information about Titan")
            .addOption(OptionType.STRING, "command", "The command you want to find more information about");

    @Override
    public void onSlashCommand(SlashCommandEvent event) {

        if (!event.getName().equals("help")) return;

        if (event.getOption("command") == null) {
            StringBuilder content = new StringBuilder();

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Help");


            for (int i = 0; i < Titan.getRegisteredCommands().size(); i++) {

                Command command = Titan.getRegisteredCommands().get(i);

                if (i == Titan.getRegisteredCommands().size() - 1) {
                    content.append("`").append(command.getCommandName()).append("`");
                } else {
                    content.append("`").append(command.getCommandName()).append("`, ");
                }
            }
            event.replyEmbeds(
                    builder.setDescription("Use /help [command] to get more information about a specific command")
                            .setColor(EmbedColour.NEUTRAL.getColour())
                            .addField("Commands:", content.toString(), false)
                            .addField("Arguments", "`<>` are required arguments, whereas `[]` are optional arguments", false)
                            .build()).queue();

        } else {
            Command chosenCommand = null;

            for (int i = 0; i < Titan.getRegisteredCommands().size(); i++) {
                Command currentCommand = Titan.getRegisteredCommands().get(i);

                if (currentCommand.getCommandName().equalsIgnoreCase(event.getOption("command").getAsString())) {
                    chosenCommand = currentCommand;
                    break;
                }
            }

            if (chosenCommand == null) {
                event.replyEmbeds(new EmbedBuilder()
                        .setTitle("No command found with the name `" + event.getOption("command").getAsString() + "`")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).queue();
            }
            else {
                EmbedBuilder builder = new EmbedBuilder();

                builder
                        .setTitle("Command Name: `" + chosenCommand.getCommandName() + "`")
                        .setColor(EmbedColour.YES.getColour())
                        .addField("Description:", chosenCommand.getCommandDescription(), false)
                        .addField("Usage:", chosenCommand.getCommandUsage(), false);

                event.replyEmbeds(builder.build()).queue();

            }

        }

    }

    @Override
    public String getCommandName() {
        return "help";
    }

    @Override
    public String getCommandDescription() {
        return "Provides you with information about Titan";
    }

    @Override
    public String getCommandUsage() {
        return "/help [command]";
    }
}
