package ru.sscefalix.sxEngine.api.data;

import lombok.Getter;
import ru.sscefalix.sxEngine.SXEngine;
import ru.sscefalix.sxEngine.api.database.AbstractTable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class AbstractDataHolder<P extends SXEngine<P>> {
    private final P plugin;

    public AbstractDataHolder(P plugin) {
        this.plugin = plugin;
    }
}
