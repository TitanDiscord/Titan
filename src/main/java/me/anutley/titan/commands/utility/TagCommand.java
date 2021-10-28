package me.anutley.titan.commands.utility;

import me.anutley.titan.commands.Command;
import me.anutley.titan.database.ActionLogger;
import me.anutley.titan.database.objects.EmbedTag;
import me.anutley.titan.database.objects.TextTag;
import me.anutley.titan.database.util.TagUtil;
import me.anutley.titan.util.PermissionUtil;
import me.anutley.titan.util.RegexUtil;
import me.anutley.titan.util.embeds.errors.NoTagEmbed;
import me.anutley.titan.util.enums.EmbedColour;
import me.anutley.titan.util.exceptions.NoTagFoundException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.ArrayList;

public class TagCommand extends ListenerAdapter {

    public static CommandData TagCommandData = new CommandData("tag", "Allows the managing / using of tags")
            .addSubcommandGroups(new SubcommandGroupData("embed", "Managing of embedded tags")

                    .addSubcommands(new SubcommandData("create", "Creates an embedded tag")
                            .addOption(OptionType.STRING, "trigger", "The trigger of the tag", true)
                            .addOption(OptionType.STRING, "title", "The title of the tag", true)
                            .addOption(OptionType.STRING, "description", "The description of the tag")
                            .addOption(OptionType.STRING, "colour", "The hex colour of the tag (EG: #ff00ff)")
                            .addOption(OptionType.STRING, "thumbnail", "The url of the thumbnail image of the tag"))

                    .addSubcommands(new SubcommandData("edit", "Edits an embedded tag")
                            .addOption(OptionType.STRING, "trigger", "The trigger of the tag", true)
                            .addOption(OptionType.STRING, "title", "The title of the tag")
                            .addOption(OptionType.STRING, "description", "The description of the tag")
                            .addOption(OptionType.STRING, "colour", "The hex colour of the tag (EG: #ff00ff)")
                            .addOption(OptionType.STRING, "thumbnail", "The url of the thumbnail image of the tag")))

            .addSubcommandGroups(new SubcommandGroupData("text", "Managing of text tags")

                    .addSubcommands(new SubcommandData("create", "Creates a text tag")
                            .addOption(OptionType.STRING, "trigger", "The trigger of the tag", true)
                            .addOption(OptionType.STRING, "content", "The content of the tag", true))

                    .addSubcommands(new SubcommandData("edit", "Edits a text tag")
                            .addOption(OptionType.STRING, "trigger", "The trigger of the tag", true)
                            .addOption(OptionType.STRING, "content", "The content of the tag", true)))

            .addSubcommands(new SubcommandData("get", "Gets a tag")
                    .addOption(OptionType.STRING, "trigger", "The trigger of the tag you want to get"))

            .addSubcommands(new SubcommandData("delete", "Deletes a tag")
                    .addOption(OptionType.STRING, "trigger", "The trigger of the tag you want to delete"))

            .addSubcommands(new SubcommandData("clear", "Clears ALL of this guild's tags. This action is irreversible"))

            .addSubcommands(new SubcommandData("list", "Lists all of the guild's tags"));


    @Command(name = "tag.embed.create", description = "Creates an embedded tag", permission = "command.utility.tag.embed.create")
    public static void createEmbedTag(SlashCommandEvent event) {

        String trigger = event.getOption("trigger").getAsString();
        String guildId = event.getGuild().getId();

        if (TagUtil.doesTagExist(trigger, guildId)) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("This tag already exists!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
        } else {
            String title = event.getOption("title") != null ? event.getOption("title").getAsString() : null;
            String description = event.getOption("description") != null ? event.getOption("description").getAsString() : null;
            String colour = event.getOption("colour") != null ? event.getOption("colour").getAsString() : null;
            String thumbnail = event.getOption("thumbnail") != null ? event.getOption("thumbnail").getAsString() : null;

            if (!checkArgs(event, title, description, colour, thumbnail)) return;

            EmbedTag tag = new EmbedTag(trigger, guildId)
                    .setTitle(title)
                    .setDescription(description)
                    .setColour(colour)
                    .setThumbnail(thumbnail)
                    .save();

            event.replyEmbeds(TagUtil.getTagEmbedBuilder(tag).build()).queue();

            new ActionLogger(event.getGuild())
                    .addAction("Embed Tag Created")
                    .addModerator(event.getUser())
                    .addExtraInfo("Tag Trigger:", tag.getTrigger()).log();
        }
    }

    @Command(name = "tag.embed.edit", description = "Edits an embedded tag", permission = "command.utility.tag.embed.edit")
    public static void editEmbedTag(SlashCommandEvent event) {
        String trigger = event.getOption("trigger").getAsString();
        String guildId = event.getGuild().getId();

        try {
            if (!TagUtil.isEmbedTag(trigger, guildId)) {
                event.replyEmbeds(notCorrectTagTypeEmbed("embed", "text").build()).setEphemeral(true).queue();
                return;
            }

            EmbedTag tag = new EmbedTag(trigger, guildId);

            String title = event.getOption("title") != null ? event.getOption("title").getAsString() : null;
            String description = event.getOption("description") != null ? event.getOption("description").getAsString() : null;
            String colour = event.getOption("colour") != null ? event.getOption("colour").getAsString() : null;
            String thumbnail = event.getOption("thumbnail") != null ? event.getOption("thumbnail").getAsString() : null;

            if (title != null)
                if (!title.equals(tag.getTitle())) tag.setTitle(title);
            if (description != null)
                if (!description.equals(tag.getDescription())) tag.setDescription(description);
            if (colour != null)
                if (!colour.equals(tag.getColour())) tag.setColour(colour);
            if (thumbnail != null)
                if (!thumbnail.equals(tag.getThumbnail())) tag.setThumbnail(thumbnail);

            tag.save();

            event.replyEmbeds(TagUtil.getTagEmbedBuilder(tag).build()).queue();

        } catch (NoTagFoundException e) {
            event.replyEmbeds(NoTagEmbed.Embed().build()).queue();
            return;
        }

        new ActionLogger(event.getGuild())
                .addAction("Embed Tag Edited")
                .addModerator(event.getUser())
                .addExtraInfo("Tag Trigger:", trigger).log();
    }

    @Command(name = "tag.text.create", description = "Creates a text tag", permission = "command.utility.tag.text.create")
    public static void createTextTag(SlashCommandEvent event) {
        String trigger = event.getOption("trigger").getAsString();
        String guildId = event.getGuild().getId();

        if (TagUtil.doesTagExist(trigger, guildId)) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("This tag already exists!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
        } else {

            EmbedBuilder builder = checkLength(event.getOption("content").getAsString(), 2000, "content");

            if (builder != null) {
                event.replyEmbeds(builder.build()).queue();
                return;
            }

            TextTag tag = new TextTag(trigger, guildId)
                    .setContent(event.getOption("content").getAsString())
                    .save();

            event.reply(tag.getContent()).queue();

            new ActionLogger(event.getGuild())
                    .addAction("Text Tag Created")
                    .addModerator(event.getUser())
                    .addExtraInfo("Tag Trigger:", tag.getTrigger()).log();
        }
    }

    @Command(name = "tag.text.edit", description = "Edits a text tag", permission = "command.utility.tag.text.edit")
    public static void editTextTag(SlashCommandEvent event) {
        String trigger = event.getOption("trigger").getAsString();
        String guildId = event.getGuild().getId();

        try {
            if (TagUtil.isEmbedTag(trigger, guildId)) {
                event.replyEmbeds(notCorrectTagTypeEmbed("text", "embed").build()).setEphemeral(true).queue();
                return;
            }

            EmbedBuilder builder = checkLength(event.getOption("content").getAsString(), 2000, "content");

            if (builder != null) {
                event.replyEmbeds(builder.build()).queue();
                return;
            }

            TextTag tag = new TextTag(trigger, guildId)
                    .setContent(event.getOption("content").getAsString())
                    .save();

            event.reply(tag.getContent()).queue();

        } catch (NoTagFoundException e) {
            event.replyEmbeds(NoTagEmbed.Embed().build()).queue();
            return;
        }

        new ActionLogger(event.getGuild())
                .addAction("Text Tag Edited")
                .addModerator(event.getUser())
                .addExtraInfo("Tag Trigger:", trigger).log();
    }

    @Command(name = "tag.get", description = "Gets a tag", permission = "command.utility.tag.get")
    public static void getTag(SlashCommandEvent event) {
        String trigger = event.getOption("trigger").getAsString();
        String guildId = event.getGuild().getId();

        try {
            if (TagUtil.isEmbedTag(trigger, guildId)) {
                event.replyEmbeds(TagUtil.getTagEmbedBuilder(new EmbedTag(trigger, guildId)).build()).queue();
            } else {
                event.reply(new TextTag(trigger, guildId).getContent()).queue();
            }
        } catch (NoTagFoundException e) {
            event.replyEmbeds(NoTagEmbed.Embed().build()).setEphemeral(true).queue();
        }
    }

    @Command(name = "tag.delete", description = "Deletes a tag", permission = "command.utility.tag.delete")
    public static void deleteTag(SlashCommandEvent event) {
        String trigger = event.getOption("trigger").getAsString();
        String guildId = event.getGuild().getId();

        if (!TagUtil.doesTagExist(trigger, guildId)) {
            event.replyEmbeds(NoTagEmbed.Embed().build()).queue();
            return;
        }

        TagUtil.deleteTagByTrigger(trigger, guildId);
        event.replyEmbeds(new EmbedBuilder()
                .setDescription("`" + trigger + "` has been deleted!")
                .setColor(EmbedColour.YES.getColour())
                .build()).queue();

        new ActionLogger(event.getGuild())
                .addAction("Tag Deleted")
                .addModerator(event.getUser())
                .addExtraInfo("Tag Trigger:", trigger).log();
    }

    @Command(name = "tag.clear", description = "Clears ALL of this guild's tags. This action is irreversible", permission = "command.utility.tag.clear")
    public static void clearTags(SlashCommandEvent event) {
        event.replyEmbeds(new EmbedBuilder().setDescription("Are you sure you want to clear all tags for this guild!")
                        .setColor(EmbedColour.NEUTRAL.getColour()).build())
                .addActionRow(
                        Button.success("clear_all_tags=true", "Yes"),
                        Button.danger("clear_all_tags=false", "No"))
                .queue();
    }


    @Override
    public void onButtonClick(ButtonClickEvent event) {

        if (!PermissionUtil.hasPermission("command.utility.tag.clear", event.getMember())) return;

        if (event.getComponentId().equals("clear_all_tags=true")) {
            event.getMessage().editMessageEmbeds(new EmbedBuilder()
                    .setDescription("This guild's tags have been cleared!")
                    .setColor(EmbedColour.YES.getColour())
                    .build()).override(true).queue();
            TagUtil.deleteGuildsTags(event.getGuild().getId());

            new ActionLogger(event.getGuild())
                    .addAction("Guilds' Tags Cleared")
                    .addModerator(event.getUser()).log();

        } else {
            event.getMessage().editMessageEmbeds(new EmbedBuilder().setDescription("This guild's tags have not been cleared!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).override(true).queue();
        }
    }

    @Command(name = "tag.list", description = "Lists all of the guild's tags", permission = "command.utility.tag.list")
    public static void listTags(SlashCommandEvent event) {
        ArrayList<EmbedTag> embedTags = TagUtil.getGuildsEmbedTags(event.getGuild().getId());
        ArrayList<TextTag> textTags = TagUtil.getGuildsTextTags(event.getGuild().getId());

        int totalTags = embedTags.size() + textTags.size();
        int embedTagCount = 0;
        int textTagCount = 0;
        int totalTagCountAdded = 0;

        StringBuilder tags = new StringBuilder();

        if (totalTags != 0) {
            tags.append("`Embed Tags:` ");
            while (embedTagCount < embedTags.size()) {
                embedTagCount++;
                tags.append(embedTags.size() != embedTagCount ?
                        embedTags.get(totalTagCountAdded).getTrigger() + ", " :
                        embedTags.get(totalTagCountAdded).getTrigger());
                totalTagCountAdded++;
            }

            tags.append(" \n`Text Tags:` ");

            while (textTagCount < textTags.size()) {
                tags.append(totalTags != totalTagCountAdded + 1 ?
                        textTags.get(textTagCount).getTrigger() + ", " :
                        textTags.get(textTagCount).getTrigger());
                totalTagCountAdded++;
                textTagCount++;
            }

            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("Available Tags")
                    .setColor(EmbedColour.NEUTRAL.getColour())
                    .setDescription(tags.toString())
                    .build()).queue();

        } else {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("The guild has no tags!")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).queue();
        }
    }


    public static EmbedBuilder checkLength(String content, int length, String option) {
        if (content.length() > length) {
            return new EmbedBuilder()
                    .setDescription("The " + option + " was too long. It can only be " + length + " characters!")
                    .setColor(EmbedColour.NO.getColour());
        }
        return null;
    }

    public static boolean checkArgs(SlashCommandEvent event, String title, String description, String colour, String thumbnail) {
        if (title != null) {
            EmbedBuilder titleLengthEmbed = checkLength(title, 256, "title");
            if (titleLengthEmbed != null) {
                event.replyEmbeds(titleLengthEmbed.build()).setEphemeral(true).queue();
                return false;
            }
        }

        if (description != null) {
            EmbedBuilder titleLengthEmbed = checkLength(description, 4096, "description");
            if (titleLengthEmbed != null) {
                event.replyEmbeds(titleLengthEmbed.build()).setEphemeral(true).queue();
                return false;
            }
        }

        if (colour != null) {
            if (!RegexUtil.validHexColour(colour)) {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("`" + colour + "` is not a valid hex colour!")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).setEphemeral(true).queue();
                return false;
            }
        }

        if (thumbnail != null) {
            if (!thumbnail.startsWith("https") && !thumbnail.startsWith("http")) {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("That is not a valid url, as it does not start with http or https!")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).setEphemeral(true).queue();
                return false;
            }
        }
        return true;
    }

    public static EmbedBuilder notCorrectTagTypeEmbed(String currentType, String correctType) {
        return new EmbedBuilder()
                .setDescription("This tag is a " + correctType + " tag, not an " + currentType + " tag!")
                .setColor(EmbedColour.NO.getColour());
    }

}