package me.anutley.titan.commands.moderation;

import me.anutley.titan.commands.Command;
import me.anutley.titan.database.ActionLogger;
import me.anutley.titan.util.embeds.errors.HierarchyError;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class BanCommand {

    public static CommandData BanCommandData = new CommandData("ban", "Bans a member")
            .addOption(OptionType.USER, "user", "The member you want to ban", true)
            .addOption(OptionType.STRING, "reason", "Reason for banning this member", true)
            .addOption(OptionType.INTEGER, "messages", "Number of days of messages to delete, set to 0 to delete none (maximum 7)");


    @Command(name = "ban", description = "Bans a member", permission = "command.moderation.ban", selfPermission = Permission.BAN_MEMBERS)
    public static void banCommand(SlashCommandEvent event) {

        OptionMapping user = event.getOption("user");
        OptionMapping messages = event.getOption("messages");
        String reason = event.getOption("reason").getAsString();
        int messagesToDelete = 0;

        if (messages != null) messagesToDelete = (int) messages.getAsLong();
        if (messagesToDelete > 7) messagesToDelete = 7;
        if (reason.length() > 470) reason = reason.substring(0, 470);


        if (!event.getGuild().getSelfMember().canInteract(user.getAsMember())) {
            event.replyEmbeds(HierarchyError.self(event).build()).queue();
            return;
        }

        if (!event.getMember().canInteract(user.getAsMember())) {
            event.replyEmbeds(HierarchyError.other(event).build()).setEphemeral(true).queue();
            return;
        }


        event.getGuild().ban(user.getAsUser(), messagesToDelete, "[" + event.getUser().getAsTag() + "] " + reason).queue();

        EmbedBuilder builder = new EmbedBuilder()
                .setColor(EmbedColour.YES.getColour());

        ActionLogger logger = new ActionLogger(event.getGuild())
                .addAction("Member banned")
                .addModerator(event.getUser())
                .addTarget(user.getAsUser())
                .addReason(reason);

        if (messagesToDelete != 0) {
            builder.setDescription(user.getAsMember().getAsMention() + " has been banned for `" + reason + "` and " + messagesToDelete + " days of messages have been deleted!");
            logger.addExtraInfo("Days Of Messages Deleted", String.valueOf(messagesToDelete));
        }

        else
            builder.setDescription(user.getAsMember().getAsMention() + " has been banned for `" + reason + "`!");

        event.replyEmbeds(builder.build()).queue();
        logger.log();

    }
}
