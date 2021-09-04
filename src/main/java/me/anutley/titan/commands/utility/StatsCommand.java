package me.anutley.titan.commands.utility;

import me.anutley.titan.commands.Command;
import me.anutley.titan.util.PermissionUtil;
import me.anutley.titan.util.RoleUtil;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class StatsCommand extends Command {

    public static CommandData StatsCommandData = new CommandData("stats", "Shows the bots stats");

    private int amountOfPeopleInEachGuild;

    @Override
    public void onSlashCommand(SlashCommandEvent event) {

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

    private int amountOfMembersInTotalGuild(SlashCommandEvent event) {
        for (Guild guild : event.getJDA().getGuilds()) {
            amountOfPeopleInEachGuild += guild.getMemberCount();
        }

        return amountOfPeopleInEachGuild;
    }

    @Override
    public String getCommandName() {
        return "stats";
    }

    @Override
    public String getCommandDescription() {
        return "Sends stats about Titan";
    }

    @Override
    public String getCommandUsage() {
        return "/stats";
    }
}

