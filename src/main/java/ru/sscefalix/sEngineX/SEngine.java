package ru.sscefalix.sEngineX;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import ru.sscefalix.sEngineX.commands.CommandManager;

import java.util.function.Consumer;

public abstract class SEngine<P extends SEngine<P>> extends JavaPlugin {
    protected CommandManager<P> commandManager;

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
        this.commandManager = new CommandManager<>(self());
        this.commandManager.setup();
    }

    private void unloadManagers() {
        if (this.commandManager != null) {
            this.commandManager.shutdown();
        }
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
