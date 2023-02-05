package cn.nukkit.plugin;

import cn.nukkit.Server;
import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PluginClassLoader extends URLClassLoader {

    private final JavaPluginLoader loader;

    private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public PluginClassLoader(JavaPluginLoader loader, ClassLoader parent, File file) throws MalformedURLException {
        super(new URL[]{file.toURI().toURL()}, parent);
        this.loader = loader;

        defineClass("NukkitPluginLookup", PLUGIN_LOOKUP_CLASS, 0, PLUGIN_LOOKUP_CLASS.length);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return this.findClass(name, true);

    }

    protected Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
        if (name.startsWith("cn.nukkit.")) {
            throw new ClassNotFoundException(name);
        }
        Class<?> result = classes.get(name);

        if (result == null) {
            if (checkGlobal) {
                result = loader.getClassByName(name);
            }

            if (result == null) {
                result = super.findClass(name);

                if (result != null) {
                    loader.setClass(name, result);
                }

                classes.put(name, result);
            }
        }

        return result;
    }

    Set<String> getClasses() {
        return classes.keySet();
    }

    /**
     * <code>
     * public interface NukkitPluginLookup {
     *     MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
     * }
     * </code>
     */
    private static final byte[] PLUGIN_LOOKUP_CLASS;

    static {
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("NukkitPluginLookup.class.bin")) {
            PLUGIN_LOOKUP_CLASS = ByteStreams.toByteArray(stream);
        } catch (Exception e) {
            throw new AssertionError("Unable to load NukkitPluginLookup.class", e);
        }
    }
}
