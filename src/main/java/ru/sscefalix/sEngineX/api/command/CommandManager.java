package ru.sscefalix.sEngineX.api.command;

import lombok.Getter;
import org.bukkit.command.Command;
import ru.sscefalix.sEngineX.SEngine;
import ru.sscefalix.sEngineX.api.manager.AbstractManager;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandManager<P extends SEngine<P>> extends AbstractManager<P> {
    private final List<AbstractMainCommand<P>> commands;

    public CommandManager(P plugin) {
        super(plugin);

        this.commands = new ArrayList<>();
    }

    public void addCommand(AbstractMainCommand<P> command) {
//        commands.add(command);

        try {
            Command customCommand = new ServerCommand<>(command);

            getPlugin().getServer().getCommandMap().register(getPlugin().getPluginMeta().getName(), customCommand);

            commands.add(command);

            getPlugin().getLogger().info("Команда '/" + command.getName() + "' успешно зарегистрирована!");
        } catch (Exception e) {
            getPlugin().getLogger().severe("Ошибка при регистрации команды '/" + command.getName() + "': " + e.getMessage());
        }

//        PluginCommand pluginCommand = getPlugin().getCommand(command.getName());
//
//        if (pluginCommand != null) {
//            pluginCommand.register(getPlugin().getServer().getCommandMap());
//
//            pluginCommand.setExecutor(command);
//            pluginCommand.setTabCompleter(command);
//        }
    }

    @Override
    protected void onSetup() {

    }

    @Override
    protected void onShutdown() {

    }
}
