package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabCopperCutExposedWaxed extends BlockDoubleSlabCopperCutWaxed {
    BlockDoubleSlabCopperCutExposedWaxed() {

    }

    @Override
    public int getId() {
        return WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public String getName() {
        return "Double Waxed Exposed Cut Copper Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LIGHT_GRAY_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    public int getCopperAge() {
        return 1;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.WAXED_EXPOSED_CUT_COPPER_SLAB);
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public int getDewaxedBlockId() {
        return EXPOSED_DOUBLE_CUT_COPPER_SLAB;
    }
}
