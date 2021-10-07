package me.anutley.titan.commands.dev;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.WaitContainerResultCallback;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class UpdateCommand extends DevBaseCommand {

    private final DockerClient dockerClient;

    public UpdateCommand() {
        this.dockerClient = DockerClientBuilder.getInstance().build();
    }

    @Override
    public void onDevCommand(GuildMessageReceivedEvent event) {
        Message message = event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                .setTitle("Updating...")
                .setColor(EmbedColour.NEUTRAL.getColour())
                .build()).complete();

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

        dockerClient.waitContainerCmd(containerResponse.getId()).exec(new WaitContainerResultCallback() {
            @Override
            public void onComplete() {
                message.editMessageEmbeds(new EmbedBuilder()
                        .setTitle("Titan has been updated!")
                        .setColor(EmbedColour.YES.getColour())
                        .build()).queue();
            }
        });
    }

    @Override
    public String getName() {
        return "update";
    }
}
