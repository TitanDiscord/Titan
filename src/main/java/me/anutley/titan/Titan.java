package me.anutley.titan;

import me.anutley.titan.commands.dev.EvalCommand;
import me.anutley.titan.commands.dev.RestartCommand;
import me.anutley.titan.commands.dev.ShutdownCommand;
import me.anutley.titan.commands.dev.UpdateCommand;
import me.anutley.titan.commands.fun.AvatarCommand;
import me.anutley.titan.commands.fun.CoinCommand;
import me.anutley.titan.commands.fun.DiceCommand;
import me.anutley.titan.commands.moderation.*;
import me.anutley.titan.commands.settings.SettingsBaseCommand;
import me.anutley.titan.commands.utility.*;
import me.anutley.titan.database.ReminderInitialiser;
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
import java.util.Arrays;

public class Titan {

    public static ArrayList<Class<?>> commands = new ArrayList<>();
    public static JDA jda;
    public static long startupTime;

    public static void main(String[] arguments) throws LoginException, InterruptedException, IOException {

        startupTime = System.currentTimeMillis();

        Config config = new Config();
        new SQLiteDataSource();

        registerCommands(
                //Fun Commands
                AvatarCommand.class,
                CoinCommand.class,
                DiceCommand.class,

                //Moderation Commands
                BanCommand.class,
                KickCommand.class,
                LockdownCommand.class,
                SetNickCommand.class,
                SlowmodeCommand.class,
                WarnCommand.class,

                //Utility Commands
                AnnounceCommand.class,
                GitHubCommand.class,
                HelpCommand.class,
                InfoCommand.class,
                InviteCommand.class,
                PingCommand.class,
                RemindCommand.class,
                RolePermissionsCommand.class,
                TagCommand.class,
                WikiCommand.class
        );

        new SettingsBaseCommand().loadSuperclasses();

        jda = JDABuilder.createDefault(config.get("DISCORD_TOKEN"))
                .setEnabledIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS)
                .disableCache(CacheFlag.VOICE_STATE)
                .setMemberCachePolicy(MemberCachePolicy.ALL)

                // Commands
                .addEventListeners(

                        //Dev Commands
                        new EvalCommand(),
                        new RestartCommand(),
                        new ShutdownCommand(),
                        new UpdateCommand(),

                        //Events
                        new CommandListener(),
                        new IllegalPingListener(),
                        new JoinLeaveListener(),
                        new LockdownListener(),
                        new MentionListener(),
                        new QuoteListener(),
                        new ReadyListener(),
                        new TagListener()
                ).build();


        jda.awaitReady();


        jda
                .updateCommands()
                .addCommands(

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
                        HelpCommand.HelpCommandData,
                        InfoCommand.InfoCommandData,
                        InviteCommand.InviteCommandData,
                        PingCommand.PingCommandData,
                        RemindCommand.RemindCommandData,
                        RolePermissionsCommand.RolePermissionsSettingsCommandData,
                        TagCommand.TagCommandData,
                        WikiCommand.WikiCommandData
                )
                .queue();
        ReminderInitialiser.run();
    }

    public static ArrayList<Class<?>> registerCommands(Class<?>... command) {
        commands.addAll(Arrays.asList(command));
        return commands;
    }

    public static ArrayList<Class<?>> getRegisteredCommands() {
        return commands;
    }

    public static JDA getJda() {
        return jda;
    }

    public static long getStartupTime() {
        return startupTime;
    }

}

