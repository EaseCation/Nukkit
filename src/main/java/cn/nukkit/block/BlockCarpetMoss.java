package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

import static cn.nukkit.GameVersion.*;

public class BlockCarpetMoss extends BlockCarpet {
    BlockCarpetMoss() {

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
    public DyeColor getDyeColor() {
        return null;
    }

    @Override
    public int getCompostableChance() {
        return 30;
    }

    @Override
    public boolean isCarpet() {
        return false;
    }
}
