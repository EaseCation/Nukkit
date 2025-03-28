package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.block.Blocks;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private final Int2ObjectMap<CompoundTag> runtimeIdToState = new Int2ObjectOpenHashMap<>();
    private final AtomicInteger runtimeIdAllocator = new AtomicInteger(0);
    public final byte[] BLOCK_PALETTE;

    public GlobalBlockPalette() {
        legacyToRuntimeId.defaultReturnValue(-1);
        runtimeIdToLegacy.defaultReturnValue(-1);

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

            if (!state.contains("LegacyStates")) continue;

            List<CompoundTag> legacyStates = state.getList("LegacyStates", CompoundTag.class).getAll();

            // Resolve to first legacy id
            CompoundTag firstState = legacyStates.get(0);
            runtimeIdToLegacy.put(runtimeId, firstState.getInt("id") << 6 | firstState.getShort("val"));

            for (CompoundTag legacyState : legacyStates) {
                int legacyId = legacyState.getInt("id") << 6 | legacyState.getShort("val");
                legacyToRuntimeId.putIfAbsent(legacyId, runtimeId);
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

    public static int getOrCreateRuntimeIdGeneral(int id, int meta) {
        return instance.getOrCreateRuntimeIdGeneral0(id, meta);
    }

    public static int getOrCreateRuntimeIdGeneral(int legacyId) throws NoSuchElementException {
        return instance.getOrCreateRuntimeIdGeneral0(legacyId);
    }

    public static int getLegacyId(int runtimeId) {
        return instance.getLegacyId0(runtimeId);
    }

    public static GlobalBlockPaletteInterface getStaticBlockPalette(StaticVersion version) {
        return instance.getStaticBlockPalette0(version);
    }

//    private static final Object2IntMap<String> stringToId;
//    private static final String[] idToString = new String[Block.BLOCK_ID_COUNT];

    private static final Object2ObjectMap<String, String> legacyToNew;
    private static final Object2ObjectMap<String, String> newToLegacy = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectMap<String, String> legacyToNewExtend;
    private static final Object2ObjectMap<String, String> newExtendToLegacy = new Object2ObjectOpenHashMap<>();

    static {
        Gson gson = new Gson();

/*
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("block_id_map.json");
             InputStreamReader reader = new InputStreamReader(stream)) {
            stringToId = gson.fromJson(reader, Object2IntOpenHashMap.class);
        } catch (NullPointerException | IOException e) {
            throw new AssertionError("Unable to load block_id_map.json", e);
        }
        stringToId.forEach((name, id) -> {
            if (id >= BlockID.UNDEFINED) {
                log.debug("Skip unsupported block: {}", id);
                return;
            }
            idToString[id] = name;
        });
        stringToId.defaultReturnValue(-1);
*/

        // 1.18.30+
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream("block_rename_map.json");
             InputStreamReader reader = new InputStreamReader(stream)) {
            legacyToNew = gson.fromJson(reader, Object2ObjectOpenHashMap.class);
        } catch (NullPointerException | IOException e) {
            throw new AssertionError("Unable to load block_rename_map.json", e);
        }
        legacyToNew.forEach((oldName, newName) -> newToLegacy.put(newName, oldName));

        // 1.19.0+
        try (InputStreamReader reader = new InputStreamReader(Server.class.getClassLoader().getResourceAsStream("block_rename_map_extend.json"))) {
            legacyToNewExtend = gson.fromJson(reader, Object2ObjectOpenHashMap.class);
        } catch (NullPointerException | IOException e) {
            throw new AssertionError("Unable to load block_rename_map_extend.json", e);
        }
        legacyToNewExtend.forEach((oldName, newName) -> newExtendToLegacy.put(newName, oldName));
    }

    public static int getBlockIdByName(String blockName) {
//        int blockId = stringToId.getInt(blockName);
//        if (blockId == -1) {
//            throw new NoSuchElementException("Unmapped block name: " + blockName);
//            return -1;
//        }
//        return blockId;
        return Blocks.getIdByBlockName(blockName, true);
//        return stringToId.getInt(blockName);
    }

    public static int getBlockIdByNewName(String blockName) {
        String legacyBlockName = newToLegacy.get(blockName);
        return getBlockIdByName(legacyBlockName != null ? legacyBlockName : blockName);
    }

    public static int getBlockIdByNewName1190(String blockName) {
        String legacyBlockName = newExtendToLegacy.get(blockName);
        if (legacyBlockName != null) {
            return getBlockIdByName(legacyBlockName);
        }
        return getBlockIdByNewName(blockName);
    }

    @Nullable
    public static String getNameByBlockId(int blockId) {
/*
        if (blockId < 0 || blockId >= BlockID.UNDEFINED) {
            return null;
        }
        return idToString[blockId];
*/
        return Blocks.getBlockFullNameById(blockId);
        /*String blockName = idToString[blockId];
        if (blockName == null) {
//            throw new NoSuchElementException("Unmapped block id: " + blockId);
            return null;
//            return "minecraft:info_update";
        }
        return blockName;*/
    }

    @Nullable
    public static String getNewNameByBlockId(int blockId) {
        String legacyBlockName = getNameByBlockId(blockId);
        if (legacyBlockName == null) {
            return null;
        }
        String newBlockName = legacyToNew.get(legacyBlockName);
        return newBlockName != null ? newBlockName : legacyBlockName;
    }

    @Nullable
    public static String getNewNameByBlockId1190(int blockId) {
        String legacyBlockName = getNameByBlockId(blockId);
        if (legacyBlockName == null) {
            return null;
        }
        String newBlockName = legacyToNewExtend.get(legacyBlockName);
        if (newBlockName != null) {
            return newBlockName;
        }
        newBlockName = legacyToNew.get(legacyBlockName);
        return newBlockName != null ? newBlockName : legacyBlockName;
    }

    //TODO: move to BlockConverter
}
