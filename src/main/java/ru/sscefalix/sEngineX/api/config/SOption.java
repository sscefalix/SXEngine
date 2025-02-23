package ru.sscefalix.sEngineX.api.config;

import java.util.List;

public record SOption<T>(String path, T defaultValue, List<String> comments) {
    public static <T> SOption<T> create(String path, T defaultValue, List<String> comments) {
        return new SOption<>(path, defaultValue, comments);
    }
}
