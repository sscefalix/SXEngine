package ru.sscefalix.sxEngine.api.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import ru.sscefalix.sxEngine.SXEngine;

import java.util.List;

public class ServerCommand<P extends SXEngine<P>> extends Command {
    private final CommandExecutor executor;
    private final TabCompleter tabCompleter;

    protected ServerCommand(AbstractMainCommand<P> command) {
        super(command.getName());
        this.executor = command;
        this.tabCompleter = command;
        setPermission(command.getPermission());
        setAliases(command.getAliases());
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String label, @NotNull String @NotNull [] args) {
        return executor.onCommand(commandSender, this, label, args);
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (tabCompleter != null) {
            List<String> completions = tabCompleter.onTabComplete(sender, this, alias, args);
            return completions != null ? completions : super.tabComplete(sender, alias, args);
        }
        return super.tabComplete(sender, alias, args);
    }
}
