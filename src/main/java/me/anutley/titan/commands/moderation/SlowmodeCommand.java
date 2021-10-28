package me.anutley.titan.commands.moderation;

import me.anutley.titan.commands.Command;
import me.anutley.titan.database.ActionLogger;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class SlowmodeCommand {

    public static CommandData SlowmodeCommandData = new CommandData("slowmode", "Sets the slowmode for either the current or a specified channel (Set to 0 to reset, max 6 hours)")
            .addOption(OptionType.INTEGER, "hours", "How many hours between messages")
            .addOption(OptionType.INTEGER, "minutes", "How many minutes between messages")
            .addOption(OptionType.INTEGER, "seconds", "How many seconds between messages")
            .addOptions(new OptionData(OptionType.CHANNEL, "channel", "The text channel to apply the slowmode to (Will use current channel if none are specified)").setChannelTypes(ChannelType.TEXT));

    @Command(name = "slowmode", description = "Sets the slowmode for either the current or a specified channel (Set to 0 to reset, max 6 hours)", permission = "command.moderation.slowmode", selfPermission = Permission.MANAGE_CHANNEL)
    public static void slowmodeCommand(SlashCommandEvent event) {

        long hours = 0;
        long minutes = 0;
        long seconds = 0;
        TextChannel channel = null;

        if (event.getOption("hours") != null) hours = event.getOption("hours").getAsLong();
        if (event.getOption("minutes") != null) minutes = event.getOption("minutes").getAsLong();
        if (event.getOption("seconds") != null) seconds = event.getOption("seconds").getAsLong();
        if (event.getOption("channel") != null) channel = event.getJDA().getTextChannelById(event.getOption("channel").getAsMessageChannel().getId());

        long total = (hours * 3600) + (minutes * 60) + seconds;

        if (total > 21600) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("The maximum slowmode you can set is 1 message every 6 hours")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();
        } else {
            String formattedTime = "";
            EmbedBuilder builder = new EmbedBuilder()
                    .setColor(EmbedColour.YES.getColour());

            if (hours != 0) formattedTime = hours + "h ";
            if (minutes != 0) formattedTime += minutes + "m ";
            if (seconds != 0) formattedTime += seconds + "s";

            if (channel == null) event.getTextChannel().getManager().setSlowmode((int) total).queue();
            else event.getGuild().getTextChannelById(channel.getId()).getManager().setSlowmode((int) total).queue();
            
            ActionLogger logger = new ActionLogger(event.getGuild());

            if (total == 0) {
                event.replyEmbeds(builder.setDescription("Slowmode has been disabled!").build()).queue();
                logger.addAction("Slowmode has been disabled");
            }
            else {
                event.replyEmbeds(builder.setDescription("The slowmode has been set to one message every `" + formattedTime.trim() + "`").build()).queue();
                logger.addAction("Slowmode has been set to one message every " + formattedTime);
            }

            logger.addModerator(event.getUser());
            if (channel != null) logger.addChannel(channel);
            logger.log();

        }
    }
}
