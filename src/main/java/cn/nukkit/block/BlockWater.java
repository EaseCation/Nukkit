package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.LiquidFlowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockWater extends BlockLiquid {

    public BlockWater() {
        this(0);
    }

    public BlockWater(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FLOWING_WATER;
    }

    @Override
    public String getName() {
        return "Flowing Water";
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        boolean ret = this.getLevel().setBlock(this, this, true, false);
        this.getLevel().scheduleUpdate(this, this.tickRate());

        return ret;
    }

    @Override
    public BlockColor getColor() {
        //TODO: biome bland
        return BlockColor.WATER_BLOCK_COLOR;
    }

    @Override
    public BlockLiquid getBlock(int meta) {
        return (BlockLiquid) Block.get(BlockID.FLOWING_WATER, meta);
    }

    @Override
    public void onEntityCollide(Entity entity) {
        super.onEntityCollide(entity);

        if (entity.isOnFire()) {
            level.addLevelSoundEvent(entity.upVec(), LevelSoundEventPacket.SOUND_FIZZ);
            level.addLevelEvent(entity, LevelEventPacket.EVENT_PARTICLE_FIZZ_EFFECT, 513);
            entity.extinguish();
        }
    }

    @Override
    public int tickRate() {
        return 5;
    }

    @Override
    public boolean canContainWater() {
        return true;
    }

    @Override
    public boolean canContainFlowingWater() {
        return true;
    }

    @Override
    public boolean isWater() {
        return true;
    }

    @Override
    public boolean isWaterSource() {
        return isLiquidSource();
    }

    @Override
    public boolean isSameLiquid(Block block) {
        return block.isWater();
    }

    @Override
    protected boolean isLiquidContainer(Block block) {
        return block.canBeFlowedInto() || block.canContainWater() && !block.isWater(); //fixme
    }

    @Override
    protected void flowIntoBlock(Block block, int newFlowDecay) {
        Block extra = level.getExtraBlock(block);
        if (!canFlowInto(block) || block.isLiquid() || extra.isLiquid()) {
            return;
        }

        LiquidFlowEvent event = new LiquidFlowEvent(block, extra, this, newFlowDecay);
        level.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        if (block.canContainWater()) {
            Block newBlock = getBlock(newFlowDecay);
            if (block.isAir()) {
                level.setBlock(block, newBlock, true, true);
            } else if (block.canBeFlowedInto()) {
                level.useBreakOn(block, ItemTool.getUniversalTool(), null, needParticle(block));

                level.setBlock(block, newBlock, true, true);
            } else if (newBlock.isWaterSource() || block.canContainFlowingWater()) {
                level.setExtraBlock(block, newBlock, true, true);
            } else {
                return;
            }
        } else {
            if (!block.isAir()) {
                if (block.getId() == SNOW_LAYER && extra.canContainSnow()) {
                    level.useBreakOn(block, ItemTool.getUniversalTool());
                }
                level.useBreakOn(block, ItemTool.getUniversalTool(), null, needParticle(block));
            }

            level.setBlock(block, getBlock(newFlowDecay), true, true);
        }

        level.scheduleUpdate(block, tickRate());
    }

    private static boolean needParticle(Block block) {
        switch (block.getId()) {
            case CORAL:
            case CORAL_FAN:
            case CORAL_FAN_DEAD:
            case CORAL_FAN_HANG:
            case CORAL_FAN_HANG2:
            case CORAL_FAN_HANG3:
                return true;
        }
        return false;
    }
}
