package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFarmland extends BlockSolidMeta {

    public BlockFarmland() {
        this(0);
    }

    public BlockFarmland(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Farmland";
    }

    @Override
    public int getId() {
        return FARMLAND;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            Block up = up();

            if (up instanceof BlockCrops) {
                return 0;
            }

            if (up.isSolid() && !up.isTransparent()) {
                this.level.setBlock(this, Block.get(BlockID.DIRT), true, true);

                return Level.BLOCK_UPDATE_RANDOM;
            }

            boolean found = false;

            if (this.level.isRaining()) {
                found = true;
            } else {
                for (int x = (int) this.x - 4; x <= this.x + 4; x++) {
                    for (int z = (int) this.z - 4; z <= this.z + 4; z++) {
                        for (int y = (int) this.y; y <= this.y + 1; y++) {
                            if (z == this.z && x == this.x && y == this.y) {
                                continue;
                            }

                            Block block = this.level.getBlock(x, y, z);
                            if (block.isWater() || !block.isAir() && block.canContainWater() && level.getExtraBlock(x, y, z).isWater()) {
                                found = true;
                                break;
                            }
                        }
                    }
                }
            }

            if (found) {
                if (this.getDamage() < 7) {
                    this.setDamage(7);
                    this.level.setBlock(this, this, true, false);
                }
                return Level.BLOCK_UPDATE_RANDOM;
            }

            if (this.getDamage() > 0) {
                this.setDamage(this.getDamage() - 1);
                this.level.setBlock(this, this, true, false);
            } else {
                this.level.setBlock(this, Block.get(Block.DIRT), true, true);
            }

            return Level.BLOCK_UPDATE_RANDOM;
        }

        return 0;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(DIRT);
    }

    @Override
    public Item pick(boolean addUserData) {
        return Item.get(FARMLAND);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

    @Override
    public double getMaxY() {
        return this.y + 1 - 1.0 / 16;
    }
}
