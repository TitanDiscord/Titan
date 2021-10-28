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

public class SetNickCommand {

    public static CommandData SetNickCommandData = new CommandData("setnick", "Sets a user's nickname, run this command without the nickname option to reset it")
            .addOption(OptionType.USER, "member", "The members' whose nickname you want to set", true)
            .addOption(OptionType.STRING, "nickname", "The nickname that you want to give the user");

    @Command(name = "setnick", description = "Sets a user's nickname, run this command without the nickname option to reset it", permission = "command.moderation.setnick", selfPermission = Permission.NICKNAME_MANAGE)
    public static void setNickCommand(SlashCommandEvent event) {

        Member member = event.getOption("member").getAsMember();

        String oldName = member.getEffectiveName();

        if (!event.getGuild().getSelfMember().canInteract(member)) {
            event.replyEmbeds(HierarchyError.self(event).build()).queue();
            return;
        }

        if (!event.getMember().canInteract(member)) {
            event.replyEmbeds(HierarchyError.other(event).build()).setEphemeral(true).queue();
            return;
        }

           member.modifyNickname(
                    event.getOption("nickname") != null ? event.getOption("nickname").getAsString() : null).queue();

        ActionLogger logger = new ActionLogger(event.getGuild());

        if (event.getOption("nickname") != null) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription(member.getUser().getAsMention() + "'s nickname has been changed to `" + event.getOption("nickname").getAsString() + "`!")
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();

            logger.addAction("Nickname changed")
                    .addTarget(member.getUser())
                    .addModerator(event.getUser())
                    .addOldValue(oldName)
                    .addNewValue(event.getOption("nickname").getAsString());
        } else {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription(member.getUser().getAsMention() + "'s nickname has been reset")
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();

            logger.addAction("Nickname reset")
                    .addTarget(member.getUser())
                    .addModerator(event.getUser());
        }

        logger.log();
    }
}
