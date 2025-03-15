package ru.sscefalix.sxEngine.api.database;

import lombok.Setter;
import ru.sscefalix.sxEngine.SXEngine;

@Setter
public abstract class AbstractTable<P extends SXEngine<P>> {
    public abstract String getTableName();
}
