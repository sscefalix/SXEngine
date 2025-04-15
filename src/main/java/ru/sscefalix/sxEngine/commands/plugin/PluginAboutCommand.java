package ru.sscefalix.sxEngine.commands.plugin;

import org.bukkit.command.CommandSender;
import ru.sscefalix.sxEngine.SXEngine;
import ru.sscefalix.sxEngine.SXEnginePlugin;
import ru.sscefalix.sxEngine.api.command.abc.AbstractSubCommand;
import ru.sscefalix.sxEngine.api.command.argument.CommandArgument;
import ru.sscefalix.sxEngine.api.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;

public class PluginAboutCommand<P extends SXEngine<P>> extends AbstractSubCommand<P> {
    public PluginAboutCommand(P plugin) {
        super("about", "Команда для получения информации о плагине.", new ArrayList<>());
        setPlugin(plugin);
    }

    @Override
    public void onExecute(CommandSender sender, List<CommandArgument> args) {
        List<String> message = List.of(
                "<black>",
                "<yellow> " + getPlugin().getName() + " <white>v<yellow>" + getPlugin().getPluginMeta().getVersion(),
                "<gray> " + getPlugin().getDescription(),
                "<black>",
                "<black> <yellow>∙ <white>Версия API: <yellow>" + getPlugin().getPluginMeta().getAPIVersion(),
                "<black> <yellow>∙ <white>Авторы: <yellow>" + String.join(", ", getPlugin().getPluginMeta().getAuthors()),
                "<black> <yellow>∙ <white>Сайт: <yellow>" + getPlugin().getPluginMeta().getWebsite(),
                "<black>",
                "<black> <yellow>∙ <white>SXEngine API: <yellow>" + SXEnginePlugin.getPlugin(SXEnginePlugin.class).getPluginMeta().getVersion(),
                "<black>"
        );

        sender.sendMessage(ColorUtils.colorize(String.join("<newline>", message)));
    }
}