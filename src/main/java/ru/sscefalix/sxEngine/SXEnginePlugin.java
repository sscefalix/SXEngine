package ru.sscefalix.sxEngine;

import org.jetbrains.annotations.NotNull;
import ru.sscefalix.sxEngine.commands.SXEngineCommand;

public final class SXEnginePlugin extends SXEngine<@NotNull SXEnginePlugin> {
    @Override
    public SXEnginePlugin self() {
        return this;
    }

    @Override
    protected void enable() {
        this.commandManager.addCommand(new SXEngineCommand<>(self()));
    }

    @Override
    protected void disable() {

    }
}
