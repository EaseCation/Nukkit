package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * AMAZING COARSE DIRT added by kvetinac97
 * Nukkit Project
 */
public class BlockDirt extends BlockSolidMeta {

    public BlockDirt() {
        this(0);
    }

    public BlockDirt(int meta){
        super(meta);
    }

    @Override
    public int getId() {
        return DIRT;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        return this.getDamage() == 0 ? "Dirt" : "Coarse Dirt";
    }

    @Override
    public boolean onActivate(Item item, BlockFace face, Player player) {
        if (item.isHoe()) {
            if (this.up().isAir()) {
                if (player != null && !player.isCreative()) {
                    item.useOn(this);
                }
                this.getLevel().setBlock(this, this.getDamage() == 0 ? get(FARMLAND) : get(DIRT), true);
                return true;
            }
        } else if (item.isShovel()) {
            if (this.up().isAir()) {
                if (player != null && !player.isCreative()) {
                    item.useOn(this);
                }
                this.getLevel().setBlock(this, get(GRASS_PATH), true);
                return true;
            }
        } else if (item.getId() == ItemID.DYE && item.getDamage() == ItemDye.BONE_MEAL) {
            //TODO: generate seagrass, coral and coral fan
            return false;
        }

        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{new ItemBlock(Block.get(BlockID.DIRT))};
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
