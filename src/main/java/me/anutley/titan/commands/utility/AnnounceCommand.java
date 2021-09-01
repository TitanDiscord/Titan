package me.anutley.titan.commands.utility;

import me.anutley.titan.commands.Command;
import me.anutley.titan.util.embeds.errors.NotTextChannelEmbed;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.awt.*;

public class AnnounceCommand extends Command {

    public static CommandData AnnounceCommandData = new CommandData("announce", "Sends an announcement into a channel of your choice")
            .addOption(OptionType.CHANNEL, "channel", "The channel you want to send the announcement to", true)
            .addOption(OptionType.STRING, "title", "The announcement title", true)
            .addOption(OptionType.STRING, "content", "The announcement content")
            .addOption(OptionType.STRING, "colour", "The announcement colour (Hex)")
            .addOption(OptionType.STRING, "thumbnail", "The announcement thumbnail url")
            .addOption(OptionType.STRING, "footer", "The announcement footer");


    public void onSlashCommand(SlashCommandEvent event) {

        if (!event.getName().equals("announce")) return;

        if (!event.getOption("channel").getChannelType().equals(ChannelType.TEXT)) {
            event.replyEmbeds(NotTextChannelEmbed.Embed().build()).queue();
            return;
        }

        String content = event.getOption("content") != null ? event.getOption("content").getAsString() : null;
        String colour = event.getOption("colour") != null ? event.getOption("colour").getAsString() : null;
        String thumbnail = event.getOption("thumbnail") != null ? event.getOption("thumbnail").getAsString() : null;
        String footer = event.getOption("footer") != null ? event.getOption("footer").getAsString() : null;

        if (colour != null)
            if (!colour.startsWith("#")) colour = "#" + colour;

        event.replyEmbeds(new EmbedBuilder().setTitle("Announced").setColor(EmbedColour.YES.getColour()).build()).queue();

        event.getGuild().getTextChannelById(event.getOption("channel").getAsGuildChannel().getId()).sendMessageEmbeds(
                new EmbedBuilder()
                        .setTitle(event.getOption("title").getAsString())
                        .setDescription(content)
                        .setColor(colour != null ? Color.decode(colour) : null)
                        .setThumbnail(thumbnail)
                        .setFooter(footer)
                        .build()).queue();
    }

    @Override
    public String getCommandName() {
        return "announce";
    }

    @Override
    public String getCommandDescription() {
        return "Sends an announcement into a channel of your choice";
    }

    @Override
    public String getCommandUsage() {
        return "/announce <channel> <title> [content] [colour] [thumbnail] [footer]";
    }
}
