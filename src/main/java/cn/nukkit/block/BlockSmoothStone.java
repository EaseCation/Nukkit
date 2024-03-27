package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import static cn.nukkit.GameVersion.*;

public class BlockSmoothStone extends BlockSolid {

    public BlockSmoothStone() {
    }

    @Override
    public int getId() {
        return SMOOTH_STONE;
    }

    @Override
    public String getName() {
        return "Smooth Stone";
    }

    @Override
    public float getHardness() {
        if (V1_20_30.isAvailable()) {
            return 2;
        }
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
                    toItem(true)
            };
        }
        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.STONE_BLOCK_COLOR;
    }
}
