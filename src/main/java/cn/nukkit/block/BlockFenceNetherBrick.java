package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/7 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockFenceNetherBrick extends BlockFence {

    public BlockFenceNetherBrick() {
        super(0);
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public String getName() {
        return "Nether Brick Fence";
    }

    @Override
    public int getId() {
        return NETHER_BRICK_FENCE;
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem(true)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public boolean canConnect(Block block, BlockFace face) {
        return block.getId() == NETHER_BRICK_FENCE || block.isFenceGate() || SupportType.hasFullSupport(block, face);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHER_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }

    @Override
    public int getFuelTime() {
        return 0;
    }

    @Override
    public int getFullId() {
        return getId() << BLOCK_META_BITS;
    }

    @Override
    public void setDamage(int meta) {
    }
}
