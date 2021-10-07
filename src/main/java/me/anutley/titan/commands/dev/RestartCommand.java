package me.anutley.titan.commands.dev;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class RestartCommand extends DevBaseCommand {

    private final DockerClient dockerClient;

    public RestartCommand() {
        this.dockerClient = DockerClientBuilder.getInstance().build();
    }

    @Override
    public void onDevCommand(GuildMessageReceivedEvent event) {
        event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                .setTitle("Restarting!")
                .setColor(EmbedColour.NEUTRAL.getColour())
                .build()).queue();
        dockerClient.restartContainerCmd("titan").exec();
    }

    @Override
    public String getName() {
        return "restart";
    }
}
