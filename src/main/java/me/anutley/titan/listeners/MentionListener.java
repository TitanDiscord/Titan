package me.anutley.titan.listeners;

import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MentionListener extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;

        if (event.getMessage().getMentionedMembers().size() == 1) {

            if (event.getMessage().getContentRaw().split(" ").length > 1) return;
            if (event.getMessage().getMentionedMembers().get(0).getUser().getId()
                    .equals(event.getJDA().getSelfUser().getId())) {
                event.getChannel().sendMessageEmbeds(
                        new EmbedBuilder()
                                .setTitle("Hi! 👋")
                                .setColor(EmbedColour.NEUTRAL.getColour())
                                .setDescription("All Titan's Command are Slash Commands" +
                                        "\n[GitHub Page](https://github.com/ANutley/Titan/)" +
                                        "\n[Documentation](https://titan.anutley.me/)" +
                                        "\n[Invite Me](https://discord.com/api/oauth2/authorize?client_id=853225073023909918&permissions=8&scope=bot%20applications.commands)" +
                                        "\n[Support Server](https://discord.gg/4ueXW4fwrR)")

                                .setFooter("If you are trying to trigger a tag, the format is @Titan <tag trigger>")
                                .build()).queue();
            }
        }
    }
}
