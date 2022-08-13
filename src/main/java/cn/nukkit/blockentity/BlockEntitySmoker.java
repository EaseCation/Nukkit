package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.RecipeTag;
import cn.nukkit.inventory.SmokerInventory;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class BlockEntitySmoker extends BlockEntityFurnace {

    public BlockEntitySmoker(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public SmokerInventory getInventory() {
        return (SmokerInventory) inventory;
    }

    @Override
    protected int getUnlitBlockId() {
        return BlockID.SMOKER;
    }

    @Override
    protected int getLitBlockId() {
        return BlockID.LIT_SMOKER;
    }

    @Override
    protected CompoundTag createSpawnTag() {
        return getDefaultCompound(this, SMOKER);
    }

    @Override
    protected RecipeTag getRecipeTag() {
        return RecipeTag.SMOKER;
    }

    @Override
    protected int getBurnInterval() {
        return BURN_INTERVAL / 2;
    }

    @Override
    protected SmokerInventory createInventory() {
        return new SmokerInventory(this);
    }

    @Override
    protected int getLitSoundEvent() {
        return LevelSoundEventPacket.SOUND_BLOCK_SMOKER_SMOKE;
    }
}
