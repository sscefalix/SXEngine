package ru.sscefalix.sEngineX.api.database;

import lombok.Setter;
import ru.sscefalix.sEngineX.SEngine;

@Setter
public abstract class AbstractTable<P extends SEngine<P>> {
    public abstract String getTableName();
}
