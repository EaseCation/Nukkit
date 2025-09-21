package cn.nukkit.level.generator;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.IdentityHashMap;
import java.util.Map;

import static cn.nukkit.level.generator.GeneratorID.*;

public final class Generators {
    private static final Map<String, GeneratorEntry> BY_NAME = new Object2ObjectOpenHashMap<>();
    private static final Int2ObjectMap<GeneratorEntry> BY_TYPE = new Int2ObjectOpenHashMap<>();
    private static final Map<Class<? extends Generator>, GeneratorEntry> BY_CLASS = new IdentityHashMap<>();

    public static void registerBuiltinGenerators() {
        addGenerator(Old.class, "legacy", LEGACY);
        addGenerator(Old.class, "old", LEGACY);
        addGenerator(Normal.class, "overworld", OVERWORLD);
        addGenerator(Normal.class, "infinite", OVERWORLD);
        addGenerator(Normal.class, "normal", OVERWORLD);
        addGenerator(Normal.class, "default", OVERWORLD);
        addGenerator(Flat.class, "flat", FLAT);
        addGenerator(LegacyNether.class, "nether", NETHER);
        addGenerator(LegacyNether.class, "hell", NETHER);
        addGenerator(TheEnd.class, "the_end", THE_END);
        addGenerator(TheEnd.class, "end", THE_END);
        addGenerator(Void.class, "void", VOID);
    }

    public static boolean addGenerator(Class<? extends Generator> clazz, String name, int type) {
        if (clazz == null) {
            return false;
        }
        name = name.toLowerCase();
        GeneratorEntry entry = new GeneratorEntry(clazz, name, type);

        if (BY_NAME.putIfAbsent(name, entry) != null) {
            return false;
        }
        BY_TYPE.putIfAbsent(type, entry);
        BY_CLASS.putIfAbsent(clazz, entry);
        return true;
    }

    public static Class<? extends Generator> getGenerator(String name) {
        name = name.toLowerCase();

        GeneratorEntry entry = BY_NAME.get(name);
        if (entry == null) {
            return Normal.class;
        }
        return entry.clazz;
    }

    public static Class<? extends Generator> getGenerator(int type) {
        GeneratorEntry entry = BY_TYPE.get(type);
        if (entry == null) {
            return Normal.class;
        }
        return entry.clazz;
    }

    public static String getGeneratorName(Class<? extends Generator> clazz) {
        GeneratorEntry entry = BY_CLASS.get(clazz);
        if (entry == null) {
            return "unknown";
        }
        return entry.name;
    }

    public static String getGeneratorName(int type) {
        GeneratorEntry entry = BY_TYPE.get(type);
        if (entry == null) {
            return "unknown";
        }
        return entry.name;
    }

    public static int getGeneratorType(Class<? extends Generator> clazz) {
        GeneratorEntry entry = BY_CLASS.get(clazz);
        if (entry == null) {
            return OVERWORLD;
        }
        return entry.type;
    }

    public static int getGeneratorType(String name) {
        name = name.toLowerCase();

        GeneratorEntry entry = BY_NAME.get(name);
        if (entry == null) {
            return OVERWORLD;
        }
        return entry.type;
    }

    public static Map<String, GeneratorEntry> getGenerators() {
        return BY_NAME;
    }

    @AllArgsConstructor
    @Value
    private static class GeneratorEntry {
        Class<? extends Generator> clazz;
        String name;
        int type;
    }

    private Generators() {
        throw new IllegalStateException();
    }
}
