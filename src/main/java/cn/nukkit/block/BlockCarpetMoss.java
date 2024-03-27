package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCarpetMoss extends BlockCarpet {
    public BlockCarpetMoss() {
        this(0);
    }

    public BlockCarpetMoss(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return MOSS_CARPET;
    }

    @Override
    public String getName() {
        return "Moss Carpet";
    }

    @Override
    public int getToolType() {
        return BlockToolType.HOE | BlockToolType.AXE;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GREEN_BLOCK_COLOR;
    }

    @Override
    public int getFullId() {
        return getId() << BLOCK_META_BITS;
    }

    @Override
    public void setDamage(int meta) {
    }
}
