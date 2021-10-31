package me.anutley.titan.commands.dev;

import me.anutley.titan.Config;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.jodah.expiringmap.ExpiringMap;
import org.jetbrains.annotations.NotNull;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        if (!event.getAuthor().getId().equals(Config.getInstance().get("BOT_OWNER"))
                || !event.getMessage().getContentRaw().contains("eval")
                || !previousEvals.containsKey(event.getMessageId())) return;
        eval(event.getMessage());
    }

    @Override
    public void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent event) {
        if (previousEvals.containsKey(event.getMessageId())) {
            event.getChannel().deleteMessageById(previousEvals.get(event.getMessageId())).queue(null, new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE));
            previousEvals.remove(event.getMessageId());
        }
    }

    public void eval(Message message) {

        String code = message.getContentRaw().split("eval")[1];

        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("js");

        String script = "with (_javaImports) {\n" + code + "\n}";

        Bindings bindings = scriptEngine.createBindings();
        importBindings(scriptEngine, bindings);

        bindings.put("message", message);
        bindings.put("author", message.getAuthor());
        bindings.put("member", message.getMember());
        bindings.put("channel", message.getChannel());
        bindings.put("guild", message.getGuild());
        bindings.put("jda", message.getJDA());

        EmbedBuilder builder = new EmbedBuilder();
        try {
            long time = System.currentTimeMillis();
            String output = scriptEngine.eval(script, bindings).toString();

            time = System.currentTimeMillis() - time;

            builder.setAuthor(message.getAuthor().getName(), null, message.getAuthor().getAvatarUrl());
            builder.setColor(EmbedColour.YES.getColour());
            builder.addField("Evaluated Code", "```java\n" + output + "\n```", false);
            builder.addField("Time Taken To Evaluate", time + "ms", true);

        } catch (Exception e) {
            builder.setAuthor(message.getAuthor().getName(), null, message.getAuthor().getAvatarUrl());
            builder.setColor(EmbedColour.NO.getColour());
            builder.addField("Error", "```\n" + e.getMessage() + "\n```", false);
            builder.addField("Your Code", "```java\n" + code + "\n```", false);

        }

        String existingMessageId = previousEvals.get(message.getId());
        if (existingMessageId != null) {
            message.getTextChannel().editMessageEmbedsById(existingMessageId, builder.build()).queue();
        } else {
            message.getTextChannel().sendMessageEmbeds(builder.build()).queue(newMessage -> previousEvals.put(message.getId(), newMessage.getId()));
        }
    }

    private static void importBindings(ScriptEngine scriptEngine, Bindings bindings) {
        List<String> imports = Arrays.asList(
                "me.anutley.titan",
                "me.anutley.titan.util",
                "me.anutley.titan.database",
                "net.dv8tion.jda.api",
                "net.dv8tion.jda.api.entities",
                "net.dv8tion.jda.api.utils",
                "net.dv8tion.jda.internal.utils",
                "java.lang",
                "java.util");
        try {
            scriptEngine.eval("var _javaImports = new JavaImporter(" +
                    imports.stream().map(i -> i.startsWith("java") ? i : "Packages." + i).collect(Collectors.joining(","))
                    + ");", bindings);

        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "eval";
    }

}

