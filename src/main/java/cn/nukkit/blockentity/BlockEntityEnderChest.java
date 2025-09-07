package cn.nukkit.blockentity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.HashSet;
import java.util.Set;

public class BlockEntityEnderChest extends BlockEntitySpawnable {
    private final Set<Player> viewers = new HashSet<>();

    public BlockEntityEnderChest(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.ENDER_CHEST;
    }

    @Override
    public boolean isValidBlock(int blockId) {
        return blockId == Block.ENDER_CHEST;
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return getDefaultCompound(this, ENDER_CHEST)
                .putList(new ListTag<>("Items"));
    }

    public Set<Player> getViewers() {
        return viewers;
    }
}
