package me.anutley.titan.commands.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.anutley.titan.Config;
import me.anutley.titan.commands.Command;
import me.anutley.titan.util.enums.EmbedColour;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.util.Objects;

public class GitHubCommand  {
    public static CommandData GitHubCommandData = new CommandData("github", "Gets information about a GitHub user/repo")
            .addSubcommands(new SubcommandData("repository", "Gets information about a GitHub repo")
                    .addOption(OptionType.STRING, "repo", "The owner and name of the repo seperated by a slash (EG ANutley/Titan)", true))
            .addSubcommands(new SubcommandData("user", "Gets information about a GitHub user")
                    .addOption(OptionType.STRING, "user", "The name of the user", true))
            .addSubcommands(new SubcommandData("organisation", "Gets information about a GitHub organisation")
                    .addOption(OptionType.STRING, "org", "The name of the organisation", true));

    @Command(name = "github.repository", description = "Gets information about a GitHub repo", permission = "command.utility.github.repo")
    public static void githubRepoCommand(SlashCommandEvent event) {
        try {
            JsonNode repo = requestGitHub("https://api.github.com/repos/" + event.getOption("repo").getAsString());

            if (repo.get("message") != null) {
                event.replyEmbeds(new EmbedBuilder()
                        .setDescription("This repository does not exist!")
                        .setColor(EmbedColour.NO.getColour())
                        .build()).setEphemeral(true).queue();
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
                    .addField("Open Issues", repo.get("open_issues_count").asText(), true);


            if (!repo.get("description").asText().equals("null"))
                builder.setDescription(repo.get("description").asText());
            if (latestTag != null) builder.addField("Latest Tag", latestTag, true);
            if (!repo.get("language").asText().equals("null"))
                builder.addField("Main Language", repo.get("language").asText(), true);
            if (!repo.get("license").asText().equals("null"))
                builder.addField("License", repo.get("license").get("name").asText(), true);


            event.replyEmbeds(builder.build()).queue();

        } catch (IOException e) {
            e.printStackTrace();
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
                    .addField("Number of starred repositories", String.valueOf(starred), true);

            event.replyEmbeds(builder.build()).queue();

        } catch (IOException e) {
            e.printStackTrace();
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
            JsonNode member = requestGitHub("https://api.github.com/orgs/" + event.getOption("org").getAsString() + "/members");

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
            e.printStackTrace();
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

}
