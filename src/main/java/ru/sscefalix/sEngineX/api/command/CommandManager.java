package ru.sscefalix.sEngineX.api.command;

import lombok.Getter;
import org.bukkit.command.PluginCommand;
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
        commands.add(command);

        PluginCommand pluginCommand = getPlugin().getCommand(command.getName());

        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        }
    }

    @Override
    protected void onSetup() {

    }

    @Override
    protected void onShutdown() {
        for (AbstractMainCommand<P> command : commands) {
            PluginCommand pluginCommand = getPlugin().getCommand(command.getName());

            if (pluginCommand != null) {
                pluginCommand.setExecutor(null);
                pluginCommand.setTabCompleter(null);
            }

            commands.remove(command);
        }
    }
}
