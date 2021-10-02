package me.anutley.titan.commands.moderation;

import me.anutley.titan.commands.Command;
import me.anutley.titan.database.objects.GuildSettings;
import me.anutley.titan.util.PermissionUtil;
import me.anutley.titan.util.RoleUtil;
import me.anutley.titan.util.embeds.errors.HierarchyError;
import me.anutley.titan.util.embeds.errors.InsufficientPermissionError;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class BanCommand extends Command {

    public static CommandData BanCommandData = new CommandData("ban", "Bans a member")
            .addOption(OptionType.USER, "user", "The member you want to ban", true)
            .addOption(OptionType.STRING, "reason", "Reason for banning this member", true)
            .addOption(OptionType.INTEGER, "messages", "Number of days of messages to delete, set to 0 to delete none (maximum 7)");


    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (!event.getName().equals("ban")) return;

        OptionMapping user = event.getOption("user");
        OptionMapping messages = event.getOption("messages");
        int messagesToDelete = 0;

        if (messages != null) messagesToDelete = (int) messages.getAsLong();
        if (messagesToDelete > 7) messagesToDelete = 7;

        GuildSettings guildSettings = new GuildSettings(event.getGuild().getId());

        if (!RoleUtil.isStaff(event.getMember())) {
            event.replyEmbeds(PermissionUtil.needRoleEmbed(event, guildSettings.getModRole()).build()).setEphemeral(true).queue();
            return;
        }

        if (RoleUtil.isMod(event.getMember()) && RoleUtil.isAdmin(user.getAsMember()) && !event.getMember().isOwner()) {
            event.replyEmbeds(noPermissionEmbed(guildSettings.getAdminRole()).build()).setEphemeral(true).queue();
            return;
        }

        if (RoleUtil.isMod(event.getMember()) && RoleUtil.isMod(user.getAsMember()) && !event.getMember().isOwner()) {
            event.replyEmbeds(noPermissionEmbed(guildSettings.getModRole()).build()).setEphemeral(true).queue();
            return;
        }

        if (RoleUtil.isAdmin(event.getMember()) && RoleUtil.isAdmin(user.getAsMember()) && !event.getMember().isOwner()) {
            event.replyEmbeds(noPermissionEmbed(guildSettings.getAdminRole()).build()).setEphemeral(true).queue();
            return;
        }


        try {
            if (!event.getGuild().getSelfMember().canInteract(event.getOption("user").getAsMember())) {
                event.replyEmbeds(HierarchyError.Embed(event).build()).queue();
                return;
            }
            event.getGuild().ban(user.getAsUser(), messagesToDelete, "[" + event.getUser().getAsTag() + "] " + event.getOption("reason").getAsString()).queue();

            EmbedBuilder builder = new EmbedBuilder()
                    .setColor(EmbedColour.YES.getColour());

            if (messagesToDelete != 0)
                builder.setDescription(user.getAsMember().getAsMention() + " has been banned for `" + event.getOption("reason").getAsString() + "` and " + messagesToDelete + " of days of messages have been deleted!");

            else
                builder.setDescription(user.getAsMember().getAsMention() + " has been banned for `" + event.getOption("reason").getAsString() + "`!");

            event.replyEmbeds(builder.build()).queue();

        } catch (HierarchyException exception) {
            event.replyEmbeds(HierarchyError.Embed(event).build()).queue();
        } catch (InsufficientPermissionException exception) {
            event.replyEmbeds(InsufficientPermissionError.Embed(event, Permission.BAN_MEMBERS).build()).setEphemeral(true).queue();
        }
    }

    public EmbedBuilder noPermissionEmbed(Role role) {
        return new EmbedBuilder()
                .setColor(EmbedColour.NO.getColour())
                .setDescription("You do not have permission to ban someone with the " + role.getAsMention() + " role!");

    }

    @Override
    public String getCommandName() {
        return "ban";
    }

    @Override
    public String getCommandDescription() {
        return "Bans a member";
    }

    @Override
    public String getCommandUsage() {
        return "/ban <member> <reason> [messages]";
    }
}
