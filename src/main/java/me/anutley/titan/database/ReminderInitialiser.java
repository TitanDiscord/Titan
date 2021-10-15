package me.anutley.titan.database;

import me.anutley.titan.Titan;
import me.anutley.titan.database.objects.Reminder;
import me.anutley.titan.database.util.ReminderUtil;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReminderInitialiser extends ListenerAdapter {

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public static void run() {

        Runnable initialiseReminders = () -> {
            for (Reminder reminder : ReminderUtil.getReminders()) {
                if (System.currentTimeMillis() >= reminder.getTimeInMilliseconds()) {

                    User user = Titan.getJda().retrieveUserById(reminder.getUserId()).complete();
                    TextChannel textChannel = Titan.getJda().getGuildById(reminder.getGuildId()).getTextChannelById(reminder.getChannelId());

                    MessageEmbed reminderEmbed = new EmbedBuilder()
                            .setColor(EmbedColour.NEUTRAL.getColour())
                            .setDescription("You asked me to remind you " + TimeFormat.RELATIVE.format(reminder.getTimeCreated()) + " about: \n" + reminder.getContent())
                            .build();

                    if (textChannel == null) {
                        user.openPrivateChannel().complete()
                                .sendMessage(user.getAsMention())
                                .setEmbeds(reminderEmbed).queue(null, new ErrorHandler().ignore(ErrorResponse.CANNOT_SEND_TO_USER));
                    } else {
                        Titan.getJda().getGuildById(reminder.getGuildId()).getTextChannelById(reminder.getChannelId())
                                .sendMessage(user.getAsMention())
                                .setEmbeds(reminderEmbed).queue();
                    }

                    ReminderUtil.removeReminderById(reminder.getId());
                }
            }
        };
        executor.scheduleAtFixedRate(initialiseReminders, 0, 1, TimeUnit.MINUTES);
    }

}
