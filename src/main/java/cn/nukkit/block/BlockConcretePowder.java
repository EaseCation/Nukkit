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

    public BlockConcretePowder() {
        this(0);
    }

    public BlockConcretePowder(int meta) {
        super(meta);
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
                    this.level.setBlock(this, Block.get(BlockConcrete.CONCRETES[getDamage() & 0xf]), true, true);
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
            this.level.setBlock(this, Block.get(BlockConcrete.CONCRETES[getDamage() & 0xf]), true, true);
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
