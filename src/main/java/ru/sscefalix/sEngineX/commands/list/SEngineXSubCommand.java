package ru.sscefalix.sEngineX.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.sscefalix.sEngineX.SEngine;
import ru.sscefalix.sEngineX.api.command.AbstractMainCommand;
import ru.sscefalix.sEngineX.api.command.AbstractSubCommand;
import ru.sscefalix.sEngineX.api.command.CommandArgument;

import java.util.List;

public class SEngineXSubCommand<P extends SEngine<P>> extends AbstractSubCommand<P> {
    public SEngineXSubCommand(AbstractMainCommand<P> parent) {
        super("sub", "senginex.commands.sub", "Саб-команда SEngineX.", List.of(
                new CommandArgument("игрок", Player.class, true),
                new CommandArgument("число", Integer.class, false, false)
        ));
        this.setParent(parent);
    }

    @Override
    public void onExecute(CommandSender sender, List<CommandArgument> args) {
        sender.sendMessage(String.join(", ", args.stream().map(arg -> arg.getValue().toString()).toList()));
    }
}
