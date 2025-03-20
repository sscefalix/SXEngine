package ru.sscefalix.sxEngine.api.permission;

import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import ru.sscefalix.sxEngine.SXEngine;
import ru.sscefalix.sxEngine.api.command.abc.AbstractCommand;
import ru.sscefalix.sxEngine.api.command.abc.AbstractMainCommand;
import ru.sscefalix.sxEngine.api.command.abc.AbstractSubCommand;
import ru.sscefalix.sxEngine.api.manager.AbstractManager;

import java.util.HashSet;
import java.util.Set;

public class PermissionManager<P extends SXEngine<P>> extends AbstractManager<P> {
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
            Permission permissionObj = new Permission(permission);

            if (getPlugin().getPluginManager().getPermission(permission) == null) {
                getPlugin().getPluginManager().addPermission(permissionObj);
            }

            registeredPermissions.add(permission);
        }
    }

    public String commandPermission(@NotNull AbstractCommand<P> abstractCommand) {
        String permission = getPlugin().getName().toLowerCase() + ".";

        if (abstractCommand instanceof AbstractMainCommand<P> command) {
            permission = permission + "commands." + command.getName();
        } else if (abstractCommand instanceof AbstractSubCommand<P> subCommand) {
            permission = permission + "commands." + subCommand.getParent().getName() + "." + subCommand.getName();
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
