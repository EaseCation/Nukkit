package cn.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.SimpleCommandMap;
import cn.nukkit.event.*;
import cn.nukkit.permission.Permissible;
import cn.nukkit.permission.Permission;
import cn.nukkit.utils.EventException;
import cn.nukkit.utils.PluginException;
import cn.nukkit.utils.Utils;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static cn.nukkit.SharedConstants.*;

/**
 * @author MagicDroidX
 */
@Log4j2
public class PluginManager {

    private final Server server;

    private final SimpleCommandMap commandMap;

    protected final Map<String, Plugin> plugins = new Object2ObjectLinkedOpenHashMap<>();

    protected final Map<String, Permission> permissions = new Object2ObjectOpenHashMap<>();

    protected final Map<String, Permission> defaultPerms = new Object2ObjectOpenHashMap<>();

    protected final Map<String, Permission> defaultPermsOp = new Object2ObjectOpenHashMap<>();

    protected final Map<String, Set<Permissible>> permSubs = new Object2ObjectOpenHashMap<>();

    protected final Set<Permissible> defSubs = Collections.newSetFromMap(new WeakHashMap<>());

    protected final Set<Permissible> defSubsOp = Collections.newSetFromMap(new WeakHashMap<>());

    protected final Map<String, PluginLoader> fileAssociations = new Object2ObjectOpenHashMap<>();

    public PluginManager(Server server, SimpleCommandMap commandMap) {
        this.server = server;
        this.commandMap = commandMap;
    }

    public Plugin getPlugin(String name) {
        return this.plugins.get(name);
    }

    public boolean registerInterface(Class<? extends PluginLoader> loaderClass) {
        if (loaderClass != null) {
            try {
                Constructor<?> constructor = loaderClass.getDeclaredConstructor(Server.class);
                constructor.setAccessible(true);
                this.fileAssociations.put(loaderClass.getName(), (PluginLoader) constructor.newInstance(this.server));
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public Map<String, Plugin> getPlugins() {
        return plugins;
    }

    public Plugin loadPlugin(String path) {
        return this.loadPlugin(path, null);
    }

    public Plugin loadPlugin(File file) {
        return this.loadPlugin(file, null);
    }

    public Plugin loadPlugin(String path, Map<String, PluginLoader> loaders) {
        return this.loadPlugin(new File(path), loaders);
    }

    public Plugin loadPlugin(File file, Map<String, PluginLoader> loaders) {
        for (PluginLoader loader : (loaders == null ? this.fileAssociations : loaders).values()) {
            for (Pattern pattern : loader.getPluginFilters()) {
                if (pattern.matcher(file.getName()).matches()) {
                    PluginDescription description = loader.getPluginDescription(file);
                    if (description != null) {
                        try {
                            Plugin plugin = loader.loadPlugin(file);
                            if (plugin != null) {
                                this.plugins.put(plugin.getDescription().getName(), plugin);

                                List<PluginCommand<Plugin>> pluginCommands = this.parseYamlCommands(plugin);

                                if (!pluginCommands.isEmpty()) {
                                    this.commandMap.registerAll(plugin.getDescription().getName(), pluginCommands);
                                }

                                return plugin;
                            }
                        } catch (Exception e) {
                            log.fatal("Could not load plugin", e);
                            return null;
                        }
                    }
                }
            }
        }

        return null;
    }

    public Map<String, Plugin> loadPlugins(String dictionary) {
        return this.loadPlugins(new File(dictionary));
    }

    public Map<String, Plugin> loadPlugins(File dictionary) {
        return this.loadPlugins(dictionary, null);
    }

    public Map<String, Plugin> loadPlugins(String dictionary, List<String> newLoaders) {
        return this.loadPlugins(new File(dictionary), newLoaders);
    }

    public Map<String, Plugin> loadPlugins(File dictionary, List<String> newLoaders) {
        return this.loadPlugins(dictionary, newLoaders, false);
    }

    public Map<String, Plugin> loadPlugins(File dictionary, List<String> newLoaders, boolean includeDir) {
        if (dictionary.isDirectory()) {
            Map<String, File> plugins = new Object2ObjectLinkedOpenHashMap<>();
            Map<String, Plugin> loadedPlugins = new Object2ObjectLinkedOpenHashMap<>();
            Map<String, List<String>> dependencies = new Object2ObjectLinkedOpenHashMap<>();
            Map<String, List<String>> softDependencies = new Object2ObjectLinkedOpenHashMap<>();
            Map<String, PluginLoader> loaders = new Object2ObjectLinkedOpenHashMap<>();
            if (newLoaders != null) {
                for (String key : newLoaders) {
                    PluginLoader loader = this.fileAssociations.get(key);
                    if (loader != null) {
                        loaders.put(key, loader);
                    }
                }
            } else {
                loaders = this.fileAssociations;
            }

            for (final PluginLoader loader : loaders.values()) {
                for (File file : dictionary.listFiles((dir, name) -> {
                    for (Pattern pattern : loader.getPluginFilters()) {
                        if (pattern.matcher(name).matches()) {
                            return true;
                        }
                    }
                    return false;
                })) {
                    if (file.isDirectory() && !includeDir) {
                        continue;
                    }
                    try {
                        PluginDescription description = loader.getPluginDescription(file);
                        if (description != null) {
                            String name = description.getName();

                            if (plugins.containsKey(name) || this.getPlugin(name) != null) {
                                log.error(this.server.getLanguage().translate("nukkit.plugin.duplicateError", name));
                                continue;
                            }

                            boolean compatible = false;

                            for (String version : description.getCompatibleAPIs()) {

                                try {
                                    //Check the format: majorVersion.minorVersion.patch
                                    if (!Pattern.matches("^[0-9]+\\.[0-9]+\\.[0-9]+$", version)) {
                                        throw new IllegalArgumentException();
                                    }
                                } catch (NullPointerException | IllegalArgumentException e) {
                                    log.error(this.server.getLanguage().translate("nukkit.plugin.loadError", name, "Wrong API format"));
                                    continue;
                                }

                                String[] versionArray = version.split("\\.");
                                String[] apiVersion = this.server.getApiVersion().split("\\.");

                                //Completely different API version
                                if (Integer.parseInt(versionArray[0]) != Integer.parseInt(apiVersion[0])) {
                                    continue;
                                }

                                //If the plugin requires new API features, being backwards compatible
                                if (Integer.parseInt(versionArray[1]) > Integer.parseInt(apiVersion[1])) {
                                    continue;
                                }

                                compatible = true;
                                break;
                            }

                            if (!compatible) {
                                log.error(this.server.getLanguage().translate("nukkit.plugin.loadError", name, "%nukkit.plugin.incompatibleAPI"));
                            }

                            plugins.put(name, file);

                            softDependencies.put(name, description.getSoftDepend());

                            dependencies.put(name, description.getDepend());

                            for (String before : description.getLoadBefore()) {
                                List<String> deps = softDependencies.get(before);
                                if (deps != null) {
                                    deps.add(name);
                                } else {
                                    List<String> list = new ObjectArrayList<>();
                                    list.add(name);
                                    softDependencies.put(before, list);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error(this.server.getLanguage().translate("nukkit.plugin.fileError", file.getName(), dictionary.toString()), e);
                    }
                }
            }

            while (!plugins.isEmpty()) {
                boolean missingDependency = true;
                for (String name : new ObjectArrayList<>(plugins.keySet())) {
                    File file = plugins.get(name);
                    List<String> deps = dependencies.get(name);
                    if (deps != null) {
                        for (String dependency : new ObjectArrayList<>(deps)) {
                            if (loadedPlugins.containsKey(dependency) || this.getPlugin(dependency) != null) {
                                deps.remove(dependency);
                            } else if (!plugins.containsKey(dependency)) {
                                log.fatal(this.server.getLanguage().translate("nukkit.plugin.loadError", name, "%nukkit.plugin.unknownDependency") + ": " + dependency);
                                break;
                            }
                        }

                        if (deps.isEmpty()) {
                            dependencies.remove(name);
                        }
                    }

                    List<String> softDeps = softDependencies.get(name);
                    if (softDeps != null) {
                        softDeps.removeIf(dependency ->
                                loadedPlugins.containsKey(dependency) || this.getPlugin(dependency) != null);

                        if (softDeps.isEmpty()) {
                            softDependencies.remove(name);
                        }
                    }

                    if (!dependencies.containsKey(name) && !softDependencies.containsKey(name)) {
                        plugins.remove(name);
                        missingDependency = false;
                        Plugin plugin = this.loadPlugin(file, loaders);
                        if (plugin != null) {
                            loadedPlugins.put(name, plugin);
                        } else {
                            log.fatal(this.server.getLanguage().translate("nukkit.plugin.genericLoadError", name));
                        }
                    }
                }

                if (missingDependency) {
                    for (String name : new ObjectArrayList<>(plugins.keySet())) {
                        File file = plugins.get(name);
                        if (!dependencies.containsKey(name)) {
                            softDependencies.remove(name);
                            plugins.remove(name);
                            missingDependency = false;
                            Plugin plugin = this.loadPlugin(file, loaders);
                            if (plugin != null) {
                                loadedPlugins.put(name, plugin);
                            } else {
                                log.fatal(this.server.getLanguage().translate("nukkit.plugin.genericLoadError", name));
                            }
                        }
                    }

                    if (missingDependency) {
                        for (String name : plugins.keySet()) {
                            log.fatal(this.server.getLanguage().translate("nukkit.plugin.loadError", name, "%nukkit.plugin.circularDependency"));
                        }
                        plugins.clear();
                    }
                }
            }

            return loadedPlugins;
        } else {
            return new Object2ObjectOpenHashMap<>();
        }
    }

    public Permission getPermission(String name) {
        return this.permissions.get(name);
    }

    public boolean addPermission(Permission permission) {
        if (this.permissions.putIfAbsent(permission.getName(), permission) == null) {
            this.calculatePermissionDefault(permission);

            return true;
        }

        return false;
    }

    public void removePermission(String name) {
        this.permissions.remove(name);
    }

    public void removePermission(Permission permission) {
        this.removePermission(permission.getName());
    }

    public Map<String, Permission> getDefaultPermissions(boolean op) {
        if (op) {
            return this.defaultPermsOp;
        } else {
            return this.defaultPerms;
        }
    }

    public void recalculatePermissionDefaults(Permission permission) {
        if (this.permissions.containsKey(permission.getName())) {
            this.defaultPermsOp.remove(permission.getName());
            this.defaultPerms.remove(permission.getName());
            this.calculatePermissionDefault(permission);
        }
    }

    private void calculatePermissionDefault(Permission permission) {
        if (permission.getDefault().equals(Permission.DEFAULT_OP) || permission.getDefault().equals(Permission.DEFAULT_TRUE)) {
            this.defaultPermsOp.put(permission.getName(), permission);
            this.dirtyPermissibles(true);
        }

        if (permission.getDefault().equals(Permission.DEFAULT_NOT_OP) || permission.getDefault().equals(Permission.DEFAULT_TRUE)) {
            this.defaultPerms.put(permission.getName(), permission);
            this.dirtyPermissibles(false);
        }
    }

    private void dirtyPermissibles(boolean op) {
        for (Permissible p : this.getDefaultPermSubscriptions(op)) {
            p.recalculatePermissions();
        }
    }

    public void subscribeToPermission(String permission, Permissible permissible) {
        Set<Permissible> subs = this.permSubs.computeIfAbsent(permission, key -> Collections.newSetFromMap(new WeakHashMap<>()));
        subs.add(permissible);
    }

    public void unsubscribeFromPermission(String permission, Permissible permissible) {
        Set<Permissible> subs = this.permSubs.get(permission);
        if (subs != null) {
            subs.remove(permissible);
            if (subs.isEmpty()) {
                this.permSubs.remove(permission);
            }
        }
    }

    public Set<Permissible> getPermissionSubscriptions(String permission) {
        Set<Permissible> subs = this.permSubs.get(permission);
        if (subs != null) {
            return new ObjectOpenHashSet<>(subs);
        }
        return new ObjectOpenHashSet<>();
    }

    public void subscribeToDefaultPerms(boolean op, Permissible permissible) {
        if (op) {
            this.defSubsOp.add(permissible);
        } else {
            this.defSubs.add(permissible);
        }
    }

    public void unsubscribeFromDefaultPerms(boolean op, Permissible permissible) {
        if (op) {
            this.defSubsOp.remove(permissible);
        } else {
            this.defSubs.remove(permissible);
        }
    }

    public Set<Permissible> getDefaultPermSubscriptions(boolean op) {
        if (op) {
            return new ObjectOpenHashSet<>(this.defSubsOp);
        } else {
            return new ObjectOpenHashSet<>(this.defSubs);
        }
    }

    public Map<String, Permission> getPermissions() {
        return permissions;
    }

    public boolean isPluginEnabled(Plugin plugin) {
        if (plugin != null && this.plugins.containsKey(plugin.getDescription().getName())) {
            return plugin.isEnabled();
        } else {
            return false;
        }
    }

    public void enablePlugin(Plugin plugin) {
        if (!plugin.isEnabled()) {
            try {
                for (Permission permission : plugin.getDescription().getPermissions()) {
                    this.addPermission(permission);
                }
                plugin.getPluginLoader().enablePlugin(plugin);
            } catch (Throwable e) {
                this.server.getLogger().logException(e);
                this.disablePlugin(plugin);
            }
        }
    }

    protected List<PluginCommand<Plugin>> parseYamlCommands(Plugin plugin) {
        List<PluginCommand<Plugin>> pluginCmds = new ObjectArrayList<>();

        for (Map.Entry<String, Object> entry : plugin.getDescription().getCommands().entrySet()) {
            String key = entry.getKey();
            Object data = entry.getValue();
            if (key.contains(":")) {
                log.fatal(this.server.getLanguage().translate("nukkit.plugin.commandError", key, plugin.getDescription().getFullName()));
                continue;
            }
            if (data instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) data;
                PluginCommand<Plugin> newCmd = new PluginCommand<>(key, plugin);

                Object description = map.get("description");
                if (description != null) {
                    newCmd.setDescription((String) description);
                }

                Object usage = map.get("usage");
                if (usage != null) {
                    newCmd.setUsage((String) usage);
                }

                Object aliases = map.get("aliases");
                if (aliases != null) {
                    if (aliases instanceof List) {
                        List<String> aliasList = new ObjectArrayList<>();
                        for (String alias : (List<String>) aliases) {
                            if (alias.contains(":")) {
                                log.fatal(this.server.getLanguage().translate("nukkit.plugin.aliasError", alias, plugin.getDescription().getFullName()));
                                continue;
                            }
                            aliasList.add(alias);
                        }

                        newCmd.setAliases(aliasList.toArray(new String[0]));
                    }
                }

                Object permission = map.get("permission");
                if (permission != null) {
                    newCmd.setPermission((String) permission);
                }

                Object permissionMessage = map.get("permission-message");
                if (permissionMessage != null) {
                    newCmd.setPermissionMessage((String) permissionMessage);
                }

                pluginCmds.add(newCmd);
            }
        }

        return pluginCmds;
    }

    public void disablePlugins() {
        ListIterator<Plugin> plugins = new ObjectArrayList<>(this.getPlugins().values()).listIterator(this.getPlugins().size());

        while (plugins.hasPrevious()) {
            this.disablePlugin(plugins.previous());
        }
    }

    public void disablePlugin(Plugin plugin) {
        if (plugin.isEnabled()) {
            try {
                plugin.getPluginLoader().disablePlugin(plugin);
            } catch (Exception e) {
                this.server.getLogger().logException(e);
            }

            this.server.getScheduler().cancelTask(plugin);
            HandlerList.unregisterAll(plugin);
            for (Permission permission : plugin.getDescription().getPermissions()) {
                this.removePermission(permission);
            }
        }
    }

    public void clearPlugins() {
        this.disablePlugins();
        this.plugins.clear();
        this.fileAssociations.clear();
        this.permissions.clear();
        this.defaultPerms.clear();
        this.defaultPermsOp.clear();
    }

    public void callEvent(Event event) {
        try {
            for (RegisteredListener registration : getEventListeners(event.getClass()).getRegisteredListeners()) {
                if (!registration.getPlugin().isEnabled()) {
                    continue;
                }

                try {
                    registration.callEvent(event);
                } catch (Exception e) {
                    log.fatal(this.server.getLanguage().translate("nukkit.plugin.eventError", event.getEventName(), registration.getPlugin().getDescription().getFullName(), registration.getListener().getClass().getName()), e);
                }
            }
        } catch (IllegalAccessException e) {
            this.server.getLogger().logException(e);
        }
    }

    public void registerEvents(Listener listener, Plugin plugin) {
        if (!plugin.isEnabled()) {
            throw new PluginException("Plugin attempted to register " + listener.getClass().getName() + " while not enabled");
        }

        Set<Method> methods;
        try {
            Method[] publicMethods = listener.getClass().getMethods();
            Method[] privateMethods = listener.getClass().getDeclaredMethods();
            methods = new ObjectOpenHashSet<>(publicMethods.length + privateMethods.length);
            Collections.addAll(methods, publicMethods);
            Collections.addAll(methods, privateMethods);
        } catch (NoClassDefFoundError e) {
            plugin.getLogger().error("Plugin " + plugin.getDescription().getFullName() + " has failed to register events for " + listener.getClass() + " because " + e.getMessage() + " does not exist.", e);
            return;
        }

        for (final Method method : methods) {
            final EventHandler eh = method.getAnnotation(EventHandler.class);
            if (eh == null) continue;
            if (method.isBridge() || method.isSynthetic()) {
                continue;
            }
            final Class<?> checkClass;

            if (method.getParameterTypes().length != 1 || !Event.class.isAssignableFrom(checkClass = method.getParameterTypes()[0])) {
                plugin.getLogger().error(plugin.getDescription().getFullName() + " attempted to register an invalid EventHandler method signature \"" + method.toGenericString() + "\" in " + listener.getClass());
                continue;
            }

            final Class<? extends Event> eventClass = checkClass.asSubclass(Event.class);
            method.setAccessible(true);

            for (Class<?> clazz = eventClass; Event.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
                // This loop checks for extending deprecated events
                if (clazz.getAnnotation(Deprecated.class) != null) {
                    if (Boolean.parseBoolean(String.valueOf(this.server.getConfig("settings.deprecated-verbose", true)))) {
                        log.warn(this.server.getLanguage().translate("nukkit.plugin.deprecatedEvent", plugin.getName(), clazz.getName(), listener.getClass().getName() + "." + method.getName() + "()"));
                    }
                    break;
                }
            }

            EventExecutor executor;
            if (USE_FUNCTION_EVENT_EXECUTOR) {
                MethodHandles.Lookup lookup = plugin.getMethodHandlesLookup();
                try {
                    CallSite site = LambdaMetafactory.metafactory(lookup, "accept",
                            MethodType.methodType(Consumer.class, listener.getClass()),
                            MethodType.methodType(void.class, Object.class),
                            lookup.unreflect(method),
                            MethodType.methodType(void.class, eventClass));
                    Consumer<Event> consumer = (Consumer<Event>) site.getTarget().invoke(listener);
                    executor = (listen, event) -> {
                        try {
                            if (!eventClass.isAssignableFrom(event.getClass())) {
                                return;
                            }
                            consumer.accept(event);
                        } catch (Throwable t) {
                            throw new EventException(t);
                        }
                    };
                } catch (Throwable e) {
                    plugin.getLogger().debug("FunctionEventExecutor is not available, fallback to ReflectionEventExecutor: " + method, e);
                    executor = new MethodEventExecutor(method, eventClass);
                }
            } else {
                executor = new MethodEventExecutor(method, eventClass);
            }

            this.registerEvent(eventClass, listener, eh.priority(), executor, plugin, eh.ignoreCancelled());
        }
    }

    public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority, EventExecutor executor, Plugin plugin) throws PluginException {
        this.registerEvent(event, listener, priority, executor, plugin, false);
    }

    public void registerEvent(Class<? extends Event> event, Listener listener, EventPriority priority, EventExecutor executor, Plugin plugin, boolean ignoreCancelled) throws PluginException {
        if (!plugin.isEnabled()) {
            throw new PluginException("Plugin attempted to register " + event + " while not enabled");
        }

        try {
            this.getEventListeners(event).register(new RegisteredListener(listener, executor, priority, plugin, ignoreCancelled));
        } catch (IllegalAccessException e) {
            Server.getInstance().getLogger().logException(e);
        }
    }

    private HandlerList getEventListeners(Class<? extends Event> type) throws IllegalAccessException {
        try {
            Method method = getRegistrationClass(type).getDeclaredMethod("getHandlers");
            method.setAccessible(true);
            return (HandlerList) method.invoke(null);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("getHandlers method in " + type.getName() + " was not static!");
        } catch (Exception e) {
            throw new IllegalAccessException(Utils.getExceptionMessage(e));
        }
    }

    private Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) throws IllegalAccessException {
        try {
            clazz.getDeclaredMethod("getHandlers");
            return clazz;
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null
                    && !clazz.getSuperclass().equals(Event.class)
                    && Event.class.isAssignableFrom(clazz.getSuperclass())) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            } else {
                throw new IllegalAccessException("Unable to find handler list for event " + clazz.getName() + ". Static getHandlers method required!");
            }
        }
    }
}
