package me.anutley.titan.commands.utility;

import me.anutley.titan.commands.Command;
import me.anutley.titan.database.objects.Note;
import me.anutley.titan.database.util.NoteUtil;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.TimeFormat;

import java.util.ArrayList;
import java.util.Objects;

public class NoteCommand {

    public static CommandData NoteCommandData = new CommandData("note", "Manages notes")
            .addSubcommands(new SubcommandData("add", "Adds a new note")
                    .addOption(OptionType.USER, "user", "The user you want to set the note for", true)
                    .addOption(OptionType.STRING, "content", "The content of the note", true))
            .addSubcommands(new SubcommandData("remove", "Removes a specific note")
                    .addOption(OptionType.USER, "user", "The user that has the note you want to remove", true)
                    .addOption(OptionType.INTEGER, "id", "The id of the note you want to remove", true))
            .addSubcommands(new SubcommandData("edit", "Edits a specific note")
                    .addOption(OptionType.USER, "user", "The user that has the note you want to remove", true)
                    .addOption(OptionType.INTEGER, "id", "The id of the note you want to remove", true)
                    .addOption(OptionType.STRING, "content", "The new content of the reminder"))
            .addSubcommands(new SubcommandData("list", "Lists a user's notes notes")
                    .addOption(OptionType.USER, "user", "The user who's notes you want to find", true));

    @Command(name = "note.add", description = "Adds a new note", permission = "command.utility.note.add")
    public static void addNote(SlashCommandEvent event) {

        User user = event.getOption("user").getAsUser();
        String content = event.getOption("content").getAsString();

        if (content.length() > 1024) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("The content of the note can only be a maximum of 1024 characters long")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();
            return;
        }

        Note note = new Note(null, event.getGuild().getId())
                .setGuildId(event.getGuild().getId())
                .setAuthorId(event.getUser().getId())
                .setTargetId(user.getId())
                .setContent(content)
                .setTimeCreated(System.currentTimeMillis())
                .save();

        event.replyEmbeds(new EmbedBuilder()
                .setDescription("This note has been added to " + user.getAsMention())
                .setColor(EmbedColour.NEUTRAL.getColour())
                .build()).queue();
    }

    @Command(name = "note.remove", description = "Removes a specific note", permission = "command.utility.note.remove")
    public static void removeNote(SlashCommandEvent event) {

        String id = event.getOption("id").getAsString();
        User user = event.getOption("user").getAsUser();

        Note note = new Note(id, event.getGuild().getId());

        ArrayList<Note> notes = NoteUtil.getUsersNotes(event.getGuild().getId(), user.getId());

        if (notes.stream().anyMatch(w -> Objects.equals(w.getId(), note.getId()))) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("Note (`" + event.getOption("id").getAsString() + "`) for " + user.getAsMention() + " has been deleted!")
                    .setColor(EmbedColour.YES.getColour())
                    .build()).queue();
            NoteUtil.removeNoteById(id);

        } else {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("A note with the id of `" + event.getOption("id").getAsString() + "` for " + user.getAsMention() + " could not be found!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();
        }
    }

    @Command(name = "note.edit", description = "Edits a specific note", permission = "command.utility.note.edit")
    public static void editNote(SlashCommandEvent event) {

        try {
            String id = event.getOption("id").getAsString();
            User user = event.getOption("user").getAsUser();

            if (event.getOption("content").getAsString().length() > 1024) {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("The content of the note can only be a maximum of 1024 characters long")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).queue();
                return;
            }
            Note note = new Note(id, event.getGuild().getId());

            ArrayList<Note> notes = NoteUtil.getUsersNotes(event.getGuild().getId(), user.getId());

            if (notes.stream().anyMatch(w -> Objects.equals(w.getId(), note.getId()))) {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("Note (`" + event.getOption("id").getAsString() + "`) for " + user.getAsMention() + " has been edited!")
                        .setColor(EmbedColour.YES.getColour())
                        .build()).queue();
                note.setContent(event.getOption("content").getAsString()).save();

            } else {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("A note with the id of `" + event.getOption("id").getAsString() + "` for " + user.getAsMention() + " could not be found!")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).queue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Command(name = "note.list", description = "Adds a new note", permission = "command.utility.note.list")
    public static void listNotes(SlashCommandEvent event) {

        User user = event.getOption("user").getAsUser();
        ArrayList<Note> notes = NoteUtil.getUsersNotes(event.getGuild().getId(), user.getId());
        int count = notes.size();
        int noteCount = 0;
        int pageNumber = 0;

        for (int i = 0; i < count; i += 25) {

            pageNumber++;

            EmbedBuilder builder = new EmbedBuilder()
                    .setColor(EmbedColour.NEUTRAL.getColour())
                    .setFooter("Page Number " + pageNumber);

            for (int j = 0; j < 25; j++) {
                if (noteCount >= count) break;

                builder.addField("`" + notes.get(noteCount).getId() + "` added by "
                                + event.getJDA().getUserById(notes.get(noteCount).getAuthorId()).getAsTag()
                                + " at " + TimeFormat.DATE_TIME_LONG.format(notes.get(noteCount).getTimeCreated()),
                        notes.get(noteCount).getContent(), false);

                noteCount++;
            }

            if (pageNumber == 1)
                event.replyEmbeds(builder.setTitle(user.getAsTag() + "'s notes")
                        .build()).queue();
            else
                event.getChannel().sendMessageEmbeds(builder.build()).queue();
        }

        if (pageNumber == 0)
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription(user.getAsMention() + " has no notes!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();
    }

}
