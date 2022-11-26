package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.event.block.BlockFallEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * author: rcsuperman
 * Nukkit Project
 */
public abstract class BlockFallable extends BlockSolid {

    protected BlockFallable() {
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (canSlide(down())) {
                BlockFallEvent event = new BlockFallEvent(this);
                this.level.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return 0;
                }

                //TODO: sync
                this.level.setBlock(this, Block.get(Block.AIR), true, true);

                CompoundTag nbt = Entity.getDefaultNBT(this.add(0.5, 0, 0.5))
                        .putInt("TileID", this.getId())
                        .putByte("Data", this.getDamage());
                EntityFallingBlock fall = (EntityFallingBlock) Entity.createEntity("FallingSand", getChunk(), nbt);
                if (fall != null) {
                    fall.sync = true;
                    fall.spawnToAll();
                }
            }
            return type;
        }
        return 0;
    }

    @Override
    public boolean isHeavyBlock() {
        return true;
    }

    protected boolean canSlide(Block below) {
        int id = below.getId();
        return below.isAir() || below.isLiquid() || below.isFire()
                || !SupportType.hasFullSupport(below, BlockFace.UP) && below.isVegetation() && !below.isLeaves()
                && id != CACTUS && id != WATERLILY && id != CHORUS_FLOWER && id != CHORUS_PLANT && id != BAMBOO;
    }
}
