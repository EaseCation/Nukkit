package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockAmethyst extends BlockSolid {
    public BlockAmethyst() {
    }

    @Override
    public int getId() {
        return AMETHYST_BLOCK;
    }

    @Override
    public String getName() {
        return "Block of Amethyst";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public float getResistance() {
        return 7.5f;
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
        return BlockColor.PURPLE_BLOCK_COLOR;
    }
}
