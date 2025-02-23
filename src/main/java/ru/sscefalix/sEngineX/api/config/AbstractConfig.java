package ru.sscefalix.sEngineX.api.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import ru.sscefalix.sEngineX.SEngine;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractConfig<P extends SEngine<P>> {
    private final P plugin;
    private final String fileName;

    @Getter
    private FileConfiguration config;

    private File configFile;

    public AbstractConfig(P plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.loadConfig();
    }

    private void loadConfig() {
        configFile = new File(plugin.getDataFolder(), fileName);
        if (!configFile.exists()) {
            plugin.getDataFolder().mkdirs();
            plugin.saveResource(fileName, false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        addDefaults();
    }

    private void addDefaults() {
        for (SOption<?> option : getOptions()) {
            if (!config.contains(option.path())) {
                config.set(option.path(), option.defaultValue());
                for (String comment : option.comments()) {
                    config.addDefault(option.path() + ".comment", comment);
                }
            }
        }
        saveConfig();
    }

    public void reloadConfig() {
        loadConfig();
    }

    public void set(String path, Object value) {
        config.set(path, value);
        saveConfig();
    }

    public <T> @Nullable T get(String path) {
        return (@Nullable T) config.get(path);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected List<SOption<?>> getOptions() {
        List<SOption<?>> options = new ArrayList<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.getType() == SOption.class) {
                try {
                    options.add((SOption<?>) field.get(null)); // Получаем статическое поле
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return options;
    }
}