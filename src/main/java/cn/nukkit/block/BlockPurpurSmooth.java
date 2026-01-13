package cn.nukkit.block;

public class BlockPurpurSmooth extends BlockPurpur {
    BlockPurpurSmooth() {

    }

    @Override
    public int getId() {
        return DEPRECATED_PURPUR_BLOCK_2;
    }

    @Override
    public String getName() {
        return "Smooth Purpur";
    }

    @Override
    public String getDescriptionId() {
        return "tile.purpur_block.smooth.name";
    }
}
