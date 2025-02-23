package ru.sscefalix.sEngineX.api.command;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

public class CommandArgument {
    @Getter
    private final String name;
    @Getter
    private final Class<?> type;
    @Getter
    private final boolean required;

    private final Object defaultValue;

    @Setter
    private Object value;

    public CommandArgument(String name, Class<?> type, boolean required) {
        this(name, type, required, null);
    }

    public CommandArgument(String name, Class<?> type, boolean required, @Nullable Object defaultValue) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.defaultValue = defaultValue;
    }

    public Object getValue() {
        return value == null ? defaultValue : value;
    }
}
