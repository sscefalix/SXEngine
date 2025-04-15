package ru.sscefalix.sxEngine.api.data.impl.cached;

import lombok.Getter;
import ru.sscefalix.sxEngine.SXEngine;
import ru.sscefalix.sxEngine.api.data.AbstractDataHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractCachedDataHolder<P extends SXEngine<P>, T> extends AbstractDataHolder<P> {
    @Getter
    private final Class<T> dataClass;
    @Getter
    private final String keyName;
    private final Map<String, List<T>> cache;

    /**
     * Конструктор с параметрами по умолчанию.
     * @param plugin Экземпляр плагина
     * @param dataClass Класс данных (например, класс запроса)
     * @param keyName Имя ключа для поиска (например, имя игрока)
     */
    public AbstractCachedDataHolder(P plugin, Class<T> dataClass, String keyName) {
        super(plugin);
        this.dataClass = dataClass;
        this.keyName = keyName;
        this.cache = new ConcurrentHashMap<>();
    }

    /**
     * Получает первую запись по ключу из кэша.
     * @param key Ключ для поиска
     * @return Первая запись или null, если данных нет
     */
    @Nullable
    public T get(String key) {
        List<T> entries = cache.get(key);
        return (entries != null && !entries.isEmpty()) ? entries.getFirst() : null;
    }

    /**
     * Получает все записи из кэша, соответствующие ключу.
     * @param key Ключ для поиска
     * @return Список записей (пустой список, если ничего не найдено)
     */
    @Nonnull
    public List<T> getAll(String key) {
        List<T> entries = cache.get(key);
        return entries != null ? new ArrayList<>(entries) : new ArrayList<>();
    }

    /**
     * Добавляет или обновляет данные в кэше.
     * @param key Ключ
     * @param value Значение для сохранения
     */
    public void save(String key, T value) {
        cache.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    /**
     * Удаляет конкретную запись из кэша.
     * @param key Ключ
     * @param value Запись для удаления
     * @return true, если запись была удалена, false если не найдена
     */
    public boolean delete(String key, T value) {
        List<T> entries = cache.get(key);
        if (entries == null) {
            return false;
        }
        boolean removed = entries.remove(value);
        if (entries.isEmpty()) {
            cache.remove(key);
        }
        return removed;
    }

    /**
     * Удаляет все записи из кэша для заданного ключа.
     * @param key Ключ для удаления
     */
    public void deleteAll(String key) {
        cache.remove(key);
    }

    /**
     * Очищает весь кэш.
     */
    public void clearCache() {
        cache.clear();
    }

    /**
     * Проверяет, существует ли запись в кэше для заданного ключа.
     * @param key Ключ для проверки
     * @return true, если данные есть, false иначе
     */
    public boolean contains(String key) {
        return cache.containsKey(key) && !cache.get(key).isEmpty();
    }
}