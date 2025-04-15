package ru.sscefalix.sxEngine.api.command;

import lombok.Getter;
import org.bukkit.command.Command;
import ru.sscefalix.sxEngine.SXEngine;
import ru.sscefalix.sxEngine.api.command.abc.AbstractMainCommand;
import ru.sscefalix.sxEngine.api.command.abc.AbstractSubCommand;
import ru.sscefalix.sxEngine.api.command.server.ServerCommand;
import ru.sscefalix.sxEngine.api.manager.AbstractManager;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandManager<P extends SXEngine<P>> extends AbstractManager<P> {
    private final List<AbstractMainCommand<P>> commands;
    private AbstractMainCommand<P> pluginCommand;

    public CommandManager(P plugin) {
        super(plugin);

        this.commands = new ArrayList<>();
    }

    public void setPluginCommand(AbstractMainCommand<P> pluginCommand) {
        this.pluginCommand = pluginCommand;

        addCommand(pluginCommand);
    }

    public void addCommand(AbstractMainCommand<P> command) {
        try {
            Command customCommand = new ServerCommand<>(command);

            getPlugin().getServer().getCommandMap().register(getPlugin().getPluginMeta().getName(), customCommand);

            commands.add(command);

            getPlugin().getLogger().info("Команда '/" + command.getName() + "' успешно зарегистрирована!");
        } catch (Exception e) {
            getPlugin().getLogger().severe("Ошибка при регистрации команды '/" + command.getName() + "': " + e.getMessage());
        }
    }

    public void addCommand(AbstractSubCommand<P> subCommand) {
        this.pluginCommand.addSubCommand(subCommand);
    }

    @Override
    protected void onSetup() {

    }

    @Override
    protected void onShutdown() {

    }
}
