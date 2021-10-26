package me.anutley.titan.commands.utility;

import me.anutley.titan.commands.Command;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class WikiCommand {

    public static CommandData WikiCommandData = new CommandData("wiki", "Sends the link to Titan's wiki");

    @Command(name = "wiki", description = "Sends the link to Titan's wiki", permission = "command.utility.wiki")
    public static void wikiCommand(SlashCommandEvent event) {
        event.replyEmbeds(new EmbedBuilder()
                .setTitle("Wiki")
                .setColor(EmbedColour.NEUTRAL.getColour())
                .setDescription("https://titan.anutley.me/")
                .build()).queue();
    }

}
