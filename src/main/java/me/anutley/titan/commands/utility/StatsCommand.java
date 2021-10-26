package me.anutley.titan.commands.utility;

import me.anutley.titan.commands.Command;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class StatsCommand {

    public static CommandData StatsCommandData = new CommandData("stats", "Shows some statistics about Titan");

    @Command(name = "stats", description = "Shows some statistics about Titan", permission = "command.utility.stats")
    public static void statsCommand(SlashCommandEvent event) {

        if (!event.getName().equals("stats")) return;

        EmbedBuilder builder = new EmbedBuilder();

        event.replyEmbeds(
        builder.setAuthor(event.getJDA().getSelfUser().getAsTag() + "'s stats", null, event.getJDA().getSelfUser().getAvatarUrl())
                .setColor(EmbedColour.NEUTRAL.getColour())
                .addField("Amount of Guilds", String.valueOf(event.getJDA().getGuilds().size()), true)
                .addField("Total Amount Of Member (from all guilds)", String.valueOf(amountOfMembersInTotalGuild(event)), true)
                .addField("Ping", String.valueOf(event.getJDA().getGatewayPing()), true)

                .build()).queue();

    }

    private static int amountOfMembersInTotalGuild(SlashCommandEvent event) {
        int amountOfPeopleInEachGuild = 0;
        for (Guild guild : event.getJDA().getGuilds()) {
            amountOfPeopleInEachGuild += guild.getMemberCount();
        }
        return amountOfPeopleInEachGuild;
    }
}

