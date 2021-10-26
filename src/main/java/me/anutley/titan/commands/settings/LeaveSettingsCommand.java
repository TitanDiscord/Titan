package me.anutley.titan.commands.settings;

import me.anutley.titan.commands.Command;
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
        new LeaveSettings(event.getGuild().getId())
                .setEnabled(true)
                .save();

        event.replyEmbeds(new EmbedBuilder()
                .setDescription("Leave messages has been enabled!")
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();
    }

    @Command(name = "settings.leave.disable", description = "Disables Titan's leave messages", permission = "command.settings.leave.disable")
    public static void disableWelcome(SlashCommandEvent event) {
        new LeaveSettings(event.getGuild().getId())
                .setEnabled(false)
                .save();

        event.replyEmbeds(new EmbedBuilder()
                .setDescription("Leave messages has been disabled!")
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();
    }

    @Command(name = "settings.welcome.channel", description = "Changes the channel Titan should send leave messages to", permission = "command.settings.leave.channel")
    public static void changeLeaveChannel(SlashCommandEvent event) {

        if (!event.getOption("channel").getChannelType().equals(ChannelType.TEXT)) {
            event.replyEmbeds(NotTextChannelEmbed.Embed().build()).queue();
            return;
        }

        new LeaveSettings(event.getGuild().getId()).setChannelId(event.getOption("channel").getAsGuildChannel().getId()).save();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription("The leave channel has been set to " + event.getOption("channel").getAsGuildChannel().getAsMention());

        builder.setColor(EmbedColour.YES.getColour());

        event.replyEmbeds(builder.build()).queue();

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

            new LeaveSettings(event.getGuild().getId()).setMessage(event.getOption("message").getAsString()).save();

            EmbedBuilder builder = new EmbedBuilder();
            builder.setDescription("The leave message has been set to " + event.getOption("message").getAsString());

            builder.setColor(EmbedColour.YES.getColour());

            event.replyEmbeds(builder.build()).queue();

        }
    }

}
