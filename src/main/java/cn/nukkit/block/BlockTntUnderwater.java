package cn.nukkit.block;

public class BlockTntUnderwater extends BlockTNT {
    BlockTntUnderwater() {

    }

    @Override
    public int getId() {
        return UNDERWATER_TNT;
    }

    @Override
    public String getName() {
        return "Underwater TNT";
    }

    @Override
    protected boolean isAllowUnderwater() {
        return true;
    }

    @Override
    public boolean isChemistryFeature() {
        return true;
    }
}
