package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.level.HeightRange;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 2015/11/23 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public abstract class BlockFlower extends BlockFlowable {
    public static final int[] FLOWERS = {
            POPPY,
            BLUE_ORCHID,
            ALLIUM,
            AZURE_BLUET,
            RED_TULIP,
            ORANGE_TULIP,
            WHITE_TULIP,
            PINK_TULIP,
            OXEYE_DAISY,
            CORNFLOWER,
            LILY_OF_THE_VALLEY,
    };

    public static final int TYPE_POPPY = 0;
    public static final int TYPE_BLUE_ORCHID = 1;
    public static final int TYPE_ALLIUM = 2;
    public static final int TYPE_AZURE_BLUET = 3;
    public static final int TYPE_RED_TULIP = 4;
    public static final int TYPE_ORANGE_TULIP = 5;
    public static final int TYPE_WHITE_TULIP = 6;
    public static final int TYPE_PINK_TULIP = 7;
    public static final int TYPE_OXEYE_DAISY = 8;
    public static final int TYPE_CORNFLOWER = 9;
    public static final int TYPE_LILY_OF_THE_VALLEY = 10;

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        if (block.isLiquid() || !block.isAir() && block.canContainWater() && level.getExtraBlock(this).isWater()) {
            return false;
        }

        if (canSurvive()) {
            this.getLevel().setBlock(block, this, true);

            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!canSurvive()) {
                this.getLevel().useBreakOn(this, true);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PLANT_BLOCK_COLOR;
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

            this.level.addParticle(new BoneMealParticle(this));

            HeightRange heightRange = level.getHeightRange();
            ThreadLocalRandom random = ThreadLocalRandom.current();
            for (int i = 0; i < 8; i++) {
                Vector3 vec = this.add(
                        random.nextInt(-3, 4),
                        random.nextInt(-1, 2),
                        random.nextInt(-3, 4));

                if (level.getBlock(vec).getId() == AIR && level.getBlock(vec.down()).getId() == GRASS_BLOCK && vec.getY() >= heightRange.getMinY() && vec.getY() < heightRange.getMaxY()) {
                    int blockId = getUncommonFlowerId();
                    if (blockId != -1 && random.nextInt(10) == 0) {
                        this.level.setBlock(vec, get(blockId), true);
                    } else {
                        this.level.setBlock(vec, get(this.getId()), true);
                    }
                }
            }

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

    @Override
    public boolean canContainSnow() {
        return true;
    }

    @Override
    public int getCompostableChance() {
        return 65;
    }

    @Override
    public int getBurnChance() {
        return 60;
    }

    @Override
    public int getBurnAbility() {
        return 100;
    }

    @Override
    public boolean isPottable() {
        return true;
    }

    protected int getUncommonFlowerId() {
        return -1;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }

    protected boolean canSurvive() {
        int id = down().getId();
        return id == Block.GRASS_BLOCK || id == Block.DIRT || id == COARSE_DIRT || id == Block.FARMLAND || id == Block.PODZOL || id == MYCELIUM || id == DIRT_WITH_ROOTS || id == MOSS_BLOCK || id == PALE_MOSS_BLOCK || id == MUD || id == MUDDY_MANGROVE_ROOTS;
    }
}
