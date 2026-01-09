package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlockID;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabCopperCutWeatheredWaxed extends BlockDoubleSlabCopperCutWaxed {
    BlockDoubleSlabCopperCutWeatheredWaxed() {

    }

    @Override
    public int getId() {
        return WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public String getName() {
        return "Double Waxed Weathered Cut Copper Slab";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_STEM_BLOCK_COLOR;
    }

    @Override
    public int getCopperAge() {
        return 2;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(ItemBlockID.WAXED_WEATHERED_CUT_COPPER_SLAB);
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public int getDewaxedBlockId() {
        return WEATHERED_DOUBLE_CUT_COPPER_SLAB;
    }
}
