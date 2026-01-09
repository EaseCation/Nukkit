package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockStone extends BlockStoneAbstract {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_GRANITE = 1;
    public static final int TYPE_POLISHED_GRANITE = 2;
    public static final int TYPE_DIORITE = 3;
    public static final int TYPE_POLISHED_DIORITE = 4;
    public static final int TYPE_ANDESITE = 5;
    public static final int TYPE_POLISHED_ANDESITE = 6;

    BlockStone() {

    }

    @Override
    public int getId() {
        return STONE;
    }

    @Override
    public String getName() {
        return "Stone";
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    Item.get(ItemBlockID.COBBLESTONE)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }

    @Override
    public float getFurnaceXpMultiplier() {
        return 0.1f;
    }
}
