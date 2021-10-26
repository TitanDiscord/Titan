package me.anutley.titan.commands.utility;

import me.anutley.titan.commands.Command;
import me.anutley.titan.util.embeds.errors.NotTextChannelEmbed;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;

public class AnnounceCommand {

    public static CommandData AnnounceCommandData = new CommandData("announce", "Sends an announcement into a channel of your choice")
            .addOptions(new OptionData(OptionType.CHANNEL, "channel", "The channel you want to send the announcement to", true).setChannelTypes(ChannelType.TEXT))
            .addOption(OptionType.STRING, "title", "The announcement title", true)
            .addOption(OptionType.STRING, "content", "The announcement content")
            .addOption(OptionType.STRING, "colour", "The announcement colour (Hex)")
            .addOption(OptionType.STRING, "thumbnail", "The announcement thumbnail url")
            .addOption(OptionType.STRING, "footer", "The announcement footer");


    @Command(name = "announce", description = "Sends an announcement into a channel of your choice", permission = "command.utility.announce")
    public static void announceCommand(SlashCommandEvent event) {

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

        TextChannel channel = event.getGuild().getTextChannelById(event.getOption("channel").getAsGuildChannel().getId());
        try {
            channel.sendMessageEmbeds(
                    new EmbedBuilder()
                            .setTitle(event.getOption("title").getAsString())
                            .setDescription(content)
                            .setColor(colour != null ? Color.decode(colour) : null)
                            .setThumbnail(thumbnail)
                            .setFooter(footer)
                            .build()).queue();
        } catch (InsufficientPermissionException e) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("I do not have permission to send a message in " + channel.getAsMention() + "!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
            return;
        }

        event.replyEmbeds(new EmbedBuilder().setDescription("Announced!").setColor(EmbedColour.YES.getColour()).build()).setEphemeral(true).queue();

    }
}
