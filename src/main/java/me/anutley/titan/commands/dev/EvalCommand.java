package me.anutley.titan.commands.dev;

import me.anutley.titan.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.jodah.expiringmap.ExpiringMap;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class EvalCommand extends DevBaseCommand {

    private final ExpiringMap<String, String> previousEvals = ExpiringMap.builder()
            .expiration(1, TimeUnit.HOURS)
            .build();

    @Override
    public void onDevCommand(@NotNull GuildMessageReceivedEvent event) {
        eval(event.getMessage());
    }

    @Override
    public void onGuildMessageUpdate(@NotNull GuildMessageUpdateEvent event) {
        if (!event.getAuthor().getId().equals(Config.getInstance().get("BOT_OWNER"))) return;
        eval(event.getMessage());
    }

    public void eval(Message message) {

        String code = message.getContentRaw().split("eval")[1];


        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("js");
        scriptEngine.put("message", message);
        scriptEngine.put("author", message.getAuthor());
        scriptEngine.put("channel", message.getChannel());
        scriptEngine.put("guild", message.getGuild());
        scriptEngine.put("jda", message.getJDA());

        EmbedBuilder builder = new EmbedBuilder();
        try {
            long time = System.currentTimeMillis();
            String output = scriptEngine.eval(code).toString();
            time = System.currentTimeMillis() - time;
            builder.setAuthor(message.getAuthor().getName(), null, message.getAuthor().getAvatarUrl());
            builder.setColor(Color.GREEN);
            builder.addField("Evaluated Code", "```\n" + output + "\n```", false);
            builder.addField("Time Taken To Evaluate", time + "ms", true);

        } catch (Exception e) {
            builder.setAuthor(message.getAuthor().getName(), null, message.getAuthor().getAvatarUrl());
            builder.setColor(Color.RED);
            builder.addField("Error", "```\n" + e.getMessage() + "\n```", false);
            builder.addField("Your Code", "```\n" + code + "\n```", false);

        }

        String existingMessageId = previousEvals.get(message.getId());
        if (existingMessageId != null) {
            message.getTextChannel().editMessageEmbedsById(existingMessageId, builder.build()).queue();
        } else {
            message.getTextChannel().sendMessageEmbeds(builder.build()).queue(newMessage -> previousEvals.put(message.getId(), newMessage.getId()));
        }
    }

    @Override
    public String getName() {
        return "eval";
    }

}

