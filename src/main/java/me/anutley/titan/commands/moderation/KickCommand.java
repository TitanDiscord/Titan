package me.anutley.titan.commands.moderation;

import me.anutley.titan.commands.Command;
import me.anutley.titan.database.ActionLogger;
import me.anutley.titan.util.embeds.errors.HierarchyError;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class KickCommand {

    public static CommandData KickCommandData = new CommandData("kick", "Kicks a member")
            .addOption(OptionType.USER, "user", "The member you want to kick", true)
            .addOption(OptionType.STRING, "reason", "Reason for kicking this member", true);


    @Command(name = "kick", description = "Kicks a member", permission = "command.moderation.kick", selfPermission = Permission.KICK_MEMBERS)
    public static void kickCommand(SlashCommandEvent event) {

        Member member = event.getOption("user").getAsMember();
        String reason = event.getOption("reason").getAsString();
        if (reason.length() > 470) reason = reason.substring(0, 470);

        if (!event.getGuild().getSelfMember().canInteract(member)) {
            event.replyEmbeds(HierarchyError.self(event).build()).queue();
            return;
        }

        if (!event.getMember().canInteract(member)) {
            event.replyEmbeds(HierarchyError.other(event).build()).setEphemeral(true).queue();
            return;
        }


       member.kick("[" + event.getUser().getAsTag() + "]" + reason).queue();

        EmbedBuilder builder = new EmbedBuilder()
                .setColor(EmbedColour.YES.getColour())
                .setDescription(member.getAsMention() + " has been kicked for `" + reason + "`!");

        event.replyEmbeds(builder.build()).queue();

        new ActionLogger(event.getGuild())
                .addAction("Member kicked")
                .addModerator(event.getUser())
                .addTarget(member.getUser())
                .addReason(reason)
                .log();

    }

}

