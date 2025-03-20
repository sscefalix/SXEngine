package ru.sscefalix.sxEngine.commands;

import org.bukkit.command.CommandSender;
import ru.sscefalix.sxEngine.SXEngine;
import ru.sscefalix.sxEngine.api.command.abc.AbstractMainCommand;
import ru.sscefalix.sxEngine.api.command.argument.CommandArgument;

import java.util.ArrayList;
import java.util.List;

public class SXEngineCommand<P extends SXEngine<P>> extends AbstractMainCommand<P> {
    public SXEngineCommand(P plugin) {
        super("senginex", "Главная команда SEngineX.", new ArrayList<>());
        setPlugin(plugin);

        addSubCommand(new SXEngineAboutCommand<>(plugin));
    }

    @Override
    public void onExecute(CommandSender sender, List<CommandArgument> args) {
        this.sendUsageMessage(sender);
    }
}
