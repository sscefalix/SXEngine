package ru.sscefalix.sxEngine.api.data;

import lombok.Getter;
import ru.sscefalix.sxEngine.SXEngine;

@Getter
public abstract class AbstractDataHolder<P extends SXEngine<P>> {
    private final P plugin;

    public AbstractDataHolder(P plugin) {
        this.plugin = plugin;
    }


}
