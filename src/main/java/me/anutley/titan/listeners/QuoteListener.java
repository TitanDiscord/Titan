package me.anutley.titan.listeners;

import me.anutley.titan.database.objects.GuildSettings;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuoteListener extends ListenerAdapter {

    private final Pattern MESSAGE_URL_REGEX = Pattern.compile("(?<BeforeLink>\\S+\\s+\\S*)?https?://(?:(?:ptb|canary)\\.)?discord(app)?\\.com/channels/(?<GuildId>.+)/(?<ChannelId>\\d+)/(?<MessageId>\\d+)/?(?<AfterLink>\\S*\\s+\\S+)?");

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        autoQuote(event);
        manualQuote(event);
    }

    public void autoQuote(GuildMessageReceivedEvent event) {
        Matcher matcher = MESSAGE_URL_REGEX.matcher(event.getMessage().getContentRaw());

        if (!matcher.matches()) return;
        if (!new GuildSettings(event.getGuild().getId()).isAutoQuote()) return;
        if (event.getMessage().getMentionedUsers().size() != 0)
            if (event.getMessage().getMentionedUsers().get(0).equals(event.getJDA().getSelfUser())) return;

        String guildId = matcher.group("GuildId");
        String channelId = matcher.group("ChannelId");
        String messageId = matcher.group("MessageId");
        String beforeLink = matcher.group("BeforeLink");
        String afterLink = matcher.group("AfterLink");

        if (attemptEmbedSend(event, guildId, channelId, messageId))
            if (beforeLink == null && afterLink == null)
                event.getMessage().delete().queue(null, new ErrorHandler().ignore(ErrorResponse.MISSING_PERMISSIONS));

    }

    public void manualQuote(GuildMessageReceivedEvent event) {
        Message message = event.getMessage();

        if (!message.getMentionedUsers().contains(message.getJDA().getSelfUser())) return;

        String[] args = message.getContentRaw().split(" ");
        Matcher matcher = MESSAGE_URL_REGEX.matcher(args[1]);

        if (!matcher.matches()) return;

        String guildId = matcher.group("GuildId");
        String channelId = matcher.group("ChannelId");
        String messageId = matcher.group("MessageId");

        if (attemptEmbedSend(event, guildId, channelId, messageId))
            event.getMessage().delete().queue(null, new ErrorHandler().ignore(ErrorResponse.MISSING_PERMISSIONS));

    }

    public MessageEmbed createEmbed(Guild guild, TextChannel channel, Message message, GuildMessageReceivedEvent event) {
        if (guild == null || channel == null || message == null) return null;

        String jumpUrl = MarkdownUtil.maskedLink("Click here to jump!", message.getJumpUrl());
        String content = (message.getContentRaw().length() == 0) ?
                jumpUrl :
                message.getContentRaw() + "\n " + jumpUrl;


        return new EmbedBuilder()
                .setAuthor("Original message by " + message.getAuthor().getName() + " in #" + channel.getName(), null, message.getAuthor().getAvatarUrl())
                .setColor(EmbedColour.NEUTRAL.getColour())
                .setDescription(
                        (content.trim()).substring(0, Math.min(content.length(), 2000))
                )
                .setFooter("Quoted by " + event.getAuthor().getName(), event.getAuthor().getAvatarUrl())
                .setTimestamp(message.getTimeCreated())
                .build();

    }

    private boolean attemptEmbedSend(GuildMessageReceivedEvent event, String guildId, String channelId, String messageId) {

        Guild guild = event.getJDA().getGuildById(guildId);
        TextChannel channel = event.getJDA().getTextChannelById(channelId);
        Message message = channel.retrieveMessageById(messageId).complete();

        MessageEmbed embed = createEmbed(guild, channel, message, event);

        if (embed == null) return false;
        else event.getChannel().sendMessageEmbeds(embed).queue();
        return true;
    }

}
