package ru.sscefalix.sEngineX.commands;

import org.bukkit.command.CommandSender;
import ru.sscefalix.sEngineX.SEngine;
import ru.sscefalix.sEngineX.api.command.AbstractMainCommand;
import ru.sscefalix.sEngineX.api.command.CommandArgument;

import java.util.ArrayList;
import java.util.List;

public class SEngineXCommand<P extends SEngine<P>> extends AbstractMainCommand<P> {
    public SEngineXCommand(P plugin) {
        super("senginex", "Главная команда SEngineX.", new ArrayList<>());
        setPlugin(plugin);

        addSubCommand(new SEngineXAboutCommand<>(plugin));
    }

    @Override
    public void onExecute(CommandSender sender, List<CommandArgument> args) {
        this.sendUsageMessage(sender);
    }
}
