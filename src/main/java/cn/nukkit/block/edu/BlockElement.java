package cn.nukkit.block.edu;

import cn.nukkit.block.BlockSolid;
import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public abstract class BlockElement extends BlockSolid {

    protected BlockElement() {
        // meta has been removed in the latest version
    }

    @Override
    public float getHardness() {
        return 0;
    }

    @Override
    public float getResistance() {
        return 0;
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR; //TODO: check ElementBlock::getMapColor -- 07/30/2022
    }

    @Override
    public boolean isChemistryFeature() {
        return true;
    }
}
