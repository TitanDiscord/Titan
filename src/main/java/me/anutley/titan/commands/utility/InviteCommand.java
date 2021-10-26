package me.anutley.titan.commands.utility;

import me.anutley.titan.commands.Command;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class InviteCommand {

    public static CommandData InviteCommandData = new CommandData("invite", "Sends an invite link for Titan");

    @Command(name = "invite", description = "Sends an invite link for Titan", permission = "command.utility.invite")
    public static void guildInfoCommand(SlashCommandEvent event) {
        if (!event.getName().equals("invite")) return;

        event.replyEmbeds(new EmbedBuilder()
                .setTitle("Click Here To Invite Me!", "https://discord.com/api/oauth2/authorize?client_id=853225073023909918&permissions=8&scope=bot%20applications.commands")
                .setColor(EmbedColour.NEUTRAL.getColour())
                .build()).queue();
    }
}
