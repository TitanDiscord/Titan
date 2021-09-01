package me.anutley.titan.commands.utility;

import me.anutley.titan.commands.Command;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

public class PingCommand extends Command {

    public static CommandData PingCommandData = new CommandData("ping", "Gets the current bots ping");

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (!event.getName().equals("ping")) return;

        event.replyEmbeds(new EmbedBuilder()
                .setTitle("🏓 Gateway Ping - " + event.getJDA().getGatewayPing())
                .setColor(EmbedColour.NEUTRAL.getColour())
                .build()).queue();
    }

    @Override
    public String getCommandName() {
        return "ping";
    }

    @Override
    public String getCommandDescription() {
        return "Gets the bots ping";
    }

    @Override
    public String getCommandUsage() {
        return "/ping";
    }
}
