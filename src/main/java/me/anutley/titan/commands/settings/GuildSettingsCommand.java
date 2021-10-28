package me.anutley.titan.commands.settings;

import me.anutley.titan.commands.Command;
import me.anutley.titan.database.ActionLogger;
import me.anutley.titan.database.objects.GuildSettings;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

public class GuildSettingsCommand {

    public static SubcommandGroupData GuildSettingsCommandData = new SubcommandGroupData("guild", "Controls Titan's guild settings")
            .addSubcommands(new SubcommandData("autoquote", "Toggles Titan's autoquote functionality")
                    .addOption(OptionType.BOOLEAN, "choice", "Whether or not Titan should automatically create a quote embed when someone posts a message link"))
            .addSubcommands(new SubcommandData("dm-on-warn", "Toggles Titan messaging someone when they are warned")
                    .addOption(OptionType.BOOLEAN, "choice", "Whether or not Titan message someone when they are warned"))
            .addSubcommands(new SubcommandData("botlogs-channel", "Sets the channel Titan should send log messages to, when someone performs an action with Titan")
                    .addOptions(new OptionData(OptionType.CHANNEL, "channel", "The channel Titan should send log messages to (Keep this option empty to disable it)").setChannelTypes(ChannelType.TEXT)));


    @Command(name = "settings.guild.autoquote", description = "Toggles Titan's autoquote functionality", permission = "command.settings.guild.autoquote")
    public static void toggleAutoQuote(SlashCommandEvent event) {

        GuildSettings guildSettings = new GuildSettings(event.getGuild().getId());

        if (event.getOption("choice") == null) {
            EmbedBuilder builder = new EmbedBuilder()
                    .setColor(EmbedColour.NEUTRAL.getColour());

            if (guildSettings.isAutoQuote()) builder.setDescription("Auto-quoting is currently enabled");
            else builder.setDescription("Auto-quoting is currently disabled");

            event.replyEmbeds(builder.build()).queue();
            return;
        }

        boolean choice = event.getOption("choice").getAsBoolean();

        if (choice == guildSettings.isAutoQuote()) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("Auto-quoting is already " + (choice ? "enabled" : "disabled"))
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();

        if (choice)
            builder.setDescription("Auto-quoting has been enabled!")
                    .setColor(EmbedColour.YES.getColour());
        else
            builder.setDescription("Auto-quoting has been disabled!")
                    .setColor(EmbedColour.NO.getColour());

        guildSettings.setAutoQuote(choice).save();
        event.replyEmbeds(builder.build()).queue();

        new ActionLogger(event.getGuild())
                .addAction("Auto-quoting has been " + (choice ? "enabled" : "disabled") + "!")
                .addModerator(event.getUser())
                .log();
    }

    @Command(name = "settings.guild.dm-on-warn", description = "Toggles Titan messaging someone when they are warned", permission = "command.settings.guild.dm-on-warn")
    public static void toggleDmOnWarn(SlashCommandEvent event) {

        GuildSettings guildSettings = new GuildSettings(event.getGuild().getId());

        if (event.getOption("choice") == null) {
            EmbedBuilder builder = new EmbedBuilder()
                    .setColor(EmbedColour.NEUTRAL.getColour());

            if (guildSettings.isDmOnWarn()) builder.setDescription("Messaging when warned is currently enabled");
            else builder.setDescription("Message when warned is currently disabled");

            event.replyEmbeds(builder.build()).queue();
            return;
        }

        boolean choice = event.getOption("choice").getAsBoolean();

        if (choice == guildSettings.isDmOnWarn()) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("Messaging when warned is already " + (choice ? "enabled" : "disabled"))
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
            return;
        }
        EmbedBuilder builder = new EmbedBuilder();

        if (choice)
            builder.setDescription("Messaging on warn has been enabled!")
                    .setColor(EmbedColour.YES.getColour());
        else
            builder.setDescription("Messaging on warn has been disabled!")
                    .setColor(EmbedColour.NO.getColour());

        guildSettings.setDmOnWarn(choice).save();
        event.replyEmbeds(builder.build()).queue();

        new ActionLogger(event.getGuild())
                .addAction("Messaging when warned has been " + (choice ? "enabled" : "disabled") + "!")
                .addModerator(event.getUser())
                .log();
    }

    @Command(name = "settings.guild.botlogs-channel", description = "Sets the channel Titan should send log messages to, when someone performs an action with Titan", permission = "command.settings.guild.botlogs-channel")
    public static void botLogsChannel(SlashCommandEvent event) {
        GuildSettings guildSettings = new GuildSettings(event.getGuild().getId());

        if (event.getOption("channel") == null) {

            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("Logging actions has been disabled!")
                    .setColor(EmbedColour.YES.getColour()).build()).queue();

            guildSettings.setBotLogsChannel(null).save();

            return;
        }

        TextChannel channel = event.getJDA().getTextChannelById(event.getOption("channel").getAsGuildChannel().getId());

        guildSettings.setBotLogsChannel(channel.getId()).save();

        event.replyEmbeds(new EmbedBuilder()
                .setDescription("The action logging channel has been set to " + channel.getAsMention())
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();
    }
}
