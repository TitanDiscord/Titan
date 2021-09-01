package me.anutley.titan.commands;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class Command extends ListenerAdapter {

    public abstract String getCommandName();
    public abstract String getCommandDescription();
    public abstract String getCommandUsage();



}
