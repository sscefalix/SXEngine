package ru.sscefalix.sxEngine;

import org.jetbrains.annotations.NotNull;

public final class SXEnginePlugin extends SXEngine<@NotNull SXEnginePlugin> {
    @Override
    public SXEnginePlugin self() {
        return this;
    }

    @Override
    protected void enable() {
    }

    @Override
    protected void disable() {

    }
}
