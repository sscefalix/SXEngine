package ru.sscefalix.sxEngine.commands;

import org.bukkit.command.CommandSender;
import ru.sscefalix.sxEngine.SXEngine;
import ru.sscefalix.sxEngine.api.command.abc.AbstractSubCommand;
import ru.sscefalix.sxEngine.api.command.argument.CommandArgument;
import ru.sscefalix.sxEngine.api.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;

public class SXEngineAboutCommand<P extends SXEngine<P>> extends AbstractSubCommand<P> {
    public SXEngineAboutCommand(P plugin) {
        super("about", "Команда для получения информации о плагине.", new ArrayList<>());
        setPlugin(plugin);
    }

    @Override
    public void onExecute(CommandSender sender, List<CommandArgument> args) {
        List<String> message = List.of(
                "&0",
                "&e " + getPlugin().getName() + " &fv" + getPlugin().getPluginMeta().getVersion(),
                "&7 " + getPlugin().getDescription().getDescription(),
                "&0",
                "&0 &e∙ &fВерсия API: &e" + getPlugin().getPluginMeta().getAPIVersion(),
                "&0 &e∙ &fАвторы: &e" + String.join(", ", getPlugin().getPluginMeta().getAuthors()),
                "&0 &e∙ &fСайт: &e" + getPlugin().getPluginMeta().getWebsite(),
                "&0"
        );

        for (String line : message) {
            sender.sendMessage(ColorUtils.colorize(line));
        }
    }
}
