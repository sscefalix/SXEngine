package ru.sscefalix.sEngineX.api.permission;

import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import ru.sscefalix.sEngineX.SEngine;
import ru.sscefalix.sEngineX.api.command.AbstractCommand;
import ru.sscefalix.sEngineX.api.command.AbstractMainCommand;
import ru.sscefalix.sEngineX.api.command.AbstractSubCommand;
import ru.sscefalix.sEngineX.api.manager.AbstractManager;

import java.util.HashSet;
import java.util.Set;

public class PermissionManager<P extends SEngine<P>> extends AbstractManager<P> {
    private final Set<String> registeredPermissions;

    public PermissionManager(P plugin) {
        super(plugin);

        registeredPermissions = new HashSet<>();
    }

    @Override
    protected void onSetup() {

    }

    @Override
    protected void onShutdown() {

    }

    public void registerPermission(String permission) {
        if (!registeredPermissions.contains(permission)) {
            getPlugin().getPluginManager().addPermission(new Permission(permission));
            registeredPermissions.add(permission);
        }
    }

    public String commandPermission(@NotNull AbstractCommand<P> abstractCommand) {
        String permission = getPlugin().getName().toLowerCase() + ".";

        if (abstractCommand instanceof AbstractMainCommand<P> command) {
            permission = permission + "commands." + command.getName();
        } else if (abstractCommand instanceof AbstractSubCommand<P> subCommand) {
            permission = "commands." + subCommand.getParent().getName() + "." + subCommand.getName();
        } else {
            return null;
        }

        registerPermission(permission);
        return permission;
    }

    public String pluginPermission(String permission) {
        String perm = getPlugin().getName().toLowerCase() + "." + permission;

        registerPermission(perm);
        return perm;
    }
}
