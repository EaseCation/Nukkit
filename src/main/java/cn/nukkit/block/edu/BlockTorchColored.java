package cn.nukkit.block.edu;

import cn.nukkit.block.BlockTorch;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

import static cn.nukkit.GameVersion.*;

public abstract class BlockTorchColored extends BlockTorch {

    public static final int COLOR_BIT = 0b1000;

    protected BlockTorchColored() {
        this(0);
    }

    protected BlockTorchColored(int meta) {
        super(meta);
    }

    @Override
    public boolean isStackedByData() {
        return !V1_21_30.isAvailable();
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId(), getDamage() & COLOR_BIT);
    }

    @Override
    protected void setBlockFace(BlockFace face) {
        setDamage((6 - face.getIndex()) | (getDamage() & COLOR_BIT));
    }

    @Override
    public boolean isChemistryFeature() {
        return true;
    }
}
