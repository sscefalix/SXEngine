package ru.sscefalix.sxEngine.commands.plugin;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.sscefalix.sxEngine.SXEngine;
import ru.sscefalix.sxEngine.api.command.abc.AbstractMainCommand;
import ru.sscefalix.sxEngine.api.command.argument.CommandArgument;

import java.util.ArrayList;
import java.util.List;

public class PluginCommand<P extends SXEngine<P>> extends AbstractMainCommand<@NotNull P> {
    public PluginCommand(@NotNull P plugin) {
        super(plugin.getName().toLowerCase(), "Главная команда плагина " + plugin.getName(), new ArrayList<>());
        setPlugin(plugin);

        addSubCommand(new PluginReloadCommand<>(plugin));
        addSubCommand(new PluginAboutCommand<>(plugin));
    }

    @Override
    public void onExecute(CommandSender sender, List<CommandArgument> args) {
        sendUsageMessage(sender);
    }
}
