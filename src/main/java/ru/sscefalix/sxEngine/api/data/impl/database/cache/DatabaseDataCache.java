package ru.sscefalix.sxEngine.api.data.impl.database.cache;

import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import ru.sscefalix.sxEngine.SXEngine;
import ru.sscefalix.sxEngine.api.database.AbstractTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
public class DatabaseDataCache<P extends SXEngine<P>, T extends AbstractTable<P>> {
    private final P plugin;
    private final Class<T> tableClass;
    private final String keyName;

    private static class CacheEntry<T> {
        final T value;
        final long timestamp;

        CacheEntry(T value) {
            this.value = value;
            this.timestamp = System.currentTimeMillis();
        }
    }

    private BukkitRunnable cleanupTask;

    private final Map<String, List<CacheEntry<T>>> cache = new ConcurrentHashMap<>();
    private final long ttl;
    private final long cleanupInterval;

    public DatabaseDataCache(P plugin, Class<T> tableClass, String keyName) {
        this(plugin, tableClass, keyName, 300_000, 20L * 60);
    }

    public DatabaseDataCache(P plugin, Class<T> tableClass, String keyName, long ttl) {
        this(plugin, tableClass, keyName, ttl, 20L * 60);
    }

    public DatabaseDataCache(P plugin, Class<T> tableClass, String keyName, long ttl, long cleanupInterval) {
        this.plugin = plugin;
        this.tableClass = tableClass;
        this.keyName = keyName;
        this.ttl = ttl;
        this.cleanupInterval = cleanupInterval;
    }

    /**
     * Добавляет или обновляет запись в кэше.
     * @param key Ключ
     * @param table Значение
     */
    public void add(String key, T table) {
        cache.computeIfAbsent(key, k -> new ArrayList<>()).add(new CacheEntry<>(table));
    }

    /**
     * Получает все записи из кэша для заданного ключа, если они не устарели.
     * @param key Ключ
     * @return Список значений (пустой, если ничего не найдено или все устарели)
     */
    public List<T> getAll(String key) {
        List<CacheEntry<T>> entries = getValidEntries(key);
        return entries.stream().map(entry -> entry.value).collect(Collectors.toList());
    }

    /**
     * Получает первую запись из кэша, если она не устарела.
     * @param key Ключ
     * @return Первое значение или null, если не найдено или все записи устарели
     */
    public T get(String key) {
        List<CacheEntry<T>> entries = getValidEntries(key);
        return entries.isEmpty() ? null : entries.getFirst().value;
    }

    /**
     * Вспомогательный метод для получения актуальных записей из кэша.
     */
    private List<CacheEntry<T>> getValidEntries(String key) {
        List<CacheEntry<T>> entries = cache.get(key);

        if (entries == null) {
            return new ArrayList<>();
        }

        long now = System.currentTimeMillis();

        entries.removeIf(entry -> now - entry.timestamp > ttl);

        if (entries.isEmpty()) {
            cache.remove(key);
        }

        return entries;
    }

    /**
     * Удаляет конкретную запись из кэша по ключу и значению.
     * @param key Ключ
     * @param table Значение для удаления
     * @return true, если запись была найдена и удалена, false иначе
     */
    public boolean remove(String key, T table) {
        List<CacheEntry<T>> entries = cache.get(key);

        if (entries == null) {
            return false;
        }

        boolean removed = entries.removeIf(entry -> entry.value.equals(table));
        if (entries.isEmpty()) {
            cache.remove(key);
        }
        return removed;
    }

    /**
     * Удаляет все записи для заданного ключа из кэша.
     * @param key Ключ
     */
    public void removeAll(String key) {
        cache.remove(key);
    }

    /**
     * Очищает весь кэш.
     */
    public void clear() {
        cache.clear();
    }

    /**
     * Запускает асинхронную задачу очистки кэша.
     */
    public void startCleanupTask() {
        cleanupTask = new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                cache.entrySet().removeIf(entry -> {
                    entry.getValue().removeIf(e -> now - e.timestamp > ttl);
                    boolean isEmpty = entry.getValue().isEmpty();

                    if (isEmpty) {
                        plugin.getLogger();
                        plugin.getLogger().fine("Removed all expired cache entries for key: " + entry.getKey());
                    }

                    return isEmpty;
                });
            }
        };
        cleanupTask.runTaskTimerAsynchronously(plugin, 0L, cleanupInterval);
    }

    /**
     * Завершает асинхронную задачу очистки кэша.
     */
    public void stopCleanupTask() {
        if (cleanupTask != null && !cleanupTask.isCancelled()) {
            cleanupTask.cancel();
        }
    }
}