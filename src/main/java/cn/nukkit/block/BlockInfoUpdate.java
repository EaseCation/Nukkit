package cn.nukkit.block;

public class BlockInfoUpdate extends BlockSolid {

    public BlockInfoUpdate() {
    }

    @Override
    public int getId() {
        return INFO_UPDATE;
    }

    @Override
    public String getName() {
        return "update!";
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 5;
    }
}
