package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.BlastFurnaceInventory;
import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class BlockEntityBlastFurnace extends BlockEntityFurnace {

    public BlockEntityBlastFurnace(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getTypeId() {
        return BlockEntityType.BLAST_FURNACE;
    }

    @Override
    public BlastFurnaceInventory getInventory() {
        return (BlastFurnaceInventory) inventory;
    }

    @Override
    protected int getUnlitBlockId() {
        return BlockID.BLAST_FURNACE;
    }

    @Override
    protected int getLitBlockId() {
        return BlockID.LIT_BLAST_FURNACE;
    }

    @Override
    protected CompoundTag createSpawnTag() {
        return getDefaultCompound(this, BLAST_FURNACE);
    }

    @Override
    protected RecipeTag getRecipeTag() {
        return RecipeTag.BLAST_FURNACE;
    }

    @Override
    protected int getBurnInterval() {
        return BURN_INTERVAL / 2;
    }

    @Override
    protected BlastFurnaceInventory createInventory() {
        return new BlastFurnaceInventory(this);
    }

    @Override
    protected int getLitSoundEvent() {
        return LevelSoundEventPacket.SOUND_BLOCK_BLASTFURNACE_FIRE_CRACKLE;
    }
}
