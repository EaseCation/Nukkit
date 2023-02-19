package cn.nukkit.level.format;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.LevelCreationOptions;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.format.anvil.Chunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.leveldb.LevelDB;
import cn.nukkit.level.format.leveldb.LevelDbChunk;
import cn.nukkit.level.format.mcregion.McRegion;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Builder;
import lombok.Value;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class LevelProviderManager {
    public static final LevelProviderHandle DEFAULT;
    public static final LevelProviderHandle LEVELDB;
    public static final LevelProviderHandle ANVIL;
    public static final LevelProviderHandle MCREGION;

    private static final List<LevelProviderHandle> PROVIDERS = new ObjectArrayList<>(3);
    private static final Map<Class<? extends LevelProvider>, LevelProviderHandle> BY_CLASS = new IdentityHashMap<>(3);
    private static final Map<String, LevelProviderHandle> BY_NAME = new Object2ObjectOpenHashMap<>(3);

    static {
        addProvider(LEVELDB = LevelProviderHandle.builder()
                .clazz(LevelDB.class)
                .name(LevelDB.getProviderName())
                .order(LevelDB.getProviderOrder())
                .useSubChunk(LevelDB.usesChunkSection())
                .subChunkFactory(LevelDB::createChunkSection)
                .chunkFactory(LevelDbChunk::getEmptyChunk)
                .validator(LevelDB::isValid)
                .initializer(LevelDB::generate)
                .instantiator(LevelDB::new)
                .build());

        addProvider(ANVIL = LevelProviderHandle.builder()
                .clazz(Anvil.class)
                .name(Anvil.getProviderName())
                .order(Anvil.getProviderOrder())
                .useSubChunk(Anvil.usesChunkSection())
                .subChunkFactory(Anvil::createChunkSection)
                .chunkFactory(Chunk::getEmptyChunk)
                .validator(Anvil::isValid)
                .initializer(Anvil::generate)
                .instantiator(Anvil::new)
                .build());

        addProvider(MCREGION = LevelProviderHandle.builder()
                .clazz(McRegion.class)
                .name(McRegion.getProviderName())
                .order(McRegion.getProviderOrder())
                .useSubChunk(McRegion.usesChunkSection())
                .subChunkFactory(McRegion::createChunkSection)
                .chunkFactory(cn.nukkit.level.format.mcregion.Chunk::getEmptyChunk)
                .validator(McRegion::isValid)
                .initializer(McRegion::generate)
                .instantiator(McRegion::new)
                .build());

        DEFAULT = LEVELDB;
    }

    private static boolean addProvider(LevelProviderHandle handle) {
        if (BY_CLASS.putIfAbsent(handle.clazz, handle) != null) {
            return false;
        }
        BY_NAME.put(handle.name, handle);
        PROVIDERS.add(handle);
        return true;
    }

    @Nullable
    public static LevelProviderHandle getProvider(String path) {
        for (LevelProviderHandle handle : PROVIDERS) {
            try {
                if (handle.validator.isValid(path)) {
                    return handle;
                }
            } catch (Exception e) {
                Server.getInstance().getLogger().logException(e);
            }
        }
        return null;
    }

    @Nullable
    public static LevelProviderHandle getProviderByClass(Class<? extends LevelProvider> clazz) {
        return BY_CLASS.get(clazz);
    }

    @Nullable
    public static LevelProviderHandle getProviderByName(String name) {
        return BY_NAME.get(name);
    }

    public static void registerBuiltinProviders() {
    }

    @Builder
    @Value
    public static class LevelProviderHandle {
        Class<? extends LevelProvider> clazz;
        String name;
        byte order;
        boolean useSubChunk;
        SubChunkFactory subChunkFactory;
        ChunkFactory chunkFactory;
        LevelProviderValidator validator;
        LevelProviderInitializer initializer;
        LevelProviderInstantiator instantiator;
    }

    @FunctionalInterface
    public interface LevelProviderValidator {
        boolean isValid(String path);
    }

    @FunctionalInterface
    public interface LevelProviderInitializer {
        void generate(String path, String name, LevelCreationOptions options) throws IOException;

        default void generate(String path, String name) throws IOException {
            generate(path, name, LevelCreationOptions.builder().build());
        }
    }

    @FunctionalInterface
    public interface LevelProviderInstantiator {
        LevelProvider create(Level level, String path) throws IOException;
    }

    @FunctionalInterface
    public interface ChunkFactory {
        BaseFullChunk create(int chunkX, int chunkZ, @Nullable LevelProvider provider);

        default BaseFullChunk create(int chunkX, int chunkZ) {
            return create(chunkX, chunkZ, null);
        }
    }

    @FunctionalInterface
    public interface SubChunkFactory {
        ChunkSection create(int chunkY);
    }
}
