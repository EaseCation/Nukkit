package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

/**
 * Created by CreeperFace on 2.6.2017.
 */
public abstract class BlockConcretePowder extends BlockFallable {
    public static final int[] CONCRETE_POWDERS = {
            WHITE_CONCRETE_POWDER,
            ORANGE_CONCRETE_POWDER,
            MAGENTA_CONCRETE_POWDER,
            LIGHT_BLUE_CONCRETE_POWDER,
            YELLOW_CONCRETE_POWDER,
            LIME_CONCRETE_POWDER,
            PINK_CONCRETE_POWDER,
            GRAY_CONCRETE_POWDER,
            LIGHT_GRAY_CONCRETE_POWDER,
            CYAN_CONCRETE_POWDER,
            PURPLE_CONCRETE_POWDER,
            BLUE_CONCRETE_POWDER,
            BROWN_CONCRETE_POWDER,
            GREEN_CONCRETE_POWDER,
            RED_CONCRETE_POWDER,
            BLACK_CONCRETE_POWDER,
    };

    @Override
    public float getResistance() {
        return 2.5f;
    }

    @Override
    public float getHardness() {
        return 0.5f;
    }

    @Override
    public int getToolType() {
        return BlockToolType.SHOVEL;
    }
    
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (super.onUpdate(type) == type) {
                return type;
            }

            for (int side = 1; side <= 5; side++) {
                Block block = this.getSide(BlockFace.fromIndex(side));
                if (block.isWater()) {
                    this.level.setBlock(this, Block.get(getConcreteBlockId()), true, true);
                }
            }

            return Level.BLOCK_UPDATE_NORMAL;
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block b, Block target, BlockFace face, float fx, float fy, float fz, Player player) {
        boolean concrete = false;

        for (int side = 1; side <= 5; side++) {
            Block block = this.getSide(BlockFace.fromIndex(side));
            if (block.isWater()) {
                concrete = true;
                break;
            }
        }

        if (concrete) {
            this.level.setBlock(this, Block.get(getConcreteBlockId()), true, true);
        } else {
            this.level.setBlock(this, this, true, true);
        }

        return true;
    }

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    public abstract DyeColor getDyeColor();

    protected abstract int getConcreteBlockId();

    @Override
    public String getDescriptionId() {
        return "tile.concretePowder." + getDyeColor().getDescriptionName() + ".name";
    }
}
