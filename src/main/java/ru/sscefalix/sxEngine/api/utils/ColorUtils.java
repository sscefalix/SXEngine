package ru.sscefalix.sxEngine.api.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Arrays;
import java.util.List;

public class ColorUtils {
    public static List<Component> colorizeMiniMessage(String message) {
        MiniMessage miniMessage = MiniMessage.miniMessage();

        return Arrays.stream(message.split("\n")).map(miniMessage::deserialize).toList();
    }

    public static TextComponent colorize(String message) {
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();

        return serializer.deserialize(message);
    }

    public static String stripColors(String message) {
        return message.replaceAll("(?i)&[0-9A-FK-OR]", "");
    }
}
