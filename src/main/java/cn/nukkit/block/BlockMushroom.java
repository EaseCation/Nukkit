package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.object.mushroom.BigMushroom;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.LocalRandom;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public abstract class BlockMushroom extends BlockFlowable {

    BlockMushroom() {

    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!canStay()) {
                getLevel().useBreakOn(this, true);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (canStay()) {
            getLevel().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, float fx, float fy, float fz, Player player) {
        if (item.isFertilizer()) {
            if (player != null && (player.gamemode & 0x01) == 0) {
                item.count--;
            }

            if (item.is(Item.RAPID_FERTILIZER) || player != null && player.isCreative() || ThreadLocalRandom.current().nextFloat() < 0.4f) {
                this.grow();
            }

            this.level.addParticle(new BoneMealParticle(this));
            return true;
        }

        if (item.is(ItemBlockID.SNOW_LAYER)) {
            level.setExtraBlock(this, this, true, false);
            level.setBlock(this, get(SNOW_LAYER, BlockSnowLayer.COVERED_BIT), true);

            if (player != null && player.isSurvivalLike()) {
                item.pop();
            }

            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_PLACE, getFullId(SNOW_LAYER, BlockSnowLayer.COVERED_BIT));
            return true;
        }

        return false;
    }

    @Override
    public boolean isFertilizable() {
        return true;
    }

    public boolean grow() {
        this.level.setBlock(this, Block.get(BlockID.AIR), true, false);

        BigMushroom generator = new BigMushroom(getType());

        if (generator.generate(this.level, new LocalRandom(), this.asBlockVector3())) {
            return true;
        } else {
            this.level.setBlock(this, this, true, false);
            return false;
        }
    }

    public boolean canStay() {
        Block block = this.down();
        return block.getId() == MYCELIUM || block.getId() == PODZOL || (!block.isTransparent() /*&& this.level.getFullLight(this) < 13*/); //TODO: light
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PLANT_BLOCK_COLOR;
    }

    @Override
    public boolean canContainSnow() {
        return true;
    }

    @Override
    public int getCompostableChance() {
        return 65;
    }

    @Override
    public boolean isPottable() {
        return true;
    }

    protected abstract int getType();

    @Override
    public boolean isVegetation() {
        return true;
    }
}
