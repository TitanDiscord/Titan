package me.anutley.titan.commands.settings;

import me.anutley.titan.database.objects.PingProtectionSettings;
import me.anutley.titan.database.objects.PingProtectionUserData;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.ArrayList;
import java.util.Arrays;

public class PingProtectionSettingsCommand {

    public static SubcommandGroupData PingProtectionSettingsCommandData = new SubcommandGroupData("pingprotection", "Controls Titan's ping protection settings")
            .addSubcommands(new SubcommandData("enable", "Enables ping protection for this guild"))
            .addSubcommands(new SubcommandData("disable", "Disables ping protection for this guild"))
            .addSubcommands(new SubcommandData("threshold", "Modifies the threshold of illegal pings someone can make before an action of your choice is taken")
                    .addOption(OptionType.INTEGER, "amount", "Amount of pings", true))
            .addSubcommands(new SubcommandData("add", "Adds a role to the ping protection")
                    .addOption(OptionType.ROLE, "role", "The role to add to the ping protection", true))
            .addSubcommands(new SubcommandData("remove", "Removes a role from the ping protection")
                    .addOption(OptionType.ROLE, "role", "The role to remove from the ping protection", true))
            .addSubcommands(new SubcommandData("list", "Lists all the roles that are protected from pings"))
            .addSubcommands(new SubcommandData("resetpings", "Resets a members illegal ping count")
                    .addOption(OptionType.USER, "member", "The member whose illegal ping count you want to reset", true))
            .addSubcommands(new SubcommandData("action", "Changes the action that Titan will take when someone surpasses the illegal ping threshold")
                    .addOption(OptionType.STRING, "action", "Possible options: warn, kick, ban", true));


    public void pingProtectionSettingsCommand(SlashCommandEvent event) {
        if (event.getSubcommandName().equals("enable")) togglePingProtection(event, true);
        if (event.getSubcommandName().equals("disable")) togglePingProtection(event, false);
        if (event.getSubcommandName().equals("threshold")) modifyPingThreshold(event);
        if (event.getSubcommandName().equals("add")) toggleRolePingProtection(event, true);
        if (event.getSubcommandName().equals("remove")) toggleRolePingProtection(event, false);
        if (event.getSubcommandName().equals("resetpings")) resetIllegalPings(event);
        if (event.getSubcommandName().equals("action")) changeActionWhenPingsSurpassThreshold(event);
        if (event.getSubcommandName().equals("list")) listPingProtectedRoles(event);
    }

    public void togglePingProtection(SlashCommandEvent event, boolean bool) {

        new PingProtectionSettings(event.getGuild().getId())
                .setEnabled(bool)
                .save();

        EmbedBuilder builder = new EmbedBuilder();
        if (bool) builder.setTitle("Ping protection has been enabled!");
        else builder.setTitle("Ping protection has been disabled!");

        builder.setColor(EmbedColour.YES.getColour());

        event.replyEmbeds(builder.build()).queue();
    }

    public void modifyPingThreshold(SlashCommandEvent event) {

        new PingProtectionSettings(event.getGuild().getId())
                .setThreshold(Integer.parseInt(event.getOption("amount").getAsString()))
                .save();

        event.replyEmbeds(new EmbedBuilder()
                .setTitle("The ping protection threshold has been set to " + event.getOption("amount").getAsString())
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();
    }

    public void toggleRolePingProtection(SlashCommandEvent event, boolean bool) {

        PingProtectionSettings pingProtectionSettings = new PingProtectionSettings(event.getGuild().getId());
        ArrayList<String> pingProtectedRoles = new ArrayList<>();

        if (pingProtectionSettings.getRoles() != null) {

            ArrayList<String> pingProtectedRolesAsArrayList = new ArrayList<>(Arrays.asList(pingProtectionSettings.getRoles().toString()
                    .replaceAll("\\[", "")
                    .replaceAll("]", "")
                    .split(",")));

            if (bool) {
                if (pingProtectionSettings.getRoles().contains(event.getOption("role").getAsRole().getId())) {
                    event.replyEmbeds(new EmbedBuilder()
                            .setTitle("This role is already protected from pings!")
                            .setColor(EmbedColour.NO.getColour())
                            .build()).queue();
                    return;
                }

                event.replyEmbeds(new EmbedBuilder()
                        .setDescription(event.getOption("role").getAsRole().getAsMention() + " is now protected from pings!")
                        .setColor(EmbedColour.YES.getColour())
                        .build()).queue();

                pingProtectedRolesAsArrayList.add(event.getOption("role").getAsRole().getId());
            }

            if (!bool) {
                if (!pingProtectionSettings.getRoles().contains(event.getOption("role").getAsRole().getId())) {
                    event.replyEmbeds(new EmbedBuilder()
                            .setTitle("This role is not currently protected from pings!")
                            .setColor(EmbedColour.NO.getColour())
                            .build()).queue();
                    return;
                }

                event.replyEmbeds(new EmbedBuilder()
                        .setDescription(event.getOption("role").getAsRole().getAsMention() + " is no longer protected from pings!")
                        .setColor(EmbedColour.YES.getColour())
                        .build()).queue();

                pingProtectedRolesAsArrayList.remove(event.getOption("role").getAsRole().getId());


            }
            pingProtectedRoles = pingProtectedRolesAsArrayList;

        } else {
            if (!bool) {
                event.replyEmbeds(new EmbedBuilder()
                        .setTitle("This role is not protected from pings!")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).queue();
                return;
            } else {
                pingProtectedRoles.add(event.getOption("role").getAsRole().getId());

                event.replyEmbeds(new EmbedBuilder()
                        .setDescription(event.getOption("role").getAsRole().getAsMention() + " is now protected from pings!")
                        .setColor(EmbedColour.YES.getColour())
                        .build()).queue();

            }
        }
        pingProtectionSettings.setRoles(pingProtectedRoles).save();
    }


    public void listPingProtectedRoles(SlashCommandEvent event) {

        PingProtectionSettings pingProtectionSettings = new PingProtectionSettings(event.getGuild().getId());

        if (pingProtectionSettings.getRoles() == null) {
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("There are no ping protected roles!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();
            return;
        }

        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle("Ping Protected Roles")
                .setColor(EmbedColour.NEUTRAL.getColour());

        StringBuilder content = new StringBuilder();

        for (String string : pingProtectionSettings.getRoles()) {
            content.append(event.getGuild().getRoleById(string.trim()).getAsMention());
        }

        builder.setDescription(content.toString());
        event.replyEmbeds(builder.build()).queue();
    }

    public void resetIllegalPings(SlashCommandEvent event) {

        new PingProtectionUserData(event.getGuild().getId(), event.getOption("member").getAsUser().getId())
                .setCount(0)
                .save();

            event.replyEmbeds(new EmbedBuilder()
                    .setDescription(event.getOption("member").getAsUser().getAsMention() + "'s pings have been reset!")
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();
    }

    public void changeActionWhenPingsSurpassThreshold(SlashCommandEvent event) {

        if (!event.getOption("action").getAsString().equals("warn") &&
                !event.getOption("action").getAsString().equals("kick") &&
                !event.getOption("action").getAsString().equals("ban")) {

            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("That is not a valid option. You can choose between: warn, kick or ban!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();
        } else {

            new PingProtectionSettings(event.getGuild().getId())
                    .setAction(event.getOption("action").getAsString())
                    .save();

            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("The action when someone surpasses the illegal ping threshold has been set to `" + event.getOption("action").getAsString() + "`!")
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();

        }
    }
}
