package cn.nukkit.plugin;

import cn.nukkit.Server;
import cn.nukkit.event.plugin.PluginDisableEvent;
import cn.nukkit.event.plugin.PluginEnableEvent;
import cn.nukkit.utils.PluginException;
import cn.nukkit.utils.Utils;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * Created by Nukkit Team.
 */
@Log4j2
public class JavaPluginLoader implements PluginLoader {

    private static final Pattern REGEX = Pattern.compile("^.+\\.jar$");

    private final Server server;
    private final Pattern[] fileFilters = new Pattern[]{REGEX};
    private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();
    private final List<PluginClassLoader> classLoaders = new CopyOnWriteArrayList<>();

    public JavaPluginLoader(Server server) {
        this.server = server;
    }

    @Override
    public Plugin loadPlugin(File file) throws Exception {
        PluginDescription description = this.getPluginDescription(file);
        if (description != null) {
            log.info(this.server.getLanguage().translate("nukkit.plugin.load", description.getFullName()));
            File dataFolder = new File(file.getParentFile(), description.getName());
            if (dataFolder.exists() && !dataFolder.isDirectory()) {
                throw new IllegalStateException("Projected dataFolder '" + dataFolder + "' for " + description.getName() + " exists and is not a directory");
            }

            String className = description.getMain();
            PluginClassLoader classLoader = new PluginClassLoader(this, this.getClass().getClassLoader(), file);
            this.classLoaders.add(classLoader);
            MethodHandles.Lookup lookup = (MethodHandles.Lookup) classLoader.loadClass("NukkitPluginLookup").getField("LOOKUP").get(null);
            PluginBase plugin;
            try {
                Class<?> javaClass = Class.forName(className, true, classLoader);

                try {
                    Class<? extends PluginBase> pluginClass = javaClass.asSubclass(PluginBase.class);

                    plugin = pluginClass.newInstance();
                    this.initPlugin(plugin, description, dataFolder, file, lookup);
                    plugin.onLoad();

                    return plugin;
                } catch (ClassCastException e) {
                    throw new PluginException("main class `" + description.getMain() + "' does not extend PluginBase");
                } catch (InstantiationException | IllegalAccessException e) {
                    Server.getInstance().getLogger().logException(e);
                }

            } catch (ClassNotFoundException e) {
                throw new PluginException("Couldn't load plugin " + description.getName() + ": main class not found");
            }
        }

        return null;
    }

    @Override
    public Plugin loadPlugin(String filename) throws Exception {
        return this.loadPlugin(new File(filename));
    }

    @Override
    public PluginDescription getPluginDescription(File file) {
        try (JarFile jar = new JarFile(file)) {
            JarEntry entry = jar.getJarEntry("nukkit.yml");
            if (entry == null) {
                entry = jar.getJarEntry("plugin.yml");
                if (entry == null) {
                    return null;
                }
            }
            try (InputStream stream = jar.getInputStream(entry)) {
                return new PluginDescription(Utils.readFile(stream));
            }
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public PluginDescription getPluginDescription(String filename) {
        return this.getPluginDescription(new File(filename));
    }

    @Override
    public Pattern[] getPluginFilters() {
        return this.fileFilters.clone();
    }

    private synchronized void initPlugin(PluginBase plugin, PluginDescription description, File dataFolder, File file, MethodHandles.Lookup lookup) {
        plugin.init(this, this.server, description, dataFolder, file, lookup);
    }

    @Override
    public void enablePlugin(Plugin plugin) {
        if (plugin instanceof PluginBase && !plugin.isEnabled()) {
            log.info(this.server.getLanguage().translate("nukkit.plugin.enable", plugin.getDescription().getFullName()));

            PluginClassLoader pluginLoader = (PluginClassLoader) ((PluginBase) plugin).getClassLoader();

            if (!this.classLoaders.contains(pluginLoader)) {
                this.classLoaders.add(pluginLoader);
                log.warn("Enabled plugin with unregistered PluginClassLoader " + plugin.getDescription().getFullName());
            }

            ((PluginBase) plugin).setEnabled(true);

            this.server.getPluginManager().callEvent(new PluginEnableEvent(plugin));
        }
    }

    @Override
    public void disablePlugin(Plugin plugin) {
        if (plugin instanceof PluginBase && plugin.isEnabled()) {
            log.info(this.server.getLanguage().translate("nukkit.plugin.disable", plugin.getDescription().getFullName()));

            this.server.getServiceManager().cancel(plugin);

            this.server.getPluginManager().callEvent(new PluginDisableEvent(plugin));

            ((PluginBase) plugin).setEnabled(false);

            ClassLoader cloader = ((PluginBase) plugin).getClassLoader();

            if (cloader instanceof PluginClassLoader) {
                PluginClassLoader loader = (PluginClassLoader) cloader;
                this.classLoaders.remove(loader);

                Set<String> names = loader.getClasses();

                for (String name : names) {
                    removeClass(name);
                }
            }
        }
    }

    Class<?> getClassByName(final String name) {
        Class<?> cachedClass = classes.get(name);

        if (cachedClass != null) {
            return cachedClass;
        } else {
            for (PluginClassLoader loader : this.classLoaders) {

                try {
                    cachedClass = loader.findClass(name, false);
                } catch (ClassNotFoundException e) {
                    //ignore
                }
                if (cachedClass != null) {
                    return cachedClass;
                }
            }
        }
        return null;
    }

    void setClass(final String name, final Class<?> clazz) {
        classes.putIfAbsent(name, clazz);
    }

    private void removeClass(String name) {
        classes.remove(name);
    }
}
