package me.anutley.titan.commands.moderation;

import me.anutley.titan.commands.Command;
import me.anutley.titan.util.PermissionUtil;
import me.anutley.titan.util.RoleUtil;
import me.anutley.titan.util.embeds.errors.InsufficientPermissionError;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class SlowmodeCommand extends Command {

    public static CommandData SlowmodeCommandData = new CommandData("slowmode", "Sets the slowmode for either the current or a specified channel (Set to 0 to reset, max 6 hours)")
            .addOption(OptionType.INTEGER, "hours", "How many hours between messages")
            .addOption(OptionType.INTEGER, "minutes", "How many minutes between messages")
            .addOption(OptionType.INTEGER, "seconds", "How many seconds between messages")
            .addOption(OptionType.CHANNEL, "channel", "The text channel to apply the slowmode to (Will use current channel if none are specified)");

    public void onSlashCommand(SlashCommandEvent event) {
        if (!event.getName().equals("slowmode")) return;

        if (!RoleUtil.isStaff(event.getMember())) {
            event.replyEmbeds(PermissionUtil.needRoleEmbed(event, RoleUtil.getModRole(event.getGuild())).build()).queue();
            return;
        }

        long hours = 0;
        long minutes = 0;
        long seconds = 0;
        MessageChannel channel = null;

        if (event.getOption("hours") != null) hours = event.getOption("hours").getAsLong();
        if (event.getOption("minutes") != null) minutes = event.getOption("minutes").getAsLong();
        if (event.getOption("seconds") != null) seconds = event.getOption("seconds").getAsLong();
        if (event.getOption("channel") != null) channel = event.getOption("channel").getAsMessageChannel();

        if (channel != null) {
            if (!event.getOption("channel").getAsGuildChannel().getType().isMessage()) channel = null;

        }

        long total = (hours * 3600) + (minutes * 60) + seconds;

        if (total > 21600) {
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("The maximum slowmode you can set is 1 message every 6 hours")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();
        } else {
            String formattedTime = "";
            EmbedBuilder builder = new EmbedBuilder()
                    .setColor(EmbedColour.YES.getColour());

            if (hours != 0) formattedTime = hours + "h ";
            if (minutes != 0) formattedTime += minutes + "m ";
            if (seconds != 0) formattedTime += seconds + "s";

            try {
                if (channel == null) event.getTextChannel().getManager().setSlowmode((int) total).queue();
                else event.getGuild().getTextChannelById(channel.getId()).getManager().setSlowmode((int) total).queue();


                if (total == 0) event.replyEmbeds(builder.setTitle("Slowmode has been disabled!").build()).queue();
                else
                    event.replyEmbeds(builder.setTitle("The slowmode has been set to one message every `" + formattedTime.trim() + "`").build()).queue();

            } catch (InsufficientPermissionException exception) {
                event.replyEmbeds(InsufficientPermissionError.Embed(event, Permission.MANAGE_CHANNEL).build()).setEphemeral(true).queue();
            }
        }

    }

    @Override
    public String getCommandName() {
        return "slowmode";
    }

    @Override
    public String getCommandDescription() {
        return "Sets the slowmode for either the current or a specified channel (Set to 0 to reset, max 6 hours)";
    }

    @Override
    public String getCommandUsage() {
        return "/slowmode <time> [channel]";
    }
}
