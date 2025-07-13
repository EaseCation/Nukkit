package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockDoorCopperExposed extends BlockDoorCopper {
    public BlockDoorCopperExposed() {
        this(0);
    }

    public BlockDoorCopperExposed(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return EXPOSED_COPPER_DOOR;
    }

    @Override
    public String getName() {
        return "Exposed Copper Door";
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
        return WAXED_EXPOSED_COPPER_DOOR;
    }

    @Override
    public int getIncrementAgeBlockId() {
        return WEATHERED_COPPER_DOOR;
    }

    @Override
    public int getDecrementAgeBlockId() {
        return COPPER_DOOR;
    }
}
