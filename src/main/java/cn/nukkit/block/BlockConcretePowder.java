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
public class BlockConcretePowder extends BlockFallable {
    private int meta;

    public BlockConcretePowder() {
        this(0);
    }

    public BlockConcretePowder(int meta) {
        this.meta = meta;
    }

    @Override
    public int getFullId() {
        return (getId() << BLOCK_META_BITS) | getDamage();
    }

    @Override
    public final int getDamage() {
        return this.meta;
    }

    @Override
    public final void setDamage(int meta) {
        this.meta = meta;
    }

    @Override
    public int getId() {
        return CONCRETE_POWDER;
    }

    @Override
    public String getName() {
        return getDyeColor().getName() + " Concrete Powder";
    }

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
                    this.level.setBlock(this, Block.get(Block.CONCRETE, this.meta), true, true);
                }
            }

            return Level.BLOCK_UPDATE_NORMAL;
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block b, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        boolean concrete = false;

        for (int side = 1; side <= 5; side++) {
            Block block = this.getSide(BlockFace.fromIndex(side));
            if (block.isWater()) {
                concrete = true;
                break;
            }
        }

        if (concrete) {
            this.level.setBlock(this, Block.get(Block.CONCRETE, this.getDamage()), true, true);
        } else {
            this.level.setBlock(this, this, true, true);
        }

        return true;
    }

    @Override
    public BlockColor getColor() {
        return getDyeColor().getColor();
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(getDamage());
    }
}
