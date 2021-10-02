package me.anutley.titan;

import me.anutley.titan.commands.Command;
import me.anutley.titan.commands.dev.DevBaseCommand;
import me.anutley.titan.commands.fun.AvatarCommand;
import me.anutley.titan.commands.fun.CoinCommand;
import me.anutley.titan.commands.fun.DiceCommand;
import me.anutley.titan.commands.moderation.*;
import me.anutley.titan.commands.settings.SettingsBaseCommand;
import me.anutley.titan.commands.utility.*;
import me.anutley.titan.database.SQLiteDataSource;
import me.anutley.titan.listeners.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.ArrayList;

public class Titan {

    public static ArrayList<Command> commands = new ArrayList<>();
    public static JDA jda;

    public static void main(String[] arguments) throws LoginException, InterruptedException, IOException {

        Config config = new Config();
        SQLiteDataSource sqLiteDataSource = new SQLiteDataSource();

        jda = JDABuilder.createDefault(config.get("DISCORD_TOKEN"))
                .setEnabledIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS)
                .disableCache(CacheFlag.VOICE_STATE)
                .setMemberCachePolicy(MemberCachePolicy.ALL)

                // Commands
                .addEventListeners(

                        //Dev Command
                        registerCommand(new DevBaseCommand()),

                        //Fun Commands
                        registerCommand(new AvatarCommand()),
                        registerCommand(new CoinCommand()),
                        registerCommand(new DiceCommand()),

                        //Settings Commands
                        registerCommand(new SettingsBaseCommand()),

                        //Moderation Commands
                        registerCommand(new BanCommand()),
                        registerCommand(new KickCommand()),
                        registerCommand(new LockdownCommand()),
                        registerCommand(new SetNickCommand()),
                        registerCommand(new SlowmodeCommand()),
                        registerCommand(new WarnCommand()),

                        //Utility Commands
                        registerCommand(new AnnounceCommand()),
                        registerCommand(new GitHubCommand()),
                        registerCommand(new GuildInfoCommand()),
                        registerCommand(new HelpCommand()),
                        registerCommand(new InviteCommand()),
                        registerCommand(new PingCommand()),
                        registerCommand(new StatsCommand()),
                        registerCommand(new TagCommand()),
                        registerCommand(new UserInfoCommand()),
                        registerCommand(new WikiCommand())
                )

                // Events
                .addEventListeners(
                        new IllegalPingListener(),
                        new JoinLeaveListener(),
                        new LockdownListener(),
                        new ReadyListener(),
                        new MentionListener(),
                        new TagListener()

                ).build();

        jda.awaitReady();


        jda
                .updateCommands()
                .addCommands(
                        // Dev Command
                        DevBaseCommand.DevBaseCommandData,

                        // Fun Commands
                        AvatarCommand.AvatarCommandData,
                        CoinCommand.CoinCommandData,
                        DiceCommand.DiceCommandData,

                        // Moderation Commands
                        BanCommand.BanCommandData,
                        KickCommand.KickCommandData,
                        LockdownCommand.LockdownCommandData,
                        SetNickCommand.SetNickCommandData,
                        SlowmodeCommand.SlowmodeCommandData,
                        WarnCommand.WarnCommandData,

                        //Settings Command
                        SettingsBaseCommand.SettingsCommandData,

                        // Utility Commands
                        AnnounceCommand.AnnounceCommandData,
                        GitHubCommand.GitHubCommandData,
                        GuildInfoCommand.GuildInfoCommandData,
                        HelpCommand.HelpCommandData,
                        InviteCommand.InviteCommandData,
                        PingCommand.PingCommandData,
                        StatsCommand.StatsCommandData,
                        TagCommand.TagCommandData,
                        UserInfoCommand.UserInfoCommandData,
                        WikiCommand.WikiCommandData
                )
                .queue();
    }

    public static Command registerCommand(Command command) {
        commands.add(command);
        return command;
    }

    public static ArrayList<Command> getRegisteredCommands() {
        return commands;
    }

    public static JDA getJda() {
        return jda;
    }

}

