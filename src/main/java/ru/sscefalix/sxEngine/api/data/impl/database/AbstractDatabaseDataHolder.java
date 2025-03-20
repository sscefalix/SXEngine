package ru.sscefalix.sxEngine.api.data.impl.database;

import lombok.Getter;
import ru.sscefalix.sxEngine.SXEngine;
import ru.sscefalix.sxEngine.api.data.AbstractDataHolder;
import ru.sscefalix.sxEngine.api.data.impl.database.cache.DatabaseDataCache;
import ru.sscefalix.sxEngine.api.database.AbstractTable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDatabaseDataHolder<P extends SXEngine<P>, T extends AbstractTable<P>> extends AbstractDataHolder<P> {
    private final Class<T> tableClass;
    private final DatabaseDataCache<P, T> cache;
    private final String keyName;

    public AbstractDatabaseDataHolder(P plugin, Class<T> tableClass, String keyName) {
        this(plugin, tableClass, keyName, 300_000, 20L * 60);
    }

    public AbstractDatabaseDataHolder(P plugin, Class<T> tableClass, String keyName, long cacheTtl) {
        this(plugin, tableClass, keyName, cacheTtl, 20L * 60);
    }

    public AbstractDatabaseDataHolder(P plugin, Class<T> tableClass, String keyName, long cacheTtl, long cleanupInterval) {
        super(plugin);

        if (!getPlugin().getDatabaseManager().isLoaded()) {
            throw new IllegalStateException("Database manager is not loaded.");
        }

        this.tableClass = tableClass;
        this.cache = new DatabaseDataCache<>(plugin, tableClass, keyName, cacheTtl, cleanupInterval);
        this.keyName = keyName;
    }

    /**
     * Получает первую запись по ключу из кэша или базы данных.
     * @param key Ключ для поиска
     * @return Первая запись или null, если данные не найдены
     */
    @Nullable
    public T get(String key) {
        T cachedValue = cache.get(key);
        if (cachedValue != null) {
            return cachedValue;
        }

        try {
            T value = getPlugin().getDatabaseManager().getOneByField(tableClass, keyName, key);
            if (value != null) {
                cache.add(key, value); // Кэшируем полученную запись
            }
            return value;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch data for key: " + key, e);
        }
    }

    /**
     * Получает все записи из кэша или базы данных, соответствующие указанному ключу.
     * @param key Ключ для поиска
     * @return Список записей, соответствующих ключу (пустой список, если ничего не найдено)
     */
    @Nonnull
    public List<T> getAll(String key) {
        List<T> cachedValues = cache.getAll(key);
        if (!cachedValues.isEmpty()) {
            return cachedValues;
        }

        try {
            List<T> results = getPlugin().getDatabaseManager().getByField(tableClass, keyName, key);
            for (T value : results) {
                if (value != null) {
                    cache.add(key, value); // Кэшируем все полученные записи
                }
            }
            return results.isEmpty() ? new ArrayList<>() : results;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch all data for key: " + key, e);
        }
    }

    /**
     * Добавляет или обновляет данные в кэше и базе данных.
     * @param key Ключ
     * @param value Значение для сохранения
     */
    public void save(String key, T value) {
        cache.add(key, value);
        try {
            getPlugin().getDatabaseManager().save(tableClass, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save data for key: " + key, e);
        }
    }

    /**
     * Удаляет конкретную запись из кэша и базы данных.
     * @param key Ключ
     * @param value Запись для удаления
     */
    public boolean delete(String key, T value) {
        boolean removed = cache.remove(key, value);

        try {
            Long id = getIdFromTable(value);
            if (id != null) {
                getPlugin().getDatabaseManager().deleteByField(tableClass, "id", id);
            } else {
                throw new IllegalArgumentException("Cannot delete record: ID not found in table object.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete data for key: " + key, e);
        }

        return removed;
    }

    /**
     * Удаляет все записи из кэша и базы данных для заданного ключа.
     * @param key Ключ для удаления
     */
    public void deleteAll(String key) {
        cache.removeAll(key);
        try {
            getPlugin().getDatabaseManager().deleteByField(tableClass, keyName, key);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete all data for key: " + key, e);
        }
    }

    /**
     * Вспомогательный метод для получения ID из объекта таблицы.
     * Реализуйте этот метод в зависимости от структуры вашей AbstractTable.
     */
    private Long getIdFromTable(T value) {
        try {
            java.lang.reflect.Field idField = tableClass.getDeclaredField("id");
            idField.setAccessible(true);
            Object id = idField.get(value);
            return id instanceof Number ? ((Number) id).longValue() : null;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Очищает весь кэш.
     */
    public void clearCache() {
        cache.clear();
    }

    /**
     * Останавливает задачу очистки кэша (например, при выключении плагина).
     */
    public void stopCacheCleanup() {
        cache.stopCleanupTask();
    }

    /**
     * Запускает задачу очистки кэша (например, при включении плагина).
     */
    public void startCacheCleanup() {
        cache.startCleanupTask();
    }
}