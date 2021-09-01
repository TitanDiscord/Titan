package me.anutley.titan.commands.utility;

import me.anutley.titan.commands.Command;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class WikiCommand extends Command {
    public static CommandData WikiCommandData = new CommandData("wiki", "Sends the link to Titan's wiki");

    @Override
    public void onSlashCommand(SlashCommandEvent event) {

        if (!event.getName().equals("wiki")) return;

        event.replyEmbeds(new EmbedBuilder()
                .setTitle("Wiki")
                .setColor(EmbedColour.NEUTRAL.getColour())
                .setDescription("enter wiki link here")
                .build()).queue();
    }

    @Override
    public String getCommandName() {
        return "wiki";
    }

    @Override
    public String getCommandDescription() {
        return "Sends the link to Titan's wiki page";
    }

    @Override
    public String getCommandUsage() {
        return "/wiki";
    }
}
