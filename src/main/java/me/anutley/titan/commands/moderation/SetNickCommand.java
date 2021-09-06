package me.anutley.titan.commands.moderation;

import me.anutley.titan.commands.Command;
import me.anutley.titan.util.PermissionUtil;
import me.anutley.titan.util.RoleUtil;
import me.anutley.titan.util.embeds.errors.HierarchyError;
import me.anutley.titan.util.embeds.errors.InsufficientPermissionError;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class SetNickCommand extends Command {

    public static CommandData SetNickCommandData = new CommandData("setnick", "Sets a user's nickname, run this command without the nickname option to reset it")
            .addOption(OptionType.USER, "user", "The users whose nickname you want to set", true)
            .addOption(OptionType.STRING, "nickname", "The nickname that you want to give the user");

    public void onSlashCommand(SlashCommandEvent event) {
        if (!event.getName().equals("setnick")) return;

        try {
            if (!RoleUtil.isStaff(event.getMember())) {
                event.replyEmbeds(PermissionUtil.needRoleEmbed(event, RoleUtil.getModRole(event.getGuild())).build()).setEphemeral(true).queue();
                return;
            }

            event.getOption("user").getAsMember().modifyNickname(
                    event.getOption("nickname") != null ? event.getOption("nickname").getAsString() : null).queue();

            event.replyEmbeds(new EmbedBuilder()
                    .setDescription(event.getOption("user").getAsUser().getAsMention() + "'s nickname has been changed")
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();

        } catch (HierarchyException e) {
            event.replyEmbeds(HierarchyError.Embed(event).build()).queue();
        } catch (InsufficientPermissionException exception) {
            event.replyEmbeds(InsufficientPermissionError.Embed(event, Permission.NICKNAME_MANAGE).build()).setEphemeral(true).queue();
        }
    }

    @Override
    public String getCommandName() {
        return "setnick";
    }

    @Override
    public String getCommandDescription() {
        return "Sets a user's nickname";
    }

    @Override
    public String getCommandUsage() {
        return "/setnick <user>";
    }
}
