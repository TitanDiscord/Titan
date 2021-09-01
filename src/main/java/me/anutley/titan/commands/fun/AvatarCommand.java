package me.anutley.titan.commands.fun;

import me.anutley.titan.commands.Command;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class AvatarCommand extends Command {


    public static CommandData AvatarCommandData = new CommandData("avatar", "Returns the avatar of either yourself or a specified user")
            .addOption(OptionType.USER, "user", "The user you want to find the avatar of", false);

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        User userAvatar;
        if (!event.getName().equals("avatar")) return;

        if(event.getOption("user") == null) userAvatar = event.getUser();
        else userAvatar = event.getOption("user").getAsUser();

        if (userAvatar.getAvatarUrl() == null) {
            event.replyEmbeds(new EmbedBuilder()
                    .setTitle("This user does not have an avatar")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();
        }

        else {
            event.reply(userAvatar.getAvatarUrl()).queue();
        }
    }

    @Override
    public String getCommandName() {
        return "avatar";
    }

    @Override
    public String getCommandDescription() {
        return "Returns the avatar of either yourself or a specified user";
    }

    @Override
    public String getCommandUsage() {
        return "/avatar [user]";
    }
}
