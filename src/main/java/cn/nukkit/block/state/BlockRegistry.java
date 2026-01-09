package cn.nukkit.block.state;

import cn.nukkit.block.Block;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class BlockRegistry {
    private final int version;
    private final Map<String, BlockLegacy> blocks = new HashMap<>();
    private final BlockLegacy[] byId = new BlockLegacy[Block.BLOCK_ID_COUNT];

    public BlockRegistry(int version) {
        this.version = version;
    }

    public BlockRegistry(int version, BlockRegistry copy) {
        this(version);
        blocks.putAll(copy.blocks);
    }

    public int getVersion() {
        return version;
    }

    public void createPermutations() {
        blocks.values().forEach(BlockLegacy::createPermutations);
    }

    public BlockLegacy registerBlock(String name, int id) {
        return registerBlock(name, id, new BlockLegacy(id, name));
    }

    public BlockLegacy registerBlock(String name, int id, BlockLegacy block) {
        if (byId[id] != null) {
            throw new IllegalArgumentException("Block " + id + " already exists");
        }
        if (blocks.putIfAbsent(name, block) != null) {
            throw new IllegalArgumentException("Block " + name + " already exists");
        }

        byId[id] = block;
        return block;
    }

    @Nullable
    public BlockLegacy getBlock(int id) {
        if (id < 0 || id >= byId.length) {
            return null;
        }
        return byId[id];
    }

    public void forEach(BiConsumer<String, BlockLegacy> action) {
        blocks.forEach(action);
    }
}
