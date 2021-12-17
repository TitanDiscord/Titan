package me.anutley.titan.commands.utility;

import me.anutley.titan.commands.Command;
import me.anutley.titan.database.objects.Reminder;
import me.anutley.titan.database.util.ReminderUtil;
import me.anutley.titan.util.Paginator;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.util.ArrayList;
import java.util.Objects;

public class RemindCommand {

    public static CommandData RemindCommandData = new CommandData("remind", "Manages reminders")
            .addSubcommands(new SubcommandData("add", "Adds a new reminder")
                    .addOption(OptionType.STRING, "content", "The content of the reminder", true)
                    .addOption(OptionType.INTEGER, "minutes", "The amount of minutes Titan should wait before reminding you")
                    .addOption(OptionType.INTEGER, "hours", "The amount of hours Titan should wait before reminding you")
                    .addOption(OptionType.INTEGER, "days", "The amount of days Titan should wait before reminding you"))
            .addSubcommands(new SubcommandData("list", "Lists your reminders"))
            .addSubcommands(new SubcommandData("info", "Give info about a specific reminder")
                    .addOption(OptionType.INTEGER, "id", "The id of the reminder you want to get information about", true))
            .addSubcommands(new SubcommandData("remove", "Removes a specific reminder")
                    .addOption(OptionType.INTEGER, "id", "The id of the reminder you want to remove", true));

    @Command(name = "remind.add", description = "Adds a new reminder", permission = "command.utility.remind.add")
    public static void createReminder(SlashCommandEvent event) {

        if (event.getOption("days") == null
                && event.getOption("hours") == null
                && event.getOption("minutes") == null) {

            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("You need to fill out at least one of the time options!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
            return;
        }

        String content = event.getOption("content").getAsString();

        if (content.length() > 2000) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("The content of the reminder can be a maximum of 2000 characters")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
            return;
        }

        long minutes = event.getOption("minutes") != null ? event.getOption("minutes").getAsLong() : 0;
        long hours = event.getOption("hours") != null ? event.getOption("hours").getAsLong() : 0;
        long days = event.getOption("days") != null ? event.getOption("days").getAsLong() : 0;

        long totalTimeInMs =
                ((minutes * 60) * 1000) +
                        (((hours * 60) * 60) * 1000) +
                        ((((days * 24) * 60) * 60) * 1000) + System.currentTimeMillis();

        Reminder reminder = new Reminder(null)
                .setGuildId(event.getGuild().getId())
                .setChannelId(event.getChannel().getId())
                .setUserId(event.getUser().getId())
                .setContent(content)
                .setTimeInMilliseconds(totalTimeInMs)
                .setTimeCreated(System.currentTimeMillis())
                .save();

        event.replyEmbeds(new EmbedBuilder()
                .setDescription("I will remind you " + TimeFormat.RELATIVE.format(totalTimeInMs) + " `(ID-" + reminder.getId() + ")`")
                .setColor(EmbedColour.NEUTRAL.getColour())
                .build()).queue();

    }

    @Command(name = "remind.list", description = "Lists your reminders", permission = "command.utility.remind.list")
    public static void listReminders(SlashCommandEvent event) {
        ArrayList<Reminder> reminders = ReminderUtil.getUsersReminders(event.getGuild().getId(), event.getMember().getId());
        int count = reminders.size();
        int tagCount = 0;
        int pageNumber = 0;
        int itemsPerPage = 15;

        StringBuilder content = new StringBuilder();
        content.append("ID - Time Until Reminder \n");
        Paginator paginator = new Paginator(event.getInteraction().getId(), event.getUser().getId());

        for (int i = 0; i < count; i += itemsPerPage) {

            pageNumber++;

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("Page " + pageNumber)
                    .setColor(EmbedColour.NEUTRAL.getColour());

            for (int j = 0; j < itemsPerPage; j++) {
                if (tagCount >= count) break;
                content.append("`" + reminders.get(tagCount).getId() + "` - " + TimeFormat.DATE_TIME_LONG.format(reminders.get(tagCount).getTimeInMilliseconds()) + "\n");
                tagCount++;
            }

            builder.setDescription(content.toString());

            paginator.addPage(builder.build());

            content.setLength(0);
        }
        if (pageNumber == 0)
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("You have no reminders!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();
        else
            event.reply(paginator.getCurrent())
                    .addActionRows(paginator.getButtons())
                    .queue();
    }

    @Command(name = "remind.info", description = "Give info about a specific reminder", permission = "command.utility.remind.info")
    public static void getReminderInfo(SlashCommandEvent event) {
        Reminder reminder = new Reminder(event.getOption("id").getAsString());

        if (!event.getGuild().getId().equals(reminder.getGuildId()) && !event.getUser().getId().equals(reminder.getUserId()))
            return;

        TextChannel channel = event.getJDA().getGuildById(reminder.getGuildId()).getTextChannelById(reminder.getChannelId());
        String channelInfo = channel != null ? channel.getAsMention() : "Deleted Channel";

        event.replyEmbeds(new EmbedBuilder()
                .setTitle("Reminder (" + reminder.getId() + ")")
                .setColor(EmbedColour.NEUTRAL.getColour())
                .setDescription("Channel -" + channelInfo +
                        "\nContent - " + reminder.getContent() +
                        "\nTime - " + TimeFormat.DATE_TIME_LONG.format(reminder.getTimeInMilliseconds()))
                .build()).queue();
    }

    @Command(name = "remind.remove", description = "Removes a specific reminder", permission = "command.utility.remind.remove")
    public static void removeReminder(SlashCommandEvent event) {

        ArrayList<Reminder> reminders = ReminderUtil.getUsersReminders(event.getGuild().getId(), event.getUser().getId());

        String reminderId = event.getOption("id").getAsString();

        Reminder reminder = new Reminder(reminderId);

        if (reminders.stream().anyMatch(r -> Objects.equals(r.getId(), reminder.getId()))) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("Reminder " + reminderId + " has been deleted!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();

            ReminderUtil.removeReminderById(reminderId);
        } else {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("You do not have a reminder in this guild with that ID")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();
        }
    }
}
