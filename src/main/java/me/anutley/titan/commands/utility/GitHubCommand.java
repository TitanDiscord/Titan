package me.anutley.titan.commands.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.anutley.titan.Config;
import me.anutley.titan.commands.Command;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.TimeFormat;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

public class GitHubCommand {

    private static final OptionData RepoOptionData = new OptionData(OptionType.STRING, "repo", "The owner and name of the repo seperated by a slash (EG ANutley/Titan)", true);

    public static CommandData GitHubCommandData = new CommandData("github", "Gets information about a GitHub user/repo")
            .addSubcommands(new SubcommandData("repository", "Gets information about a GitHub repo")
                    .addOptions(RepoOptionData))

            .addSubcommands(new SubcommandData("commit", "Get information about a specific commit")
                    .addOptions(RepoOptionData)
                    .addOption(OptionType.STRING, "hash", "The hash of the commit you want to find information about", true))

            .addSubcommands(new SubcommandData("issue", "Gets information about a specific issue/pull request")
                    .addOptions(RepoOptionData)
                    .addOption(OptionType.STRING, "issue", "The number of the issue/pull request you want to find information on"))

            .addSubcommands(new SubcommandData("user", "Gets information about a GitHub user")
                    .addOption(OptionType.STRING, "user", "The name of the user", true))

            .addSubcommands(new SubcommandData("organisation", "Gets information about a GitHub organisation")
                    .addOption(OptionType.STRING, "org", "The name of the organisation", true));

    @Command(name = "github.repository", description = "Gets information about a GitHub repo", permission = "command.utility.github.repository")
    public static void githubRepoInfoCommand(SlashCommandEvent event) {
        try {
            JsonNode repo = requestGitHub("https://api.github.com/repos/" + event.getOption("repo").getAsString());

            MessageEmbed doesRepoExist = doesRepoExist(repo);
            if (doesRepoExist != null) {
                event.replyEmbeds(doesRepoExist).setEphemeral(true).queue();
                return;
            }

            String latestTag = !requestGitHub("https://api.github.com/repos/" + event.getOption("repo").getAsString() + "/tags").isEmpty() ?
                    requestGitHub("https://api.github.com/repos/" + event.getOption("repo").getAsString() + "/tags").get(0).get("name").asText() : null;


            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle(repo.get("full_name").asText(), repo.get("html_url").asText())
                    .setThumbnail(repo.get("owner").get("avatar_url").asText())
                    .setColor(EmbedColour.NEUTRAL.getColour())
                    .addField("Forks", repo.get("forks_count").asText(), true)
                    .addField("Stars", repo.get("stargazers_count").asText(), true)
                    .addField("Watchers", repo.get("subscribers_count").asText(), true)
                    .addField("Open Issues", repo.get("open_issues_count").asText(), true)
                    .addField("Created At", TimeFormat.DATE_TIME_LONG.format(OffsetDateTime.parse(repo.get("created_at").asText()).toEpochSecond() * 1000), true);


            if (!repo.get("description").asText().equals("null"))
                builder.setDescription(repo.get("description").asText());
            if (latestTag != null) builder.addField("Latest Tag", latestTag, true);
            if (!repo.get("language").asText().equals("null"))
                builder.addField("Main Language", repo.get("language").asText(), true);
            if (!repo.get("license").asText().equals("null"))
                builder.addField("License", repo.get("license").get("name").asText(), true);


            event.replyEmbeds(builder.build()).queue();

        } catch (IOException e) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("Please make sure you are entering a valid URL")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
        }
    }

    @Command(name = "github.commit", description = "Get information about a specific commit", permission = "command.github.commit")
    public static void githubRepoCommitCommand(SlashCommandEvent event) {
        try {
            String repoName = event.getOption("repo").getAsString();
            String commitHash = event.getOption("hash").getAsString();

            JsonNode repo = requestGitHub("https://api.github.com/repos/" + repoName);

            MessageEmbed doesRepoExist = doesRepoExist(repo);
            if (doesRepoExist != null) {
                event.replyEmbeds(doesRepoExist).setEphemeral(true).queue();
                return;
            }

            JsonNode hash = requestGitHub("https://api.github.com/repos/" + repoName + "/commits/" + commitHash);

            if (hash.get("message") != null) {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription(hash.get("message").asText())
                        .setColor(EmbedColour.NO.getColour())
                        .build()).setEphemeral(true).queue();
                return;
            }

            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle(repo.get("full_name").asText(), hash.get("html_url").asText())
                    .setThumbnail(repo.get("owner").get("avatar_url").asText())
                    .setColor(EmbedColour.NEUTRAL.getColour())
                    .addField("Hash", commitHash, true)
                    .addField("Author", hash.get("author").get("login").asText(), true)
                    .addField("Message", StringUtils.truncate(hash.get("commit").get("message").asText(), 1024), true)
                    .addField("Additions", hash.get("stats").get("additions").asText(), true)
                    .addField("Deletions", hash.get("stats").get("deletions").asText(), true);

            event.replyEmbeds(builder.build()).queue();

        } catch (IOException e) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("Please make sure you are entering a valid URL")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
        }

    }

    @Command(name = "github.issue", description = "Gets information about a specific issue/pull request", permission = "command.github.issue")
    public static void githubRepoIssueCommand(SlashCommandEvent event) {
        try {
            String repoName = event.getOption("repo").getAsString();
            String issueNumber = event.getOption("issue").getAsString();

            JsonNode repo = requestGitHub("https://api.github.com/repos/" + repoName);

            MessageEmbed doesRepoExist = doesRepoExist(repo);
            if (doesRepoExist != null) {
                event.replyEmbeds(doesRepoExist).setEphemeral(true).queue();
                return;
            }

            JsonNode issue = requestGitHub("https://api.github.com/repos/" + repoName + "/issues/" + issueNumber);

            if (issue.get("message") != null) {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("An issue with the number `" + issueNumber + "` could not be found!")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).setEphemeral(true).queue();
                return;
            }

            String issueType = issue.get("pull_request") == null ? "Issue" : "Pull Request";
            String labels = issue.get("labels").findValues("name").stream().map(JsonNode::asText).collect(Collectors.joining(", "));

            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle(repo.get("full_name").asText(), issue.get("html_url").asText())
                    .setThumbnail(repo.get("owner").get("avatar_url").asText())
                    .addField("Number", issueNumber, true)
                    .addField("Type", issueType, true)
                    .addField("Title", issue.get("title").asText(), true)
                    .addField("Author", issue.get("user").get("login").asText(), true);

            if (!issue.get("body").asText().equals("null"))
                builder.addField("Body", StringUtils.truncate(issue.get("body").asText(), 1024), false);

            builder.addField("Created", TimeFormat.DATE_TIME_LONG.format(OffsetDateTime.parse(issue.get("created_at").asText()).toEpochSecond() * 1000), true);

            if (!StringUtils.isEmpty(labels))
                builder.addField("Labels", labels, true);

            if (issueType.equals("Pull Request")) {
                JsonNode pr = requestGitHub("https://api.github.com/repos/" + repoName + "/pulls/" + issueNumber);

                String state = "";

                if (pr.get("merged").asBoolean()) {
                    state = "Merged";
                    builder.setColor(0x8957e5);
                } else if (pr.get("draft").asBoolean()) {
                    state = "Draft";
                    builder.setColor(0x6e7681);
                } else if (pr.get("state").asText().equals("open")) {
                    state = "Open";
                    builder.setColor(0x238636);
                } else if (pr.get("state").asText().equals("closed")) {
                    state = "Closed";
                    builder.setColor(0x238636);
                }

                builder.addField("State", state, true);
                builder.addField("Head -> Base", "`" + pr.get("head").get("label").asText() + "` -> `" + pr.get("base").get("label").asText() + "`", true);

            } else {
                if (issue.get("state").asText().equals("closed"))
                    builder.setColor(0x8957e5).addField("State", "Closed", true);
                else
                    builder.setColor(0x238636).addField("State", "Open", true);

                if (!issue.get("closed_by").asText().equals("null"))
                    builder.addField("Closed By", issue.get("closed_by").get("login").asText(), true);
            }

            event.replyEmbeds(builder.build()).queue();

        } catch (IOException e) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("Please make sure you are entering a valid URL")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
        }
    }

    @Command(name = "github.user", description = "Gets information about a GitHub user", permission = "command.utility.github.user")
    public static void userGithubCommand(SlashCommandEvent event) {
        try {
            JsonNode user = requestGitHub("https://api.github.com/users/" + event.getOption("user").getAsString());

            if (user.get("message") != null) {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("This user does not exist!")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).setEphemeral(true).queue();
                return;
            }

            if (!user.get("type").asText().equals("User")) {
                event.replyEmbeds(new EmbedBuilder()
                        .setTitle(user.get("login").asText() + "is not a user")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).queue();
            }

            int starred = requestGitHub("https://api.github.com/users/" + event.getOption("user").getAsString() + "/starred").size();


            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle(user.get("login").asText(), user.get("html_url").asText())
                    .setThumbnail(user.get("avatar_url").asText())
                    .setColor(EmbedColour.NEUTRAL.getColour())
                    .addField("Public Repositories", user.get("public_repos").asText(), true)
                    .addField("Public Gists", user.get("public_gists").asText(), true)
                    .addField("Followers", user.get("followers").asText(), true)
                    .addField("Following", user.get("following").asText(), true)
                    .addField("Number of starred repositories", String.valueOf(starred), true)
                    .addField("Created At", TimeFormat.DATE_TIME_LONG.format(OffsetDateTime.parse(user.get("created_at").asText()).toEpochSecond() * 1000), true);;

            event.replyEmbeds(builder.build()).queue();

        } catch (IOException e) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("Please make sure you are entering a valid URL")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
        }
    }


    @Command(name = "github.organisation", description = "Gets information about a GitHub organisation", permission = "command.utility.github.organisation")
    public static void organisationGithubCommand(SlashCommandEvent event) {
        try {
            JsonNode repo = requestGitHub("https://api.github.com/orgs/" + event.getOption("org").getAsString());

            if (repo.get("message") != null) {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("This organisation does not exist!")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).setEphemeral(true).queue();
                return;
            }

            JsonNode mostPopularRepo = null;
            long stars = 0;
            JsonNode orgRepos = requestGitHub("https://api.github.com/orgs/" + event.getOption("org").getAsString() + "/repos");

            for (int i = 0; i < orgRepos.size(); i++) {
                if (orgRepos.get(i).get("stargazers_count").asLong() > stars) {
                    stars = orgRepos.get(i).get("stargazers_count").asLong();
                    mostPopularRepo = orgRepos.get(i);
                }
            }
            JsonNode member = requestGitHub("https://api.github.com/orgs/" + event.getOption("org").getAsString() + "/public_members");

            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle(repo.get("login").asText(), repo.get("html_url").asText())
                    .setThumbnail(repo.get("avatar_url").asText())
                    .setColor(EmbedColour.NEUTRAL.getColour())
                    .addField("Public Repositories", repo.get("public_repos").asText(), true)
                    .addField("Members", String.valueOf(member.size()), true);

            if (!repo.get("description").asText().equals("null"))
                builder.setDescription(repo.get("description").asText());
            if (mostPopularRepo != null)
                builder.addField("Most Popular Repository", "[" + mostPopularRepo.get("name").asText() + "](" + mostPopularRepo.get("html_url").asText() + ")", true);

            event.replyEmbeds(builder.build()).queue();

        } catch (IOException e) {
            event.replyEmbeds(new EmbedBuilder()
                    .setDescription("Please make sure you are entering a valid URL")
                    .setColor(EmbedColour.NO.getColour())
                    .build()).setEphemeral(true).queue();
        }
    }

    public static JsonNode requestGitHub(String url) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder().build();
        ObjectMapper mapper = new ObjectMapper();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "token " + Config.getInstance().get("GITHUB_TOKEN"))
                .build();
        return mapper.readTree(Objects.requireNonNull(client.newCall(request).execute().body()).string());
    }

    public static MessageEmbed doesRepoExist(JsonNode repo) {
        if (repo.get("message") != null) {
            return new EmbedBuilder()
                    .setDescription("This repository does not exist!")
                    .setColor(EmbedColour.NO.getColour())
                    .build();
        }
        return null;
    }

}
