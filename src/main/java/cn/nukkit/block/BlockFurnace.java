package cn.nukkit.block;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockFurnace extends BlockFurnaceBurning {

    BlockFurnace() {

    }

    @Override
    public String getName() {
        return "Furnace";
    }

    @Override
    public int getId() {
        return FURNACE;
    }

    @Override
    public int getLightLevel() {
        return 0;
    }
}