package me.anutley.titan.commands.fun;

import me.anutley.titan.commands.Command;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class CoinCommand extends Command {

    public static CommandData CoinCommandData = new CommandData("coin", "Flips a coin");

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (!event.getName().equals("coin")) return;

        boolean result = ThreadLocalRandom.current().nextBoolean();

        if (result) event.replyEmbeds(new EmbedBuilder().setTitle("Heads").setColor(EmbedColour.NEUTRAL.getColour()).build()).queue();
        if (!result) event.replyEmbeds(new EmbedBuilder().setTitle("Tails").setColor(EmbedColour.NEUTRAL.getColour()).build()).queue();

    }

    @Override
    public String getCommandName() {
        return "coin";
    }

    @Override
    public String getCommandDescription() {
        return "Flips a coin";
    }

    @Override
    public String getCommandUsage() {
        return "/coin";
    }
}
