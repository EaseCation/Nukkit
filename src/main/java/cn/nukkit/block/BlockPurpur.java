package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockPurpur extends BlockRotatedPillar {
    public static final int[] PURPUR_BLOCKS = {
            PURPUR_BLOCK,
            DEPRECATED_PURPUR_BLOCK_1,
            PURPUR_PILLAR,
            DEPRECATED_PURPUR_BLOCK_2,
    };

    public static final int NORMAL = 0;
    public static final int CHISELED = 1;
    public static final int PILLAR = 2;
    public static final int SMOOTH = 3;

    @Deprecated
    public static final int TYPE_MASK = 0b11;

    BlockPurpur() {

    }

    @Override
    public String getName() {
        return "Purpur Block";
    }

    @Override
    public int getId() {
        return PURPUR_BLOCK;
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
    public BlockColor getColor() {
        return BlockColor.MAGENTA_BLOCK_COLOR;
    }
}
