package me.anutley.titan.commands.utility;

import me.anutley.titan.commands.Command;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class PingCommand extends ListenerAdapter {

    public static CommandData PingCommandData = new CommandData("ping", "Gets the bots current gateway ping");


    @Command(name = "ping", description = "Gets the bots current gateway ping", permission = "command.utility.ping")
    public static void execute(SlashCommandEvent event) {

        event.replyEmbeds(new EmbedBuilder()
                .setDescription("🏓 Gateway Ping - " + event.getJDA().getGatewayPing())
                .setColor(EmbedColour.NEUTRAL.getColour())
                .build()).queue();
    }

}
