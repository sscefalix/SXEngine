package ru.sscefalix.sEngineX.api.config;

import org.bukkit.configuration.file.YamlConfiguration;
import ru.sscefalix.sEngineX.SEngine;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractConfig<P extends SEngine<P>> {
    protected final P plugin;
    protected final String fileName;
    protected File configFile;
    protected YamlConfiguration config;

    public AbstractConfig(P plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.configFile = new File(plugin.getDataFolder(), fileName);
        reload();
    }

    public void reload() {
        if (!configFile.getParentFile().exists()) {
            configFile.getParentFile().mkdirs();
        }
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        processAnnotatedFields();

        save();
    }

    private void processAnnotatedFields() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigField.class)) {
                ConfigField annotation = field.getAnnotation(ConfigField.class);
                String path = annotation.path();
                String defaultValue = annotation.def();
                String[] description = annotation.desc();

                if (!config.contains(path)) {
                    Object value = parseValue(defaultValue, field.getType());
                    config.set(path, value);
                    if (description.length > 0) {
                        config.setComments(path, Arrays.asList(description));
                    }
                }

                try {
                    Object value = config.get(path);
                    value = convertType(value, field.getType());
                    field.setAccessible(true);
                    field.set(this, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Object parseValue(String defaultValue, Class<?> targetType) {
        if (targetType == String.class) {
            return defaultValue;
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(defaultValue);
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.parseDouble(defaultValue);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(defaultValue);
        } else if (targetType == List.class) {
            return Arrays.asList(defaultValue.split(","));
        }
        return defaultValue;
    }

    private Object convertType(Object value, Class<?> targetType) {
        if (targetType == boolean.class || targetType == Boolean.class) {
            return (value instanceof Boolean) ? value : Boolean.parseBoolean(value.toString());
        } else if (targetType == int.class || targetType == Integer.class) {
            return (value instanceof Number) ? ((Number) value).intValue() : Integer.parseInt(value.toString());
        } else if (targetType == double.class || targetType == Double.class) {
            return (value instanceof Number) ? ((Number) value).doubleValue() : Double.parseDouble(value.toString());
        } else if (targetType == List.class) {
            return config.getList(value.toString());
        }
        return value;
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}