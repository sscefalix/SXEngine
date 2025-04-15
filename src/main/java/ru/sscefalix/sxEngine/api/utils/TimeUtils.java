package ru.sscefalix.sxEngine.api.utils;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtils {
    // Карта для быстрого поиска TimeUnit по любому алиасу
    private static final Map<String, TimeUnit> TIME_UNITS = new HashMap<>();

    static {
        // Инициализация единиц времени
        new TimeUnit(1000L, "секунд", "s", "с", "sec", "seconds");
        new TimeUnit(60 * 1000L, "минут", "m", "мин", "min", "minutes");
        new TimeUnit(60 * 60 * 1000L, "часов", "h", "ч", "hr", "hours");
        new TimeUnit(24 * 60 * 60 * 1000L, "дней", "d", "д", "day", "days");
        new TimeUnit(7 * 24 * 60 * 60 * 1000L, "недель", "w", "н", "wk", "week", "weeks");
        new TimeUnit(30 * 24 * 60 * 60 * 1000L, "месяцев", "mo", "мес", "month", "months", "месяц");
    }

    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)\\s*([a-zA-Zа-яА-Я]+)");

    /**
     * Класс, представляющий единицу времени с множеством алиасов
     */
    private static class TimeUnit {
        final long milliseconds;
        final String name;
        final Set<String> aliases;

        TimeUnit(long milliseconds, String name, String... aliases) {
            this.milliseconds = milliseconds;
            this.name = name;
            this.aliases = new HashSet<>(Arrays.asList(aliases));

            // Добавляем все алиасы в общую карту
            for (String alias : aliases) {
                TIME_UNITS.put(alias.toLowerCase(), this);
            }
        }
    }

    /**
     * Парсит строку времени и возвращает длительность в миллисекундах.
     * Поддерживает форматы: "1s", "2д 3ч", "10d7h11m"
     * @param input строка времени
     * @return время в миллисекундах
     * @throws IllegalArgumentException если формат неверный
     */
    public static long parseTime(@NotNull String input) {
        if (input.trim().isEmpty()) {
            throw new IllegalArgumentException("Неверный формат времени.");
        }

        long totalMilliseconds = 0;
        Matcher matcher = TIME_PATTERN.matcher(input.trim());

        while (matcher.find()) {
            long amount = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2).toLowerCase();

            TimeUnit timeUnit = TIME_UNITS.get(unit);
            if (timeUnit == null) {
                throw new IllegalArgumentException("Неверная единица времени: " + unit);
            }

            totalMilliseconds += amount * timeUnit.milliseconds;
        }

        if (totalMilliseconds == 0) {
            throw new IllegalArgumentException("Неверный формат времени.");
        }

        return totalMilliseconds;
    }

    /**
     * Преобразует время в миллисекундах в читаемую строку
     * @param milliseconds время в миллисекундах
     * @return форматированная строка времени
     */
    public static String formatTime(long milliseconds) {
        if (milliseconds <= 0) {
            return "0 секунд";
        }

        StringBuilder result = new StringBuilder();
        long remainingMs = milliseconds;

        // Проходим по единицам времени от больших к меньшим
        String[] unitOrder = {"mo", "w", "d", "h", "m", "s"};
        for (String unitKey : unitOrder) {
            TimeUnit unit = TIME_UNITS.get(unitKey);
            if (unit == null || remainingMs < unit.milliseconds) continue;

            long count = remainingMs / unit.milliseconds;
            remainingMs %= unit.milliseconds;

            if (count > 0) {
                if (!result.isEmpty()) {
                    result.append(" ");
                }
                result.append(count).append(" ").append(unit.name);
            }
        }

        return result.toString();
    }
}