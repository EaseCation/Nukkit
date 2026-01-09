package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockChestCopperExposed extends BlockChestCopper {
    BlockChestCopperExposed() {

    }

    @Override
    public int getId() {
        return EXPOSED_COPPER_CHEST;
    }

    @Override
    public String getName() {
        return "Exposed Copper Chest";
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
    public int getWaxedBlockId() {
        return WAXED_EXPOSED_COPPER_CHEST;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WEATHERED_COPPER_CHEST;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return COPPER_CHEST;
    }
}
