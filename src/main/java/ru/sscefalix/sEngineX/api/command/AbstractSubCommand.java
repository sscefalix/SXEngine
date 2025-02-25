package ru.sscefalix.sEngineX.api.command;

import lombok.Getter;
import lombok.Setter;
import ru.sscefalix.sEngineX.SEngine;

import java.util.List;


@Getter
@Setter
public abstract class AbstractSubCommand<P extends SEngine<P>> extends AbstractCommand<P> {
    private AbstractMainCommand<P> parent;

    public AbstractSubCommand(String command, String description, List<CommandArgument> arguments) {
        super(command, description, arguments);
    }
}
