package me.anutley.titan.commands.dev;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class RestartCommand extends DevBaseCommand {

    private final DockerClient dockerClient;

    public RestartCommand() {
        this.dockerClient = DockerClientBuilder.getInstance().build();
    }

    @Override
    public void onDevCommand(GuildMessageReceivedEvent event) {

        event.getMessage().addReaction("accepted:898671459126378517").queue();

        try {
            dockerClient.restartContainerCmd("titan").exec();
        } catch (Exception e) {
            event.getMessage().addReaction("denied:898671458954379276").queue();
        }

    }

    @Override
    public String getName() {
        return "restart";
    }
}
