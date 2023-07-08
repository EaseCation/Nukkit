package cn.nukkit.block;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockLavaStill extends BlockLava {

    public BlockLavaStill() {
        super(0);
    }

    public BlockLavaStill(int meta) {
        super(meta);
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
