package me.anutley.titan.commands.settings;

import me.anutley.titan.commands.Command;
import me.anutley.titan.database.ActionLogger;
import me.anutley.titan.database.objects.LeaveSettings;
import me.anutley.titan.util.embeds.errors.NotTextChannelEmbed;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

public class LeaveSettingsCommand {


    public static SubcommandGroupData LeaveSettingsCommandData = new SubcommandGroupData("leave", "Controls Titan's leave message settings")
            .addSubcommands(new SubcommandData("enable", "Enables Titan's leave messages"))
            .addSubcommands(new SubcommandData("disable", "Disables Titan's leave messages"))
            .addSubcommands(new SubcommandData("channel", "Changes the channel Titan should send leave messages to")
                    .addOption(OptionType.CHANNEL, "channel", "The channel Titan should send leave messages to", true))
            .addSubcommands(new SubcommandData("message", "The message Titan should send when someone leaves (Send without option to get the placeholders)")
                    .addOption(OptionType.STRING, "message", "The message Titan should send when someone leaves"));


    @Command(name = "settings.leave.enable", description = "Enables Titan's leave messages", permission = "command.settings.leave.enable")
    public static void enableLeave(SlashCommandEvent event) {
        LeaveSettings leaveSettings = new LeaveSettings(event.getGuild().getId());

        if (leaveSettings.isEnabled()) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("Leave messages are already enabled")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
        }

        leaveSettings.setEnabled(true).save();

        event.replyEmbeds(new EmbedBuilder()
                .setDescription("Leave messages have been enabled!")
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();

        new ActionLogger(event.getGuild())
                .addAction("Leave messages enabled")
                .addModerator(event.getUser())
                .addOldValue(false)
                .addNewValue(true)
                .log();
    }

    @Command(name = "settings.leave.disable", description = "Disables Titan's leave messages", permission = "command.settings.leave.disable")
    public static void disableWelcome(SlashCommandEvent event) {
        LeaveSettings leaveSettings = new LeaveSettings(event.getGuild().getId());

        if (!leaveSettings.isEnabled()) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("Leave messages are already disabled")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
        }

        leaveSettings.setEnabled(true).save();

        event.replyEmbeds(new EmbedBuilder()
                .setDescription("Leave messages have been disabled!")
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();

        new ActionLogger(event.getGuild())
                .addAction("Leave messages disabled")
                .addModerator(event.getUser())
                .addOldValue(true)
                .addNewValue(false)
                .log();
    }

    @Command(name = "settings.leave.channel", description = "Changes the channel Titan should send leave messages to", permission = "command.settings.leave.channel")
    public static void changeLeaveChannel(SlashCommandEvent event) {

        if (!event.getOption("channel").getChannelType().equals(ChannelType.TEXT)) {
            event.replyEmbeds(NotTextChannelEmbed.Embed().build()).queue();
            return;
        }

        LeaveSettings leaveSettings = new LeaveSettings(event.getGuild().getId());

        String oldVal = leaveSettings.getChannelId();

        leaveSettings.setChannelId(event.getOption("channel").getAsGuildChannel().getId()).save();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription("The leave channel has been set to " + event.getOption("channel").getAsGuildChannel().getAsMention());

        builder.setColor(EmbedColour.YES.getColour());

        event.replyEmbeds(builder.build()).queue();

        ActionLogger logger = new ActionLogger(event.getGuild())
                .addAction("Leave messages channel changed")
                .addModerator(event.getUser());

        if (oldVal != null)
            logger.addOldValue(event.getJDA().getTextChannelById(oldVal).getAsMention())
                    .addNewValue(event.getJDA().getTextChannelById(event.getOption("channel").getAsGuildChannel().getId()).getAsMention());

        logger.log();
    }

    @Command(name = "settings.leave.message", description = "The message Titan should send when someone leaves (Send without option to get a list of placeholders)", permission = "command.settings.leave.channel")
    public static void changeLeaveMessage(SlashCommandEvent event) {

        if (event.getOption("message") == null) {
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("Available Placeholders:")
                    .setColor(EmbedColour.NEUTRAL.getColour())
                    .setDescription("%user% - The user who left as a mention\n" +
                            "%username% - The username of the user\n" +
                            "%username_with_discriminator% - The username of the user with a discriminator\n" +
                            "%discriminator - The discriminator of the user\n" +
                            "%guild_name% - The name of the guild\n" +
                            "End the message with `-showavatar`, to show add the users avatar to the join message").build()).queue();
        } else {

            LeaveSettings leaveSettings = new LeaveSettings(event.getGuild().getId());

            String oldMessage = leaveSettings.getMessage();

            String newMessage = event.getOption("message").getAsString();

            if (newMessage.length() > 500) {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("The maximum length of the message can be 500 characters")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).setEphemeral(true).queue();
                return;
            }

            leaveSettings.setMessage(newMessage).save();

            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("The leave message has been set to " + newMessage)
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();

            new ActionLogger(event.getGuild())
                    .addAction("Leave message changed")
                    .addModerator(event.getUser())
                    .addOldValue(oldMessage)
                    .addNewValue(newMessage)
                    .log();

        }
    }

}
