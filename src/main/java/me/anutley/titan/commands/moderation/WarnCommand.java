package me.anutley.titan.commands.moderation;

import me.anutley.titan.commands.Command;
import me.anutley.titan.database.objects.GuildSettings;
import me.anutley.titan.database.objects.Warning;
import me.anutley.titan.database.util.WarnUtil;
import me.anutley.titan.util.PermissionUtil;
import me.anutley.titan.util.RoleUtil;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class WarnCommand extends Command {

    public static CommandData WarnCommandData = new CommandData("warn", "Warn base command")
            .addSubcommands(new SubcommandData("add", "Warns a user")
                    .addOption(OptionType.USER, "user", "The user to warn", true)
                    .addOption(OptionType.STRING, "reason", "The reason to warn the user", true))
            .addSubcommands(new SubcommandData("list", "Lists all the warnings of a user")
                    .addOption(OptionType.USER, "user", "The user to warn", true))
            .addSubcommands(new SubcommandData("remove", "Removes a warn from a user by its punishment id")
                    .addOption(OptionType.USER, "user", "The user that has the warning you want to remove", true)
                    .addOption(OptionType.STRING, "id", "The id of the warn you want to remove (Can be found by doing /warn list)", true))
            .addSubcommands(new SubcommandData("clear", "Clears all warns from a user")
                    .addOption(OptionType.USER, "user", "The user whose warns you want to clear", true));

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {

        if (!event.getName().equals("warn")) return;

        if (!RoleUtil.isStaff(event.getMember())) {
            if (!event.getSubcommandName().equals("list")) {
                event.replyEmbeds(PermissionUtil.needRoleEmbed(event, new GuildSettings(event.getGuild().getId()).getModRole()).build()).setEphemeral(true).queue();
                return;
            }
        }

        if (event.getSubcommandName().equals("add")) addWarn(event);
        if (event.getSubcommandName().equals("remove")) removeWarn(event);
        if (event.getSubcommandName().equals("clear")) clearWarns(event);
        if (event.getSubcommandName().equals("list")) listWarns(event);

    }

    public void addWarn(SlashCommandEvent event) {

        String reason = event.getOption("reason").getAsString();
        boolean trimmed = false;

        if (reason.length() > 1024) {
            reason = reason.substring(0, 1024);
            trimmed = true;
        }

        new Warning(null)
                .setGuildId(event.getGuild().getId())
                .setUserId(event.getOption("user").getAsUser().getId())
                .setModeratorId(event.getMember().getId())
                .setContent(reason)
                .setTimeCreated(System.currentTimeMillis())
                .save();

        event.replyEmbeds(new EmbedBuilder()
                .setColor(EmbedColour.YES.getColour())
                .setDescription(event.getOption("user").getAsUser().getAsMention() + " has been warned for " + (trimmed ? reason + "(Trimmed to 1024 chars)" : reason))
                .build()).queue();

    }

    public void listWarns(SlashCommandEvent event) {

        User user = event.getOption("user").getAsUser();
        ArrayList<Warning> warnings = WarnUtil.getUsersWarnings(event.getGuild().getId(), user.getId());
        int count = warnings.size();
        int warningCount = 0;
        int pageNumber = 0;


        for (int i = 0; i < count; i += 25) {

            pageNumber++;

            EmbedBuilder builder = new EmbedBuilder();
            builder.setAuthor("Warnings for " + user.getAsTag(), null, user.getAvatarUrl())
                    .setColor(EmbedColour.NEUTRAL.getColour())
                    .setFooter("Page " + pageNumber);

            for (int j = 0; j < 25; j++) {
                if (warningCount >= count) break;

                Warning warning = warnings.get(warningCount);

                User moderator = event.getJDA().getUserById(warning.getModeratorId());
                builder.addField(warning.getId() + " - by " + moderator.getAsTag() + " - " + TimeFormat.DATE_TIME_LONG.format(warning.getTimeCreated()), warning.getContent(), false);
                warningCount++;
            }

            if (pageNumber == 1)
                event.replyEmbeds(builder.setDescription("**ID - Moderator** \nReason")
                        .build()).queue();
            else
                event.getChannel().sendMessageEmbeds(builder.build()).queue();

        }
        if (pageNumber == 0)
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("They have no warnings!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();

    }

    public void removeWarn(SlashCommandEvent event) {

        String id = event.getOption("id").getAsString();
        User user = event.getOption("user").getAsUser();

        Warning warning = new Warning(id);

        ArrayList<Warning> warnings = WarnUtil.getUsersWarnings(event.getGuild().getId(), user.getId());

        if (warnings.stream().anyMatch(w -> Objects.equals(w.getId(), warning.getId()))) {
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("Warning (" + event.getOption("id").getAsString() + ") has been deleted!")
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();
            WarnUtil.removeWarningById(id);

        } else {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("A warning with the id of `" + event.getOption("id").getAsString() + "` for " + user.getAsMention() + " could not be found!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();
        }


    }

    public void clearWarns(SlashCommandEvent event) {

        User user = event.getOption("user").getAsUser();

        ArrayList<Warning> warnings = WarnUtil.getUsersWarnings(event.getGuild().getId(), user.getId());

        if (warnings.size() >= 1) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription(user.getAsMention() + "'s warnings have been cleared!")
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();

            WarnUtil.clearUsersWarnings(event.getGuild().getId(), user.getId());

        } else {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription(user.getAsMention() + " does not have any warnings")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();
        }
    }

    @Override
    public String getCommandName() {
        return "warn";
    }

    @Override
    public String getCommandDescription() {
        return "Controls Titan's warn functionality";
    }

    @Override
    public String getCommandUsage() {
        return "/warn add <user> <reason>" +
                "\n/warn remove <user> " +
                "\n/warn list <user>" +
                "\n/warn clear <user>";
    }
}
