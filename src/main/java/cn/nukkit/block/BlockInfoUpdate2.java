package cn.nukkit.block;

public class BlockInfoUpdate2 extends BlockSolid {

    public BlockInfoUpdate2() {
    }

    @Override
    public int getId() {
        return INFO_UPDATE2;
    }

    @Override
    public String getName() {
        return "ate!upd";
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
