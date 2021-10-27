package me.anutley.titan.commands.settings;

import me.anutley.titan.commands.Command;
import me.anutley.titan.database.objects.GuildSettings;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

public class GuildSettingsCommand {

    public static SubcommandGroupData GuildSettingsCommandData = new SubcommandGroupData("guild", "Controls Titan's guild settings")
            .addSubcommands(new SubcommandData("autoquote", "Toggles Titan's autoquote functionality")
                    .addOption(OptionType.BOOLEAN, "choice", "Whether or not Titan should automatically create a quote embed when someone posts a message link"))
            .addSubcommands(new SubcommandData("dm-on-warn", "Toggles Titan messaging someone when they are warned")
                    .addOption(OptionType.BOOLEAN, "choice", "Whether or not Titan message someone when they are warned"));


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

        EmbedBuilder builder = new EmbedBuilder();

        if (choice)
            builder.setDescription("Auto-quoting has been enabled!")
                    .setColor(EmbedColour.YES.getColour());
        else
            builder.setDescription("Auto-quoting has been disabled!")
                    .setColor(EmbedColour.NO.getColour());

        guildSettings.setAutoQuote(choice).save();
        event.replyEmbeds(builder.build()).queue();
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

        EmbedBuilder builder = new EmbedBuilder();

        if (choice)
            builder.setDescription("Messaging on warn has been enabled!")
                    .setColor(EmbedColour.YES.getColour());
        else
            builder.setDescription("Messaging on warn has been disabled!")
                    .setColor(EmbedColour.NO.getColour());

        guildSettings.setDmOnWarn(choice).save();
        event.replyEmbeds(builder.build()).queue();
    }

}
