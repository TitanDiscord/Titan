package me.anutley.titan.commands.fun;

import me.anutley.titan.commands.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.concurrent.ThreadLocalRandom;

public class DieCommand {

    public static CommandData DieCommandData = new CommandData("die", "Rolls a die");

    @Command(name = "die", description = "Rolls a die", permission = "command.fun.die")
    public static void dieCommand(SlashCommandEvent event) {
        int num = ThreadLocalRandom.current().nextInt(1, 7);

        if (num == 1) event.reply("1️⃣").queue();
        if (num == 2) event.reply("2️⃣").queue();
        if (num == 3) event.reply("3️⃣").queue();
        if (num == 4) event.reply("4️⃣").queue();
        if (num == 5) event.reply("5️⃣").queue();
        if (num == 6) event.reply("6️⃣").queue();
    }

}
