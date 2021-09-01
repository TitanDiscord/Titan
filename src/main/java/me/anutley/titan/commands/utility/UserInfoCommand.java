package me.anutley.titan.commands.utility;

import me.anutley.titan.commands.Command;
import me.anutley.titan.util.RoleUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.utils.TimeFormat;

public class UserInfoCommand extends Command {

    public static CommandData UserInfoCommandData = new CommandData("userinfo", "Gets information about a user")
            .addOption(OptionType.USER, "user", "The user to get the info from. Leave blank to get information about yourself");

    @Override
    public void onSlashCommand(SlashCommandEvent event) {

        Member member;

        if (!event.getName().equals("userinfo")) return;

        if (event.getOption("user") != null) member = event.getOption("user").getAsMember();
        else member = event.getMember();


        String memberTag = !member.isOwner() ? member.getUser().getAsTag() : member.getUser().getAsTag() + " 👑";

        event.replyEmbeds(new EmbedBuilder()
                .setAuthor(memberTag, null, member.getUser().getAvatarUrl())
                .setTitle("User Info")
                .setThumbnail(member.getUser().getAvatarUrl())
                .setColor(member.getColor())
                .addField("ID", member.getId(), true)
                .addField("Nickname", event.getMember().getEffectiveName(), true)
                .addField("Account Created", TimeFormat.RELATIVE.format(event.getUser().getTimeCreated()), true)
                .addField("Joined This Guild", TimeFormat.RELATIVE.format(event.getMember().getTimeJoined()), true)
                .addField("Highest Role", RoleUtil.highestRole(member).getAsMention(), true)
                .addField("Is Boosting", String.valueOf(event.getGuild().getBoosters().stream().anyMatch(m -> m.getId().equals(event.getMember().getId()))).replace("f", "F").replace("t", "T"), true)
                .build()).queue();


    }

    @Override
    public String getCommandName() {
        return "userinfo";
    }

    @Override
    public String getCommandDescription() {
        return "Gets information about a user";
    }

    @Override
    public String getCommandUsage() {
        return "/userinfo [user]";
    }
}
