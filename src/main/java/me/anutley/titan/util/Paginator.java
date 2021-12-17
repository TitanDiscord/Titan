package me.anutley.titan.util;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.ArrayList;
import java.util.List;

public class Paginator {

    private final List<Message> pages = new ArrayList<>();
    private final String id;
    private final String userId;
    private int index = 0;

    public Paginator(String id, String userId) {
        this.id = id;
        this.userId = userId;
    }

    public Paginator addPage(MessageEmbed embed) {
        pages.add(new MessageBuilder().setEmbeds(embed).build());
        return this;
    }

    public String getId() {
        return "paginator:" + userId + ":" + id;
    }

    public Message getNext() {
        return isEnd() ? pages.get(index) : pages.get(++index);
    }

    public Message getPrev() {
        return isStart() ? pages.get(0) : pages.get(--index);
    }

    public Message getCurrent() {
        return pages.get(index);
    }


    private Button getNextButton() {
        return Button.primary(getId() + ":next", Emoji.fromUnicode("➡"));
    }

    private Button getPrevButton() {
        return Button.primary(getId() + ":prev", Emoji.fromUnicode("⬅"));
    }

    private Button getDeleteButton() {
        return Button.danger(getId() + ":delete", Emoji.fromUnicode("\uD83D\uDEAE"));
    }

    public ActionRow getButtons() {
        return ActionRow.of(
                getPrevButton().withDisabled(isStart()),
                getNextButton().withDisabled(isEnd()),
                getDeleteButton()
        );
    }

    private boolean isStart() {
        return index == 0;
    }

    private boolean isEnd() {
        return index == pages.size() - 1;
    }
}
