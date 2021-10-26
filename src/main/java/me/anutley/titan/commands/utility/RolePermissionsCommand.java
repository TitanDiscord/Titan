package me.anutley.titan.commands.utility;

import me.anutley.titan.commands.Command;
import me.anutley.titan.database.objects.RolePermissions;
import me.anutley.titan.util.PermissionUtil;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RolePermissionsCommand {

    public static CommandData RolePermissionsSettingsCommandData = new CommandData("permission", "Manages a roles permission")
            .addSubcommands(new SubcommandData("add", "Adds a permission to a role")
                    .addOption(OptionType.ROLE, "role", "The role to add the permission to", true)
                    .addOption(OptionType.STRING, "perm", "The permission to add", true))

            .addSubcommands(new SubcommandData("remove", "Removes a permission from a role")
                    .addOption(OptionType.ROLE, "role", "The role to remove the permission from", true)
                    .addOption(OptionType.STRING, "perm", "The permission to remove", true))

            .addSubcommands(new SubcommandData("list", "Lists a roles' permissions")
                    .addOption(OptionType.ROLE, "role", "The role to list the permissions for", true))

            .addSubcommands(new SubcommandData("createdefault", "Adds default permissions to a role (WARNING - This will clear the roles' previous permissions)")
                    .addOption(OptionType.ROLE, "role", "The role to add the permissions to", true));


    @Command(name = "permission.add", description = "Adds a permission to a role", permission = "command.utility.permission.add")
    public static void addPermission(@NotNull SlashCommandEvent event) {

        Role role = event.getOption("role").getAsRole();

        RolePermissions rolePermissions = new RolePermissions(event.getGuild().getId(), role.getId());
        ArrayList<String> rolePermissionList = rolePermissions.getPermissions();

        String perm = event.getOption("perm").getAsString();

        if (!PermissionUtil.isValidPermission(perm)) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("`" + perm + "` is not a valid permission!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
            return;
        }

        if (!event.getMember().canInteract(role)) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("You cannot modify the permissions of " + role.getAsMention() + " because it is either your highest role, or above you in the hierarchy!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
            return;
        }

        if (rolePermissionList != null) {
            if (rolePermissionList.contains(perm)) {
                event.replyEmbeds(
                        new EmbedBuilder()
                                .setDescription("This role already has the permission `" + perm + "`!")
                                .setColor(EmbedColour.NO.getColour())
                                .build()).setEphemeral(true).queue();
                return;
            }
        } else
            rolePermissionList = new ArrayList<>();

        rolePermissionList.add(perm);

        event.replyEmbeds(new EmbedBuilder()
                .setDescription("The permission `" + perm + "` has been added to " + role.getAsMention() + "!")
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();

        rolePermissions.setPermissions(rolePermissionList).save();
    }

    @Command(name = "permission.remove", description = "Removes a permission from a role", permission = "command.utility.permission.remove")
    public static void removePermission(@NotNull SlashCommandEvent event) {

        Role role = event.getOption("role").getAsRole();

        RolePermissions rolePermissions = new RolePermissions(event.getGuild().getId(), role.getId());
        ArrayList<String> rolePermissionList = rolePermissions.getPermissions();
        String perm = event.getOption("perm").getAsString();


        if (!event.getMember().canInteract(role)) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("You cannot modify the permissions of " + role.getAsMention() + " because it is either your highest role, or above you in the hierarchy")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
            return;
        }

        if (rolePermissionList != null) {
            if (!rolePermissionList.contains(perm)) {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("This role does not have the permission `" + perm + "`!")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).setEphemeral(true).queue();
                return;
            }

            rolePermissionList.remove(perm);

            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("The permission `" + perm + "` has been removed from " + role.getAsMention() + "!")
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();

            rolePermissions.setPermissions(rolePermissionList).save();
        }
        if (rolePermissionList == null)
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription(role.getAsMention() + " does not have any permissions!")
                    .setColor(EmbedColour.NEUTRAL.getColour())
                    .build()).queue();

    }

    @Command(name = "permission.list", description = "Lists a roles' permissions", permission = "command.utility.permission.list")
    public static void listPermissions(@NotNull SlashCommandEvent event) {

        Role role = event.getOption("role").getAsRole();
        StringBuilder content = new StringBuilder();
        ArrayList<String> permissions = new RolePermissions(event.getGuild().getId(), role.getId()).getPermissions();

        if (permissions == null) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription(role.getAsMention() + " does not have any permissions!")
                    .setColor(role.getColor())
                    .build()).queue();
            return;
        }

        content.append("**").append(role.getAsMention()).append("'s permissions:** \n\n");

        permissions.sort(String.CASE_INSENSITIVE_ORDER);

        for (String string : permissions) {
            content.append(string).append("\n");
        }

        event.replyEmbeds(new EmbedBuilder()
                .setColor(role.getColor())
                .setDescription(content.toString()).build()).queue();

    }

    @Command(name = "permission.createdefault", description = "Adds default permissions to a role", permission = "command.utility.permission.createdefault")
    public static void createDefaultPermissions (@NotNull SlashCommandEvent event) {

        Role role = event.getOption("role").getAsRole();
        RolePermissions rolePermissions = new RolePermissions(event.getGuild().getId(), role.getId());

        rolePermissions.setPermissions(new ArrayList<>()).save();

        ArrayList<String> permissions = new ArrayList<>();

        permissions.add("command.fun");
        permissions.add("command.utility.github");
        permissions.add("command.utility.info");
        permissions.add("command.utility.help");
        permissions.add("command.utility.invite");
        permissions.add("command.utility.ping");
        permissions.add("command.utility.reminder");
        permissions.add("command.utility.wiki");

        StringBuilder perms = new StringBuilder();

        for (String perm : permissions) {
            perms.append(perm).append("\\n");
        }

        rolePermissions.setPermissions(permissions).save();

        event.replyEmbeds(new EmbedBuilder()
                .setDescription("The role " + role.getAsMention() + " has had their permissions reset and now have the following permissions \n" + perms)
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();

    }

}
