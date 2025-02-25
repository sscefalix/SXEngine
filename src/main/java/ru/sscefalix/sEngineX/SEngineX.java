package ru.sscefalix.sEngineX;

import org.jetbrains.annotations.NotNull;
import ru.sscefalix.sEngineX.commands.SEngineXCommand;

public final class SEngineX extends SEngine<@NotNull SEngineX> {
    @Override
    public SEngineX self() {
        return this;
    }

    @Override
    protected void enable() {
        this.commandManager.addCommand(new SEngineXCommand<>(self()));
    }

    @Override
    protected void disable() {

    }
}
