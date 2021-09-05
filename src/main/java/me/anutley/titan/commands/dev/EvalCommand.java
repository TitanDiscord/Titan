package me.anutley.titan.commands.dev;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;

public class EvalCommand  {

    public void evalCommand(SlashCommandEvent event) {

        String code = event.getOption("code").getAsString();

        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("js");
        scriptEngine.put("event", event);
        scriptEngine.put("jda", event.getJDA());
        scriptEngine.put("guild", event.getGuild());
        scriptEngine.put("channel", event.getChannel());
        scriptEngine.put("user", event.getUser());
        scriptEngine.put("member", event.getMember());

        EmbedBuilder builder = new EmbedBuilder();
        try {
            long time = System.currentTimeMillis();
            String output = scriptEngine.eval(code).toString();
            time = System.currentTimeMillis() - time;
            builder.setAuthor(event.getUser().getName(), null, event.getUser().getAvatarUrl());
            builder.setColor(Color.GREEN);
            builder.addField("Evaluated Code", "```\n" + output + "\n```", false);
            builder.addField("Time Taken To Evaluate", time + "ms", true);

        } catch (Exception e) {
            builder.setAuthor(event.getUser().getName(), null, event.getUser().getAvatarUrl());
            builder.setColor(Color.RED);
            builder.addField("Error", "```\n" + e.getMessage() + "\n```", false);
            builder.addField("Your Code", "```\n" + code + "\n```", false);

        }
        event.replyEmbeds(builder.build()).queue();
    }
}

