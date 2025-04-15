package ru.sscefalix.sxEngine.api.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ColorUtils {
    @Deprecated(forRemoval = true, since = "1.5.4")
    public static List<Component> colorizeMiniMessage(@NotNull String message) {
        MiniMessage miniMessage = MiniMessage.miniMessage();

        return Arrays.stream(message.split("\n")).map(miniMessage::deserialize).toList();
    }

    public static Component colorize(@NotNull String message) {
        MiniMessage miniMessage = MiniMessage.miniMessage();

        return miniMessage.deserialize(String.join("<newline>", message.split("\n")));
    }
}
