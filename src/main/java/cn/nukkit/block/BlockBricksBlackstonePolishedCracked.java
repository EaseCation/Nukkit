package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockBricksBlackstonePolishedCracked extends BlockSolid {
    public BlockBricksBlackstonePolishedCracked() {
    }

    @Override
    public int getId() {
        return CRACKED_POLISHED_BLACKSTONE_BRICKS;
    }

    @Override
    public String getName() {
        return "Cracked Polished Blackstone Bricks";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem(true),
            };
        }
        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.1f;
    }
}
