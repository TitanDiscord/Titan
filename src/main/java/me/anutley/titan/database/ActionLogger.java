package me.anutley.titan.database;

import me.anutley.titan.database.objects.GuildSettings;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

public class ActionLogger {

    private final Guild guild;
    private final EmbedBuilder builder;

    public ActionLogger(Guild guild) {
        this.guild = guild;
        this.builder = getEmbed();
    }

    public void log() {
        try {
            guild.getTextChannelById(new GuildSettings(this.guild.getId()).getBotLogChannelId()).sendMessageEmbeds(this.builder.build())
                    .queue(null, new ErrorHandler()
                            .ignore(ErrorResponse.UNKNOWN_CHANNEL)
                            .ignore(ErrorResponse.MISSING_PERMISSIONS));
        } catch (NullPointerException | IllegalArgumentException ignored) {
        }
    }

    public static EmbedBuilder getEmbed() {
        return new EmbedBuilder()
                .setTitle("New Action Logged")
                .setColor(EmbedColour.MODERATION_LOGGING.getColour());
    }

    public ActionLogger addAction(String action) {
        this.builder.addField("Action", action, false);
        return this;
    }

    public ActionLogger addTarget(User target) {
        this.builder.addField("Target", target.getAsMention(), false);
        return this;
    }

    public ActionLogger addTarget(Role target) {
        this.builder.addField("Target", target.getAsMention(), false);
        return this;
    }

    public ActionLogger addModerator(User moderator) {
        this.builder.addField("Moderator", moderator.getAsMention(), false);
        return this;
    }

    public ActionLogger addReason(String reason) {
        this.builder.addField("Reason", reason, false);
        return this;
    }

    public ActionLogger addChannel(GuildChannel channel) {
        this.builder.addField("Channel", channel.getAsMention(), false);
        return this;
    }

    public ActionLogger addOldValue(String oldVal) {
        this.builder.addField("Old Value", oldVal, false);
        return this;
    }

    public ActionLogger addOldValue(boolean oldVal) {
        this.builder.addField("Old Value", String.valueOf(oldVal), false);
        return this;
    }

    public ActionLogger addNewValue(String newVal) {
        this.builder.addField("New Value", newVal, false);
        return this;
    }

    public ActionLogger addNewValue(boolean newVal) {
        this.builder.addField("New Value", String.valueOf(newVal), false);
        return this;
    }

    public ActionLogger addExtraInfo(String fieldName, String fieldDescription) {
        this.builder.addField(fieldName, fieldDescription, false);
        return this;
    }


}
