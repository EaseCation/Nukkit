package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

import static cn.nukkit.GameVersion.*;

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
        int type = BlockToolType.HOE | BlockToolType.SWORD;
        if (!V1_21_50.isAvailable()) {
            type |= BlockToolType.AXE;
        }
        return type;
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
    public int getCompostableChance() {
        return 30;
    }

    @Override
    public int getFullId() {
        return getId() << BLOCK_META_BITS;
    }

    @Override
    public void setDamage(int meta) {
    }
}
