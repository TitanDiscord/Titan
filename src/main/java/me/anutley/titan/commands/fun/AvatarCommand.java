package me.anutley.titan.commands.fun;

import me.anutley.titan.commands.Command;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class AvatarCommand {


    public static final CommandData AvatarCommandData = new CommandData("avatar", "Returns the avatar of either yourself or a specified user")
            .addOption(OptionType.USER, "user", "The user you want to find the avatar of", false);

    @Command(name = "avatar", description = "Returns the avatar of either yourself or a specified user", permission = "command.fun.avatar")
    public static void avatarCommand(SlashCommandEvent event) {

        User userAvatar;

        if (event.getOption("user") == null) userAvatar = event.getUser();
        else userAvatar = event.getOption("user").getAsUser();

        if (userAvatar.getAvatarUrl() == null) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription(userAvatar.getAsMention() + " does not have an avatar")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
        } else {
            event.reply(userAvatar.getAvatarUrl()).queue();
        }
    }

}
