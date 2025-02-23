package ru.sscefalix.sEngineX.commands.list;

import org.bukkit.command.CommandSender;
import ru.sscefalix.sEngineX.SEngine;
import ru.sscefalix.sEngineX.api.command.AbstractMainCommand;
import ru.sscefalix.sEngineX.api.command.CommandArgument;

import java.util.ArrayList;
import java.util.List;

public class SEngineXCommand<P extends SEngine<P>> extends AbstractMainCommand<P> {
    public SEngineXCommand() {
        super("senginex", "senginex.commands.default", "Главная команда SEngineX.", new ArrayList<>());

        addSubCommand(new SEngineXSubCommand<>(this));
    }

    @Override
    public void onExecute(CommandSender sender, List<CommandArgument> args) {
        this.sendUsageMessage(sender);
    }
}
