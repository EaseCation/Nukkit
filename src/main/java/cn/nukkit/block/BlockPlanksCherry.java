package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockPlanksCherry extends BlockPlanksAbstract {
    public BlockPlanksCherry() {
    }

    @Override
    public int getId() {
        return CHERRY_PLANKS;
    }

    @Override
    public String getName() {
        return "Cherry Planks";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WHITE_TERRACOTA_BLOCK_COLOR;
    }
}
