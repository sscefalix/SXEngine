package ru.sscefalix.sEngineX.api.managers;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public interface EventListener extends Listener {
    void register();

    default void unregister() {
        HandlerList.unregisterAll(this);
    }
}
