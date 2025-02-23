package ru.sscefalix.sEngineX.api.command;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.sscefalix.sEngineX.SEngine;
import ru.sscefalix.sEngineX.api.utils.ColorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public abstract class AbstractMainCommand<P extends SEngine<P>> extends AbstractCommand<P> implements CommandExecutor, TabCompleter {
    private final List<AbstractSubCommand<P>> subCommands;

    public AbstractMainCommand(String command, String permission, String description, List<CommandArgument> arguments) {
        super(command, permission, description, arguments);

        this.subCommands = new ArrayList<>();
    }

    public void addSubCommand(AbstractSubCommand<P> subCommand) {
        subCommands.add(subCommand);
    }

    protected boolean executeCommand(AbstractCommand<P> command, CommandSender sender, List<String> args) {
        if (command.isOnlyPlayers() && !(sender instanceof Player)) {
            sender.sendMessage(ColorUtils.colorize("&cКоманда доступна только игрокам."));
            return true;
        }

        if (!sender.hasPermission(command.getPermission())) {
            sendPermissionMessage(sender);
            return true;
        }

        try {
            List<String> argsToProcess;

            if (command instanceof AbstractSubCommand) {
                argsToProcess = args.subList(1, args.size());
            } else {
                argsToProcess = args;
            }

            List<CommandArgument> arguments = command.validateAndParseArguments(argsToProcess);

            command.onExecute(sender, arguments);
        } catch (Exception ignored) {
            command.sendUsageMessage(sender);
        }

        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        String subCommandName = args.length > 0 ? args[0] : null;
        AbstractCommand<P> command = null;

        if (subCommandName == null) {
            command = this;
        } else {
            for (AbstractSubCommand<P> subCommand : subCommands) {
                if (subCommand.getName().equalsIgnoreCase(subCommandName)) {
                    command = subCommand;
                    break;
                }
            }
        }

        if (command == null) {
            return true;
        }

        return executeCommand(command, sender, Arrays.asList(args));
    }

    public static List<String> filterAndSort(List<String> items, String input) {
        List<String> filteredList = new ArrayList<>(items);

        filteredList.sort((o1, o2) -> {
            int prefixLength1 = getCommonPrefixLength(o1, input);
            int prefixLength2 = getCommonPrefixLength(o2, input);

            if (prefixLength1 != prefixLength2) {
                return Integer.compare(prefixLength2, prefixLength1);
            }
            return 0;
        });

        return filteredList;
    }

    private static int getCommonPrefixLength(String str, String prefix) {
        int length = Math.min(str.length(), prefix.length());
        for (int i = 0; i < length; i++) {
            if (str.charAt(i) != prefix.charAt(i)) {
                return i;
            }
        }
        return length;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> tabs = new ArrayList<>();

        if (args.length == 1) {
            tabs.addAll(filterAndSort(subCommands.stream().filter(command -> sender.hasPermission(command.getPermission())).map(AbstractSubCommand::getName).toList(), label));
        }

        return tabs;
    }
}
