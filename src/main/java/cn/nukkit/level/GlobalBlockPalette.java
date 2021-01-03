package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import com.google.common.io.ByteStreams;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class GlobalBlockPalette implements GlobalBlockPaletteInterface {

    private static GlobalBlockPaletteInterface instance = new GlobalBlockPalette();

    public static void setInstance(GlobalBlockPaletteInterface instance) {
        GlobalBlockPalette.instance = instance;
    }

    private final Int2IntMap legacyToRuntimeId = new Int2IntOpenHashMap();
    private final Int2IntMap runtimeIdToLegacy = new Int2IntOpenHashMap();
    private final Int2ObjectMap<String> legacyIdToString = new Int2ObjectOpenHashMap<>();
    private final Object2IntMap<String> stringToLegacyId = new Object2IntOpenHashMap<>();
    private final Int2ObjectMap<String> runtimeIdToString = new Int2ObjectOpenHashMap<>();
    private final Object2IntMap<String> stringToRuntimeId = new Object2IntOpenHashMap<>();
    private final Int2ObjectMap<CompoundTag> runtimeIdToState = new Int2ObjectOpenHashMap<>();
    private final AtomicInteger runtimeIdAllocator = new AtomicInteger(0);
    public final byte[] BLOCK_PALETTE;

    public GlobalBlockPalette() {
        legacyToRuntimeId.defaultReturnValue(-1);
        runtimeIdToLegacy.defaultReturnValue(-1);
        stringToRuntimeId.defaultReturnValue(-1);

        ListTag<CompoundTag> tag;
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("runtime_block_states.dat")) {
            if (stream == null) {
                throw new AssertionError("Unable to locate block state nbt");
            }
            //noinspection unchecked
            tag = (ListTag<CompoundTag>) NBTIO.readTag(new ByteArrayInputStream(ByteStreams.toByteArray(stream)), ByteOrder.LITTLE_ENDIAN, false);
        } catch (IOException e) {
            throw new AssertionError("Unable to load block palette", e);
        }

        for (CompoundTag state : tag.getAll()) {
            int runtimeId = runtimeIdAllocator.getAndIncrement();
            runtimeIdToState.put(runtimeId, state);

            String name = state.getCompound("block").getString("name");
            stringToRuntimeId.putIfAbsent(name, runtimeId);
            runtimeIdToString.putIfAbsent(runtimeId, name);

            if (!state.contains("LegacyStates")) continue;

            List<CompoundTag> legacyStates = state.getList("LegacyStates", CompoundTag.class).getAll();

            // Resolve to first legacy id
            CompoundTag firstState = legacyStates.get(0);
            runtimeIdToLegacy.put(runtimeId, firstState.getInt("id") << 6 | firstState.getShort("val"));

            stringToLegacyId.put(name, firstState.getInt("id"));
            legacyIdToString.put(firstState.getInt("id"), name);

            for (CompoundTag legacyState : legacyStates) {
                int legacyId = legacyState.getInt("id") << 6 | legacyState.getShort("val");
                legacyToRuntimeId.put(legacyId, runtimeId);
            }
            //state.remove("LegacyStates"); // No point in sending this since the client doesn't use it.
        }

        try {
            BLOCK_PALETTE = NBTIO.write(tag, ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException e) {
            throw new AssertionError("Unable to write block palette", e);
        }
    }

    @Override
    public int getOrCreateRuntimeId0(int id, int meta) {
        int legacyId = id << 6 | meta;
        int runtimeId = legacyToRuntimeId.get(legacyId);
        if (runtimeId == -1) {
            runtimeId = legacyToRuntimeId.get(id << 6);
            if (runtimeId == -1) {
                throw new NoSuchElementException("Unmapped block registered id:" + id + " meta:" + meta);
            }
        }
        return runtimeId;
    }

    @Override
    public int getOrCreateRuntimeId0(int legacyId) throws NoSuchElementException {
        return getOrCreateRuntimeId0(legacyId >> 4, legacyId & 0xf);
    }

    @Override
    public int getLegacyId0(int runtimeId) {
        return runtimeIdToLegacy.get(runtimeId);
    }

    @Override
    public String getNameByRuntimeId0(int runtimeId) {
        String name = runtimeIdToString.get(runtimeId);
        return name == null ? "minecraft:air" : name;
    }

    @Override
    public String getNameByBlockId0(int blockId) {
        return null;
    }

    @Override
    public int getBlockIdByName0(String name) {
        return stringToLegacyId.getInt(name);
    }

    public CompoundTag getState0(int runtimeId) {
        return runtimeIdToState.get(runtimeId);
    }

    //外部直接调用下面几个方法

    public static int getOrCreateRuntimeId(int id, int meta) {
        return instance.getOrCreateRuntimeId0(id, meta);
    }

    public static int getOrCreateRuntimeId(int legacyId) throws NoSuchElementException {
        return instance.getOrCreateRuntimeId0(legacyId);
    }

    public static int getLegacyId(int runtimeId) {
        return instance.getLegacyId0(runtimeId);
    }

    /**
     * 注意这里要求的参数是{@code runtimeId}, 而社区版是{@code blockId}.
     * 可以用{@link #getOrCreateRuntimeId(int, int)}将{@code blockId}转为{@code runtimeId}.
     */
    public static String getNameByRuntimeId(int runtimeId) {
        return instance.getNameByRuntimeId0(runtimeId);
    }

    public static String getNameByBlockId(int blockId) {
        return instance.getNameByBlockId0(blockId);
    }

    public static int getBlockIdByName(String name) {
        return instance.getBlockIdByName0(name);
    }
}
