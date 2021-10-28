package me.anutley.titan.commands.settings;

import me.anutley.titan.commands.Command;
import me.anutley.titan.database.ActionLogger;
import me.anutley.titan.database.objects.WelcomeSettings;
import me.anutley.titan.util.embeds.errors.HierarchyError;
import me.anutley.titan.util.embeds.errors.NotTextChannelEmbed;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

public class WelcomeSettingsCommand {


    public static SubcommandGroupData WelcomeSettingsCommandData = new SubcommandGroupData("welcome", "Controls Titan's welcome message settings")
            .addSubcommands(new SubcommandData("enable", "Enables Titan welcoming new members"))
            .addSubcommands(new SubcommandData("disable", "Disables Titan welcoming new members"))
            .addSubcommands(new SubcommandData("channel", "Changes the channel Titan should send welcome messages to")
                    .addOptions(new OptionData(OptionType.CHANNEL, "channel", "The channel Titan should send welcome messages to", true).setChannelTypes(ChannelType.TEXT)))
            .addSubcommands(new SubcommandData("message", "The message Titan should send when someone joins (Send without option to get a list of placeholders)")
                    .addOption(OptionType.STRING, "message", "The message Titan should send when someone joins"))
            .addSubcommands(new SubcommandData("role", "The role Titan should give to users when they join. Set to @everyone to disable")
                    .addOption(OptionType.ROLE, "role", "The role Titan should give to users when they join"));


    @Command(name = "settings.welcome.enable", description = "Enables Titan welcoming new members", permission = "command.settings.welcome.enable")
    public static void enableWelcome(SlashCommandEvent event) {
        WelcomeSettings welcomeSettings = new WelcomeSettings(event.getGuild().getId());

        if (welcomeSettings.isEnabled()) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("Welcome messages are already enabled")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
        }

        welcomeSettings.setEnabled(true).save();

        event.replyEmbeds(new EmbedBuilder()
                .setDescription("Welcome messages have been enabled!")
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();

        new ActionLogger(event.getGuild())
                .addAction("Welcome messages enabled")
                .addModerator(event.getUser())
                .addOldValue(false)
                .addNewValue(true)
                .log();
    }

    @Command(name = "settings.welcome.disable", description = "Disables Titan welcoming new members", permission = "command.settings.welcome.disable")
    public static void disableWelcome(SlashCommandEvent event) {
        WelcomeSettings welcomeSettings = new WelcomeSettings(event.getGuild().getId());

        if (!welcomeSettings.isEnabled()) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("Welcome messages are already disabled")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
        }

        welcomeSettings.setEnabled(true).save();

        event.replyEmbeds(new EmbedBuilder()
                .setDescription("Welcome messages have been disabled!")
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();

        new ActionLogger(event.getGuild())
                .addAction("Welcome messages disabled")
                .addModerator(event.getUser())
                .addOldValue(true)
                .addNewValue(false)
                .log();
    }

    @Command(name = "settings.welcome.channel", description = "Changes the channel Titan should send welcome messages to", permission = "command.settings.welcome.channel")
    public static void changeWelcomeChannel(SlashCommandEvent event) {

        if (!event.getOption("channel").getChannelType().equals(ChannelType.TEXT)) {
            event.replyEmbeds(NotTextChannelEmbed.Embed().build()).queue();
            return;
        }

        WelcomeSettings welcomeSettings = new WelcomeSettings(event.getGuild().getId());

        String oldVal = welcomeSettings.getChannelId();

        welcomeSettings.setChannelId(event.getOption("channel").getAsGuildChannel().getId()).save();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription("The welcome channel has been set to " + event.getOption("channel").getAsGuildChannel().getAsMention());

        builder.setColor(EmbedColour.YES.getColour());

        event.replyEmbeds(builder.build()).queue();

        ActionLogger logger = new ActionLogger(event.getGuild())
                .addAction("Welcome messages channel changed")
                .addModerator(event.getUser());

        if (oldVal != null)
            logger.addOldValue(event.getJDA().getTextChannelById(oldVal).getAsMention())
                    .addNewValue(event.getJDA().getTextChannelById(event.getOption("channel").getAsGuildChannel().getId()).getAsMention());

        logger.log();

    }

    @Command(name = "settings.welcome.message", description = "The message Titan should send when someone joins (Send without option to get a list of placeholders)", permission = "command.settings.welcome.message")
    public static void changeWelcomeMessage(SlashCommandEvent event) {

        if (event.getOption("message") == null) {
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("Available Placeholders:")
                    .setColor(EmbedColour.NEUTRAL.getColour())
                    .setDescription("%user% - The user who joined as a mention\n" +
                            "%username% - The username of the user\n" +
                            "%username_with_discriminator% - The username of the user with a discriminator\n" +
                            "%discriminator - The discriminator of the user\n" +
                            "%guild_name% - The name of the guild\n" +
                            "End the message with `-showavatar`, to show add the users avatar to the join message").build()).queue();
        } else {
            WelcomeSettings welcomeSettings = new WelcomeSettings(event.getGuild().getId());

            String oldMessage = welcomeSettings.getMessage();

            welcomeSettings.setMessage(event.getOption("message").getAsString()).save();

            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("The welcome message has been set to " + event.getOption("message").getAsString())
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();

            new ActionLogger(event.getGuild())
                    .addAction("Welcome message changed")
                    .addModerator(event.getUser())
                    .addOldValue(oldMessage)
                    .addNewValue(event.getOption("message").getAsString())
                    .log();

        }
    }

    @Command(name = "settings.welcome.role", description = "The role Titan should give to users when they join. Set to @everyone to disable", permission = "command.settings.welcome.role")
    public static void changeWelcomeRole(SlashCommandEvent event) {

        if (!event.getGuild().getSelfMember().canInteract(event.getOption("role").getAsRole())) {
            event.replyEmbeds(HierarchyError.other(event).build()).queue();
            return;
        }

        if (event.getOption("role") == null) {
            if (new WelcomeSettings(event.getGuild().getId()).getRoleId() == null) {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("No welcome role set!")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).queue();
            } else {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("This guilds welcome role is " + new WelcomeSettings(event.getGuild().getId()).getRole().getAsMention())
                        .setColor(EmbedColour.NEUTRAL.getColour())
                        .build()).queue();
            }
        } else {
            String id;

            if (event.getOption("role").getAsRole().getId().equals(event.getGuild().getPublicRole().getId())) id = null;
            else id = event.getOption("role").getAsRole().getId();

            WelcomeSettings welcomeSettings = new WelcomeSettings(event.getGuild().getId());

            Role oldVal = welcomeSettings.getRole();

            welcomeSettings.setRoleId(id).save();

            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("The guild's welcome role has been set to " + event.getOption("role").getAsRole().getAsMention() + "!")
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();

            new ActionLogger(event.getGuild())
                    .addAction("Welcome role changed")
                    .addModerator(event.getUser())
                    .addOldValue(oldVal.getAsMention())
                    .addNewValue(event.getOption("role").getAsRole().getAsMention())
                    .log();
        }
    }
}
