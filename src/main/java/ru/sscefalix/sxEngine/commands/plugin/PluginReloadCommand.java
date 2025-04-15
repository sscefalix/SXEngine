package ru.sscefalix.sxEngine.commands.plugin;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.sscefalix.sxEngine.SXEngine;
import ru.sscefalix.sxEngine.api.command.abc.AbstractSubCommand;
import ru.sscefalix.sxEngine.api.command.argument.CommandArgument;
import ru.sscefalix.sxEngine.api.config.AbstractConfig;
import ru.sscefalix.sxEngine.api.utils.ColorUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PluginReloadCommand<P extends SXEngine<P>> extends AbstractSubCommand<@NotNull P> {
    public PluginReloadCommand(@NotNull P plugin) {
        super("reload", "Команда для перезагрузки конфигураций.", new ArrayList<>());
        setPlugin(plugin);
    }

    @Override
    public void onExecute(CommandSender sender, List<CommandArgument> args) {
        reloadAllConfigs(sender);
    }

    private void reloadAllConfigs(CommandSender sender) {
        List<Field> configFields = getPrivateFieldsOfAbstractConfigType();

        for (Field field : configFields) {
            try {
                field.setAccessible(true);
                @SuppressWarnings("unchecked")
                AbstractConfig<P> config = (AbstractConfig<P>) field.get(getPlugin());
                if (config != null) {
                    config.reload();
                    sender.sendMessage(ColorUtils.colorize("<green>Файл <yellow>" + config.getConfigFile() + "<green> успешно перезагружен!"));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Field> getPrivateFieldsOfAbstractConfigType() {
        List<Field> matchingFields = new ArrayList<>();

        Field[] fields = getPlugin().getClass().getDeclaredFields();

        for (Field field : fields) {
            if (java.lang.reflect.Modifier.isPrivate(field.getModifiers()) &&
                    AbstractConfig.class.isAssignableFrom(field.getType())) {
                matchingFields.add(field);
            }
        }

        return matchingFields;
    }
}
