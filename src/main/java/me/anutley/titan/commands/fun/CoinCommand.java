package me.anutley.titan.commands.fun;

import me.anutley.titan.commands.Command;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.concurrent.ThreadLocalRandom;

public class CoinCommand {

    public static CommandData CoinCommandData = new CommandData("coin", "Flips a coin");


    @Command(name = "coin", description = "Flips a coin", permission = "command.fun.coin")
    public static void coinCommand(SlashCommandEvent event) {
        boolean result = ThreadLocalRandom.current().nextBoolean();

        if (result)
            event.replyEmbeds(new EmbedBuilder().setTitle("Heads").setColor(EmbedColour.NEUTRAL.getColour()).build()).queue();
        if (!result)
            event.replyEmbeds(new EmbedBuilder().setTitle("Tails").setColor(EmbedColour.NEUTRAL.getColour()).build()).queue();
    }

}
