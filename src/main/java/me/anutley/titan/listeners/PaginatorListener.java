package me.anutley.titan.listeners;

import me.anutley.titan.util.Paginator;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ButtonInteraction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PaginatorListener extends ListenerAdapter {

    private static final List<Paginator> paginators = new ArrayList<>();

    public void onButtonClick(@NotNull ButtonClickEvent event) {

        Paginator chosenPaginator = null;

        for (Paginator paginator : paginators) {
            if (event.getComponentId().startsWith(paginator.getId()))
                chosenPaginator = paginator;
        }


        ButtonInteraction interaction = event.getInteraction();
        String[] id = interaction.getComponentId().split(":");
        String operation = id[3];

        if (chosenPaginator == null) {
            if (id[0].equals("paginator")) {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("This paginator has timed out!")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).setEphemeral(true).queue();
            }
            return;
        }

        if (!interaction.getUser().getId().equals(chosenPaginator.getId().split(":")[1])) return;
        if (!id[2].equals(chosenPaginator.getId().split(":")[2])) return;

        event.deferEdit().queue();

        switch (operation) {
            case "next":
                interaction.getHook().editOriginal(chosenPaginator.getNext())
                        .setActionRows(chosenPaginator.getButtons())
                        .queue();

                break;
            case "prev":
                interaction.getHook().editOriginal(chosenPaginator.getPrev())
                        .setActionRows(chosenPaginator.getButtons())
                        .queue();

                break;
            case "delete":
                event.getHook().deleteOriginal().queue();
                break;
        }
    }

    public static void addPaginator(Paginator paginator) {
        paginators.add(paginator);
    }


}
