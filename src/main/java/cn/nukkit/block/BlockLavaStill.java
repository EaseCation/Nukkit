package cn.nukkit.block;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockLavaStill extends BlockLava {

    BlockLavaStill() {

    }

    @Override
    public int getId() {
        return LAVA;
    }

    @Override
    public String getName() {
        return "Lava";
    }

    @Override
    public BlockLiquid getBlock(int meta) {
        return (BlockLiquid) Block.get(BlockID.LAVA, meta);
    }
}
