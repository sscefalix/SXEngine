package ru.sscefalix.sEngineX.commands;

import lombok.Getter;
import org.bukkit.command.PluginCommand;
import ru.sscefalix.sEngineX.SEngine;
import ru.sscefalix.sEngineX.api.command.AbstractMainCommand;
import ru.sscefalix.sEngineX.api.managers.AbstractManager;

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
        command.setPlugin(getPlugin());
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

    }
}
