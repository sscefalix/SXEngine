package ru.sscefalix.sEngineX;

import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import ru.sscefalix.sEngineX.api.command.CommandManager;
import ru.sscefalix.sEngineX.api.database.DatabaseManager;
import ru.sscefalix.sEngineX.api.listener.ListenerManager;
import ru.sscefalix.sEngineX.api.permission.PermissionManager;

import java.util.function.Consumer;

@Getter
public abstract class SEngine<P extends SEngine<P>> extends JavaPlugin {
    protected CommandManager<P> commandManager;
    protected PermissionManager<P> permissionManager;
    protected DatabaseManager<P> databaseManager;
    protected ListenerManager<P> listenerManager;

    @Override
    public void onEnable() {
        loadManagers();
        enable();
    }

    @Override
    public void onDisable() {
        unloadManagers();
        disable();
    }

    public abstract P self();

    protected abstract void enable();

    protected abstract void disable();

    private void loadManagers() {
        commandManager = new CommandManager<>(self());
        commandManager.setup();

        permissionManager = new PermissionManager<>(self());
        permissionManager.setup();

        databaseManager = new DatabaseManager<>(self());

        listenerManager = new ListenerManager<>(self());
        listenerManager.setup();
    }

    private void unloadManagers() {
        if (commandManager != null) {
            commandManager.shutdown();
        }

        if (permissionManager != null) {
            permissionManager.shutdown();
        }

        if (databaseManager != null) {
            databaseManager.shutdown();
        }

        if (listenerManager != null) {
            listenerManager.shutdown();
        }
    }

    public @NotNull PluginManager getPluginManager() {
        return getServer().getPluginManager();
    }

    public @NotNull BukkitScheduler getScheduler() {
        return getServer().getScheduler();
    }

    public void runTask(Consumer<BukkitTask> task) {
        getScheduler().runTask(this, task);
    }

    public void runTaskLater(Consumer<BukkitTask> task, long delay) {
        getScheduler().runTaskLater(this, task, delay);
    }

    public void runTaskTimer(Consumer<BukkitTask> task, long delay, long period) {
        getScheduler().runTaskTimer(this, task, delay, period);
    }

    public void runTaskAsync(Consumer<BukkitTask> task) {
        getScheduler().runTaskAsynchronously(this, task);
    }

    public void runTaskLaterAsync(Consumer<BukkitTask> task, long delay) {
        getScheduler().runTaskLaterAsynchronously(this, task, delay);
    }

    public void runTaskTimerAsync(Consumer<BukkitTask> task, long delay, long period) {
        getScheduler().runTaskTimerAsynchronously(this, task, delay, period);
    }
}
