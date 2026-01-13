package cn.nukkit.block;

public class BlockPurpurChiseled extends BlockPurpur {
    BlockPurpurChiseled() {

    }

    @Override
    public int getId() {
        return DEPRECATED_PURPUR_BLOCK_1;
    }

    @Override
    public String getName() {
        return "Chiseled Purpur";
    }

    @Override
    public String getDescriptionId() {
        return "tile.purpur_block.chiseled.name";
    }
}
