package me.anutley.titan.commands.dev;

import me.anutley.titan.commands.Command;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class DevBaseCommand extends Command {

    ShutdownCommand ShutdownCommand = new ShutdownCommand();
    EvalCommand EvalCommand = new EvalCommand();

    public static CommandData DevBaseCommandData = new CommandData("dev", "Commands for the developer of the bot")
            .addSubcommands(new SubcommandData("shutdown", "Shutdown the bot"))
            .addSubcommands(new SubcommandData("eval", "Evaluates code")
                    .addOption(OptionType.STRING, "code", "The code to evaluate", true));

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (!event.getName().equals("dev")) return;
        if (!event.getMember().getId().equals("804067028334936114")) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("Only <@804067028334936114> can run these commands :)")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();
            return;
        }

        if (event.getSubcommandName().equals("shutdown")) ShutdownCommand.shutdownCommand(event);
        if (event.getSubcommandName().equals("eval")) EvalCommand.evalCommand(event);

    }

    @Override
    public String getCommandName() {
        return "dev";
    }

    @Override
    public String getCommandDescription() {
        return "Developer restricted commands, don't bother trying to use them :)";
    }

    @Override
    public String getCommandUsage() {
        return "/dev eval <code> " +
                "\n/dev shutdown";
    }
}
