package ru.sscefalix.sxEngine.api.utils;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ColorUtils {
    public static TextComponent colorize(String message) {
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();

        return serializer.deserialize(message);
    }

    public static String stripColors(String message) {
        return message.replaceAll("(?i)&[0-9A-FK-OR]", "");
    }
}
