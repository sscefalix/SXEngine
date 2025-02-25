package ru.sscefalix.sEngineX.api.listener;

import lombok.Getter;
import ru.sscefalix.sEngineX.SEngine;
import ru.sscefalix.sEngineX.api.manager.AbstractManager;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ListenerManager<P extends SEngine<P>> extends AbstractManager<P> {
    private final List<AbstractListener<P>> listeners;

    public ListenerManager(P plugin) {
        super(plugin);

        this.listeners = new ArrayList<>();
    }

    public void addListener(AbstractListener<P> listener) {
        this.listeners.add(listener);
        listener.register();
    }

    @Override
    protected void onSetup() {

    }

    @Override
    protected void onShutdown() {
        this.listeners.clear();
    }
}
