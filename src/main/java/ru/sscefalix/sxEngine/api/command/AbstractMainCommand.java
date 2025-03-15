package ru.sscefalix.sxEngine.api.command;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.sscefalix.sxEngine.SXEngine;
import ru.sscefalix.sxEngine.api.utils.ColorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public abstract class AbstractMainCommand<P extends SXEngine<P>> extends AbstractCommand<P> implements CommandExecutor, TabCompleter {
    private final List<AbstractSubCommand<P>> subCommands;
    private final List<String> aliases;

    public AbstractMainCommand(String command, String description, List<CommandArgument> arguments) {
        super(command, description, arguments);

        this.subCommands = new ArrayList<>();
        this.aliases = new ArrayList<>();
    }

    public void addSubCommand(AbstractSubCommand<P> subCommand) {
        subCommand.setParent(this);
        subCommands.add(subCommand);
    }

    public void addAlias(String alias) {
        aliases.add(alias);
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
        } catch (Exception e) {
            sender.sendMessage(ColorUtils.colorize("&c" + e.getMessage()));
            command.sendUsageMessage(sender);
        }

        return true;
    }

    private AbstractSubCommand<P> findSubCommand(String name) {
        for (AbstractSubCommand<P> subCommand : subCommands) {
            if (subCommand.getName().equalsIgnoreCase(name)) {
                return subCommand;
            }
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args) {
        String subCommandName = args.length > 0 ? args[0] : null;
        AbstractCommand<P> command;

        if (subCommandName == null) {
            command = this;
        } else {
            command = findSubCommand(subCommandName);
        }

        if (command == null) {
            command = this;
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
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> tabs = new ArrayList<>();

        if (subCommands.isEmpty()) {
            if (args.length > 0 && !args[0].isEmpty()) {
                int index = args.length - 1;
                List<CommandArgument> arguments = getArguments();
                if (index < arguments.size()) {
                    CommandArgument argument = arguments.get(index);
                    tabs.addAll(getArgumentTabs(argument));
                }
            }
        } else {
            if (args.length == 1) {
                tabs.addAll(filterAndSort(
                        subCommands.stream()
                                .filter(command -> sender.hasPermission(command.getPermission()))
                                .map(AbstractSubCommand::getName)
                                .toList(),
                        args[0]
                ));
            } else if (args.length > 1) {
                AbstractSubCommand<P> subCommand = findSubCommand(args[0]);
                if (subCommand != null) {
                    int index = args.length - 2;
                    List<CommandArgument> subArgs = subCommand.getArguments();
                    if (index < subArgs.size()) {
                        CommandArgument argument = subArgs.get(index);
                        tabs.addAll(getArgumentTabs(argument));
                    }
                }
            }
        }

        return tabs;
    }

    private List<String> getArgumentTabs(CommandArgument argument) {
        List<String> tabs = new ArrayList<>();

        if (argument.getType() == Player.class) {
            tabs.addAll(getPlugin().getServer().getOnlinePlayers().stream().map(Player::getName).toList());
        } else if (argument.getType() == Boolean.class) {
            tabs.addAll(List.of("true", "false"));
        } else {
            String arg = (argument.isRequired() ? "<" : "[") + argument.getName() + (argument.isRequired() ? ">" : "]");
            tabs.add(arg);
        }

        return tabs;
    }
}
