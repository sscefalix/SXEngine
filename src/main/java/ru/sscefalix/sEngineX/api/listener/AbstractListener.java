package ru.sscefalix.sEngineX.api.listener;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.sscefalix.sEngineX.SEngine;

@Getter
public class AbstractListener<P extends SEngine<P>> implements EventListener {
    @NotNull
    private final P plugin;

    public AbstractListener(@NotNull P plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register() {
        getPlugin().getPluginManager().registerEvents(this, getPlugin());
    }
}
