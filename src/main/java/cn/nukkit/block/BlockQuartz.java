package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockQuartz extends BlockRotatedPillar {
    public static final int[] QUARTZ_BLOCKS = {
            QUARTZ_BLOCK,
            CHISELED_QUARTZ_BLOCK,
            QUARTZ_PILLAR,
            SMOOTH_QUARTZ,
    };

    public static final int NORMAL = 0;
    public static final int CHISELED = 1;
    public static final int PILLAR = 2;
    public static final int SMOOTH = 3;

    @Deprecated
    public static final int TYPE_MASK = 0b11;

    BlockQuartz() {

    }

    @Override
    public int getId() {
        return QUARTZ_BLOCK;
    }

    @Override
    public float getHardness() {
        return 0.8f;
    }

    @Override
    public float getResistance() {
        return 4;
    }

    @Override
    public String getName() {
        return "Block of Quartz";
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
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
