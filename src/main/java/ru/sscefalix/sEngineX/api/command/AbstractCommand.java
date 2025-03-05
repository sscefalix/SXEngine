package ru.sscefalix.sEngineX.api.command;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.sscefalix.sEngineX.SEngine;
import ru.sscefalix.sEngineX.api.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class AbstractCommand<P extends SEngine<P>> {
    @Setter
    private P plugin;

    private final String name;
    private final String description;
    private final List<CommandArgument> arguments;

    @Setter
    private boolean onlyPlayers;

    public AbstractCommand(String name, String description, List<CommandArgument> arguments) {
        this.name = name;
        this.description = description;
        this.arguments = arguments;
    }

    public String getPermission() {
        return getPlugin().getPermissionManager().commandPermission(this);
    }

    protected void sendPermissionMessage(CommandSender sender) {
        sender.sendMessage(ColorUtils.colorize("&cУ вас недостаточно прав. &7({permission})".replace("{permission}", getPermission())));
    }

    protected void sendUsageMessage(CommandSender sender) {
        StringBuilder builder = new StringBuilder();

        builder.append("&0").append("\n");

        if (this instanceof AbstractSubCommand<P> subCommand) {
            builder.append("&0  &e∙ &fПомощь по использованию команды &e/").append(subCommand.getParent().getName()).append(" ").append(subCommand.getName()).append("\n\n");
            builder.append("&0  &7▸ &fАргументы &a<обязательные> &e[не обязательные] &7: ");

            for (CommandArgument arg : subCommand.getArguments()) {
                builder.append("\n").append("&0  ").append(arg.isRequired() ? "&a<" : "&e[").append(arg.getName()).append(arg.isRequired() ? ">" : "]").append(" &7— ").append("&f").append(argumentTypeToString(arg));
            }
        } else if (this instanceof AbstractMainCommand<P> mainCommand) {
            builder.append("&0 &e∙ &fПомощь по использованию команды &e/").append(mainCommand.getName()).append("\n");

            if (mainCommand.getSubCommands().isEmpty()) {
                builder.append("\n").append("&0  &7▸ &fАргументы &a<обязательные> &e[не обязательные] &7: ");

                for (CommandArgument arg : mainCommand.getArguments()) {
                    builder.append("\n").append("&0  ").append(arg.isRequired() ? "&a<" : "&e[").append(arg.getName()).append(arg.isRequired() ? ">" : "]").append(" &7— ").append("&f").append(argumentTypeToString(arg));
                }
            } else {
                for (AbstractSubCommand<P> subCommand : mainCommand.getSubCommands()) {
                    builder.append("\n").append("&0  &7▸ &e/").append(mainCommand.getName()).append(" ").append(subCommand.getName()).append(" &7— &f").append(subCommand.getDescription());
                }
            }
        }

        builder.append("\n").append("&0");

        sender.sendMessage(ColorUtils.colorize(builder.toString()));
    }

    protected List<CommandArgument> validateAndParseArguments(List<String> args) throws IllegalArgumentException {
        long requiredCount = getArguments().stream().filter(CommandArgument::isRequired).count();
        if (args.size() < requiredCount) {
            throw new IllegalArgumentException("Недостаточно аргументов.");
        }

        List<CommandArgument> parsedArguments = new ArrayList<>();
        for (int i = 0; i < getArguments().size(); ++i) {
            CommandArgument argument = getArguments().get(i);

            boolean isLastArgument = i == getArguments().size() - 1;
            boolean isListType = argument.getType() == List.class;

            if (i >= args.size() && argument.isRequired()) {
                throw new IllegalArgumentException("Аргумент " + argument.getName() + " является необходимым.");
            }

            if (i < args.size()) {
                if (isLastArgument && isListType) {
                    List<String> remainingArgs = args.subList(i, args.size());
                    argument.setValue(new ArrayList<>(remainingArgs));
                } else {
                    String arg = args.get(i);
                    if (!isValidType(arg, argument.getType())) {
                        throw new IllegalArgumentException("Неверный тип для аргумента " + argument.getName());
                    }
                    argument.setValue(parseArgument(arg, argument));
                }
            }

            parsedArguments.add(argument);
        }

        return parsedArguments;
    }

    private boolean isValidType(String arg, Class<?> type) {
        if (type == Integer.class) {
            try {
                Integer.parseInt(arg);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (type == Double.class) {
            try {
                Double.parseDouble(arg);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (type == Boolean.class) {
            return arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("false");
        } else {
            return true;
        }
    }

    private Object parseArgument(String arg, CommandArgument commandArgument) {
        if (commandArgument.getType() == Integer.class) {
            return Integer.parseInt(arg);
        } else if (commandArgument.getType() == Double.class) {
            return Double.parseDouble(arg);
        } else if (commandArgument.getType() == Boolean.class) {
            return Boolean.parseBoolean(arg);
        } else {
            return arg;
        }
    }

    protected String argumentTypeToString(CommandArgument argument) {
        if (argument.getType() == Integer.class) {
            return "число";
        } else if (argument.getType() == Double.class) {
            return "число с точкой";
        } else if (argument.getType() == Boolean.class) {
            return "true/false";
        } else if (argument.getType() == String.class || argument.getType() == List.class) {
            return "строка";
        } else if (argument.getType() == Player.class) {
            return "игрок";
        } else {
            return argument.getType().toString();
        }
    }

    public abstract void onExecute(CommandSender sender, List<CommandArgument> args);
}
