package ru.sscefalix.sxEngine.api.manager;

import lombok.Getter;
import ru.sscefalix.sxEngine.SXEngine;
import ru.sscefalix.sxEngine.api.listener.EventListener;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractManager<P extends SXEngine<P>> {
    @Getter
    private final P plugin;

    private final Set<EventListener> listeners;

    public AbstractManager(P plugin) {
        this.plugin = plugin;
        this.listeners = new HashSet<>();
    }

    public void setup() {
        this.onSetup();
    }

    public void shutdown() {
        this.listeners.forEach(EventListener::unregister);
        this.listeners.clear();

        this.onShutdown();
    }

    private void addListener(EventListener listener) {
        if (this.listeners.add(listener)) {
            listener.register();
        }
    }

    protected abstract void onSetup();

    protected abstract void onShutdown();
}
