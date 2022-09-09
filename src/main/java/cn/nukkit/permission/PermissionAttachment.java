package cn.nukkit.permission;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.PluginException;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;

import java.util.List;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PermissionAttachment {

    private PermissionRemovedExecutor removed = null;

    private final Object2BooleanMap<String> permissions = new Object2BooleanOpenHashMap<>();

    private final Permissible permissible;

    private final Plugin plugin;

    public PermissionAttachment(Plugin plugin, Permissible permissible) {
        if (!plugin.isEnabled()) {
            throw new PluginException("Plugin " + plugin.getDescription().getName() + " is disabled");
        }
        this.permissible = permissible;
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setRemovalCallback(PermissionRemovedExecutor executor) {
        this.removed = executor;
    }

    public PermissionRemovedExecutor getRemovalCallback() {
        return removed;
    }

    public Object2BooleanMap<String> getPermissions() {
        return permissions;
    }

    public void clearPermissions() {
        this.permissions.clear();
        this.permissible.recalculatePermissions();
    }

    public void setPermissions(Map<String, Boolean> permissions) {
        for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
            String key = entry.getKey();
            boolean value = entry.getValue();
            this.permissions.put(key, value);
        }
        this.permissible.recalculatePermissions();
    }

    public void unsetPermissions(List<String> permissions) {
        for (String node : permissions) {
            this.permissions.removeBoolean(node);
        }
        this.permissible.recalculatePermissions();
    }

    public void setPermission(Permission permission, boolean value) {
        this.setPermission(permission.getName(), value);
    }

    public void setPermission(String name, boolean value) {
        if (this.permissions.containsKey(name)) {
            if (this.permissions.getBoolean(name) == value) {
                return;
            }
            this.permissions.removeBoolean(name);
        }
        this.permissions.put(name, value);
        this.permissible.recalculatePermissions();
    }

    public void unsetPermission(Permission permission, boolean value) {
        this.unsetPermission(permission.getName(), value);
    }

    public void unsetPermission(String name, boolean value) {
        if (this.permissions.containsKey(name)) {
            this.permissions.removeBoolean(name);
            this.permissible.recalculatePermissions();
        }
    }

    public void remove() {
        this.permissible.removeAttachment(this);
    }

}
