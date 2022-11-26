package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockAncientDebris extends BlockSolid {
    public BlockAncientDebris() {
    }

    @Override
    public int getId() {
        return ANCIENT_DEBRIS;
    }

    @Override
    public String getName() {
        return "Ancient Debris";
    }

    @Override
    public double getHardness() {
        return 30;
    }

    @Override
    public double getResistance() {
        return 3600;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_DIAMOND) {
            return new Item[]{
                    toItem(true),
            };
        }
        return new Item[0];
    }

    @Override
    public boolean isFireResistant() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIAMOND_BLOCK_COLOR;
    }
}
