package cn.nukkit.permission;

import cn.nukkit.Server;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Permission {

    public final static String DEFAULT_OP = "op";
    public final static String DEFAULT_NOT_OP = "notop";
    public final static String DEFAULT_TRUE = "true";
    public final static String DEFAULT_FALSE = "false";

    public static final String DEFAULT_PERMISSION = DEFAULT_OP;

    public static String getByName(String value) {
        switch (value.toLowerCase()) {
            case "op":
            case "isop":
            case "operator":
            case "isoperator":
            case "admin":
            case "isadmin":
                return DEFAULT_OP;

            case "!op":
            case "notop":
            case "!operator":
            case "notoperator":
            case "!admin":
            case "notadmin":
                return DEFAULT_NOT_OP;

            case "true":
                return DEFAULT_TRUE;

            default:
                return DEFAULT_FALSE;
        }
    }

    private final String name;

    private String description;

    private final Object2BooleanMap<String> children;

    private String defaultValue;

    public Permission(String name) {
        this(name, null, null, new Object2BooleanOpenHashMap<>());
    }

    public Permission(String name, String description) {
        this(name, description, null, new Object2BooleanOpenHashMap<>());
    }

    public Permission(String name, String description, String defaultValue) {
        this(name, description, defaultValue, new Object2BooleanOpenHashMap<>());
    }

    public Permission(String name, String description, String defaultValue, Object2BooleanMap<String> children) {
        this.name = name;
        this.description = description != null ? description : "";
        this.defaultValue = defaultValue != null ? defaultValue : DEFAULT_PERMISSION;
        this.children = children;

        this.recalculatePermissibles();
    }

    public String getName() {
        return name;
    }

    public Object2BooleanMap<String> getChildren() {
        return children;
    }

    public String getDefault() {
        return defaultValue;
    }

    public void setDefault(String value) {
        if (!value.equals(this.defaultValue)) {
            this.defaultValue = value;
            this.recalculatePermissibles();
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Permissible> getPermissibles() {
        return Server.getInstance().getPluginManager().getPermissionSubscriptions(this.name);
    }

    public void recalculatePermissibles() {
        Set<Permissible> perms = this.getPermissibles();

        Server.getInstance().getPluginManager().recalculatePermissionDefaults(this);

        for (Permissible p : perms) {
            p.recalculatePermissions();
        }
    }

    public void addParent(Permission permission, boolean value) {
        this.getChildren().put(this.getName(), value);
        permission.recalculatePermissibles();
    }

    public Permission addParent(String name, boolean value) {
        Permission perm = Server.getInstance().getPluginManager().getPermission(name);
        if (perm == null) {
            perm = new Permission(name);
            Server.getInstance().getPluginManager().addPermission(perm);
        }

        this.addParent(perm, value);

        return perm;
    }

    public static List<Permission> loadPermissions(Map<String, Object> data) {
        return loadPermissions(data, DEFAULT_OP);
    }

    public static List<Permission> loadPermissions(Map<String, Object> data, String defaultValue) {
        List<Permission> result = new ObjectArrayList<>();
        if (data != null) {
            for (Map.Entry<String, Object> e : data.entrySet()) {
                String key = e.getKey();
                Map<String, Object> entry = (Map<String, Object>) e.getValue();
                result.add(loadPermission(key, entry, defaultValue, result));
            }
        }
        return result;
    }

    public static Permission loadPermission(String name, Map<String, Object> data) {
        return loadPermission(name, data, DEFAULT_OP, new ObjectArrayList<>());
    }

    public static Permission loadPermission(String name, Map<String, Object> data, String defaultValue) {
        return loadPermission(name, data, defaultValue, new ObjectArrayList<>());
    }

    public static Permission loadPermission(String name, Map<String, Object> data, String defaultValue, List<Permission> output) {
        String desc = null;
        Object2BooleanMap<String> children = new Object2BooleanOpenHashMap<>();
        Object def = data.get("default");
        if (def != null) {
            String value = Permission.getByName(String.valueOf(def));
            if (value != null) {
                defaultValue = value;
            } else {
                throw new IllegalStateException("'default' key contained unknown value");
            }
        }

        Object child = data.get("children");
        if (child != null) {
            if (child instanceof Map) {
                for (Map.Entry<String, Object> entry : ((Map<String, Object>) data.get("children")).entrySet()) {
                    String k = entry.getKey();
                    Object v = entry.getValue();
                    if (v instanceof Map) {
                        Permission permission = loadPermission(k, (Map<String, Object>) v, defaultValue, output);
                        if (permission != null) {
                            output.add(permission);
                        }
                    }
                    children.put(k, true);
                }
            } else {
                throw new IllegalStateException("'children' key is of wrong type");
            }
        }

        Object description = data.get("description");
        if (description != null) {
            desc = (String) description;
        }

        return new Permission(name, desc, defaultValue, children);
    }

}
