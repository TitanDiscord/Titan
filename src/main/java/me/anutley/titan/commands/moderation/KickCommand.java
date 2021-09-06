package me.anutley.titan.commands.moderation;

import me.anutley.titan.commands.Command;
import me.anutley.titan.util.PermissionUtil;
import me.anutley.titan.util.RoleUtil;
import me.anutley.titan.util.embeds.errors.HierarchyError;
import me.anutley.titan.util.embeds.errors.InsufficientPermissionError;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class KickCommand extends Command {

    public static CommandData KickCommandData = new CommandData("kick", "Kicks a member")
            .addOption(OptionType.USER, "user", "The member you want to kick", true)
            .addOption(OptionType.STRING, "reason", "Reason for banning this member", true);


    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (!event.getName().equals("kick")) return;

        Member member = event.getOption("user").getAsMember();


        if (!RoleUtil.isStaff(event.getMember())) {
            event.replyEmbeds(PermissionUtil.needRoleEmbed(event, RoleUtil.getModRole(event.getGuild())).build()).setEphemeral(true).queue();
            return;
        }

        if (RoleUtil.isMod(event.getMember()) && RoleUtil.isAdmin(member) && !event.getMember().isOwner()) {
            event.replyEmbeds(noPermissionEmbed(RoleUtil.getAdminRole(event.getGuild())).build()).setEphemeral(true).queue();
            return;
        }

        if (RoleUtil.isMod(event.getMember()) && RoleUtil.isMod(member) && !event.getMember().isOwner()) {
            event.replyEmbeds(noPermissionEmbed(RoleUtil.getModRole(event.getGuild())).build()).setEphemeral(true).queue();
            return;
        }

        if (RoleUtil.isAdmin(event.getMember()) && RoleUtil.isAdmin(member) && !event.getMember().isOwner()) {
            event.replyEmbeds(noPermissionEmbed(RoleUtil.getAdminRole(event.getGuild())).build()).setEphemeral(true).queue();
            return;
        }

        if (!event.getGuild().getSelfMember().canInteract(event.getOption("user").getAsMember())) {
            event.replyEmbeds(HierarchyError.Embed(event).build()).queue();
            return;
        }

        try {
            event.getGuild().kick(member, event.getOption("reason").getAsString()).queue();

            EmbedBuilder builder = new EmbedBuilder()
                    .setColor(EmbedColour.YES.getColour())
                    .setDescription(member.getAsMention() + " has been kicked for `" + "[" + event.getUser().getAsTag() + "] " + event.getOption("reason").getAsString() + "`!");

            event.replyEmbeds(builder.build()).queue();

        } catch (HierarchyException exception) {
            event.replyEmbeds(HierarchyError.Embed(event).build()).queue();
        } catch (InsufficientPermissionException exception) {
            event.replyEmbeds(InsufficientPermissionError.Embed(event, Permission.KICK_MEMBERS).build()).setEphemeral(true).queue();
        }

    }

    public EmbedBuilder noPermissionEmbed(Role role) {
        return new EmbedBuilder()
                .setColor(EmbedColour.NO.getColour())
                .setDescription("You do not have permission to ban someone with the " + role.getAsMention() + " role!");

    }

    @Override
    public String getCommandName() {
        return "kick";
    }

    @Override
    public String getCommandDescription() {
        return "Kicks a member";
    }

    @Override
    public String getCommandUsage() {
        return "/kick <member> <reason>";
    }
}

