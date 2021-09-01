package me.anutley.titan.commands.fun;

import me.anutley.titan.commands.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class DiceCommand extends Command {

    public static CommandData DiceCommandData = new CommandData("dice", "Rolls a dice");

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (!event.getName().equals("dice")) return;

        int num = ThreadLocalRandom.current().nextInt(1, 7);

        if (num == 1) event.reply("1️⃣").queue();
        if (num == 2) event.reply("2️⃣").queue();
        if (num == 3) event.reply("3️⃣").queue();
        if (num == 4) event.reply("4️⃣").queue();
        if (num == 5) event.reply("5️⃣").queue();
        if (num == 6) event.reply("6️⃣").queue();
    }


    @Override
    public String getCommandName() {
        return "dice";
    }

    @Override
    public String getCommandDescription() {
        return "Rolls a dice";
    }

    @Override
    public String getCommandUsage() {
        return "/dice";
    }
}
