package me.anutley.titan.listeners;

import me.anutley.titan.database.objects.EmbedTag;
import me.anutley.titan.database.objects.TextTag;
import me.anutley.titan.database.util.TagUtil;
import me.anutley.titan.util.exceptions.NoTagFoundException;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TagListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;
        if (event.getMessage().getMentionedMembers().size() == 1) {

            String[] args = event.getMessage().getContentRaw().split(" ");
            if (args.length < 2) return;

            if (event.getMessage().getMentionedMembers().get(0).getUser().getId()
                    .equals(event.getJDA().getSelfUser().getId())) {

                try {
                    if (TagUtil.isEmbedTag(args[1], event.getGuild().getId())) {
                        event.getChannel().sendMessageEmbeds(TagUtil.getTagEmbedBuilder(new EmbedTag(args[1], event.getGuild().getId())).build()).queue();

                    } else {
                        event.getChannel().sendMessage(new TextTag(args[1], event.getGuild().getId()).getContent()).queue();
                    }
                } catch (NoTagFoundException ignored) {
                }
            }
        }
    }
}