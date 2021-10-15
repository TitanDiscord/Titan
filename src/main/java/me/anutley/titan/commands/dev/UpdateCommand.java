package me.anutley.titan.commands.dev;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class UpdateCommand extends DevBaseCommand {

    private final DockerClient dockerClient;

    public UpdateCommand() {
        this.dockerClient = DockerClientBuilder.getInstance().build();
    }

    @Override
    public void onDevCommand(GuildMessageReceivedEvent event) {

        try {
            CreateContainerResponse containerResponse = dockerClient.createContainerCmd("containrrr/watchtower")
                    .withName("titan-update")
                    .withCmd("--cleanup", "--run-once", "titan")
                    .withHostConfig(HostConfig.newHostConfig()
                            .withAutoRemove(true)
                            .withBinds(
                                    Bind.parse("/var/run/docker.sock:/var/run/docker.sock")
                            )
                    )
                    .exec();
            dockerClient.startContainerCmd(containerResponse.getId()).exec();
        } catch (Exception e) {
            event.getMessage().addReaction("denied:898671458954379276").queue();
            return;
        }

        event.getMessage().addReaction("accepted:898671459126378517").queue();
    }

    @Override
    public String getName() {
        return "update";
    }
}
