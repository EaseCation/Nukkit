package cn.nukkit.block.state;

import cn.nukkit.block.BlockID;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class BlockRegistry {
    private final int version;
    private final Map<String, BlockLegacy> blocks = new HashMap<>();
    private final BlockLegacy[] byId = new BlockLegacy[BlockID.UNDEFINED];

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
        if (byId[id] != null) {
            throw new IllegalArgumentException("Block " + id + " already exists");
        }

        BlockLegacy block = new BlockLegacy(id, name);
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
}
