package me.anutley.titan.commands.utility;

import me.anutley.titan.Titan;
import me.anutley.titan.commands.Command;
import me.anutley.titan.database.util.TagUtil;
import me.anutley.titan.database.util.WarnUtil;
import me.anutley.titan.util.RoleUtil;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.TimeFormat;

public class InfoCommand {

    public static CommandData InfoCommandData = new CommandData("info", "Gives information about various different things")
            .addSubcommands(new SubcommandData("guild", "Gives information about the current guild"))
            .addSubcommands(new SubcommandData("user", "Gets information about a user")
                    .addOption(OptionType.USER, "user", "The user to get the info from. Leave blank to get information about yourself"))
            .addSubcommands(new SubcommandData("bot", "Gets information about Titan"));

    @Command(name = "info.guild", description = "Gives information about the current guild", permission = "command.utility.info.guild")
    public static void guildInfoCommand(SlashCommandEvent event) {

        event.deferReply().queue();
        event.getGuild().loadMembers().onSuccess(members -> event.getHook().editOriginalEmbeds(guildInfo(event).build()).queue());

    }

    private static EmbedBuilder guildInfo(SlashCommandEvent event) {
        Guild guild = event.getGuild();
        EmbedBuilder builder = new EmbedBuilder()
                .setAuthor("Owner: " + guild.getOwner().getUser().getName(), null, guild.getOwner().getUser().getAvatarUrl())
                .setTitle(guild.getName())
                .setThumbnail(guild.getIconUrl())
                .setColor(EmbedColour.NEUTRAL.getColour())
                .addField("Members", String.valueOf(guild.getMembers().size()), true)
                .addField("Roles", String.valueOf(guild.getRoles().size()), true)
                .addField("Emotes", String.valueOf(guild.getEmotes().size()), true)
                .addField("Verification Level", guild.getVerificationLevel().toString(), true)
                .addField("Created", TimeFormat.RELATIVE.format(guild.getTimeCreated()), true)
                .addField("Categories", String.valueOf(guild.getCategories().size()), true)
                .addField("Channels",
                        "Text Channels: " + guild.getTextChannels().size() +
                                "\nVoice Channels: " + guild.getVoiceChannels().size() +
                                "\nStage Channels: " + guild.getStageChannels().size()
                        , true)
                .addField("Boosters", String.valueOf(guild.getBoosters().size()), true)
                .addField("Boost Count", String.valueOf(guild.getBoostCount()), true)
                .addField("Boost Level", String.valueOf(guild.getBoostTier()), true)
                .addField("Total Tags", String.valueOf(TagUtil.getGuildsEmbedTags(guild.getId()).size() + TagUtil.getGuildsTextTags(guild.getId()).size()), true)
                .addField("Total Warnings", String.valueOf(WarnUtil.getGuildsWarnings(guild.getId()).size()), true);

        if (guild.getBoostRole() != null)
            builder.addField("Boost Role", guild.getBoostRole().getAsMention(), true);

        return builder;
    }

    @Command(name = "info.user", description = "Gets information about a user", permission = "command.utility.info.user")
    public static void userInfoCommand(SlashCommandEvent event) {
        Member member;

        if (event.getOption("user") != null) member = event.getOption("user").getAsMember();
        else member = event.getMember();


        String memberTag = !member.isOwner() ? member.getUser().getAsTag() : member.getUser().getAsTag() + " 👑";
        boolean boosting;

        boosting = member.getTimeBoosted() != null;

        event.replyEmbeds(new EmbedBuilder()
                .setAuthor(memberTag, null, member.getUser().getAvatarUrl())
                .setTitle("User Info")
                .setThumbnail(member.getUser().getAvatarUrl())
                .setColor(member.getColor())
                .addField("ID", member.getId(), true)
                .addField("Nickname", member.getEffectiveName(), true)
                .addField("Account Created", TimeFormat.RELATIVE.format(member.getUser().getTimeCreated()), true)
                .addField("Joined This Guild", TimeFormat.RELATIVE.format(member.getTimeJoined()), true)
                .addField("Highest Role", RoleUtil.highestRole(member).getAsMention(), true)
                .addField("Is Boosting", String.valueOf(boosting).replace("f", "F").replace("t", "T"), true)
                .build()).queue();

    }

    @Command(name = "info.bot", description = "Gets information about Titan", permission = "command.utility.info.bot")
    public static void botInfoCommand(SlashCommandEvent event) {

        EmbedBuilder builder = new EmbedBuilder();

        event.replyEmbeds(
                builder.setAuthor(event.getJDA().getSelfUser().getAsTag() + "'s info", null, event.getJDA().getSelfUser().getAvatarUrl())
                        .setColor(EmbedColour.NEUTRAL.getColour())
                        .setDescription("[Support Server](https://discord.gg/4ueXW4fwrR)" +
                                "\n[Wiki](https://titan.anutley.me)" +
                                "\n[Top.gg](https://top.gg/bot/853225073023909918)")
                        .addField("Amount of Guilds", String.valueOf(event.getJDA().getGuilds().size()), true)
                        .addField("Total Amount Of Member (from all guilds)", String.valueOf(amountOfMembersInTotalGuild(event)), true)
                        .addField("Ping", String.valueOf(event.getJDA().getGatewayPing()), true)
                        .addField("Uptime", TimeFormat.RELATIVE.format(Titan.getStartupTime()), true)

                        .build()).queue();

    }

    private static long amountOfMembersInTotalGuild(SlashCommandEvent event) {
        long members = 0;

        for (Guild guild : event.getJDA().getGuilds())
            members += guild.getMemberCount();

        return members;
    }

}
