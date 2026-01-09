package cn.nukkit.block;

public class BlockPoppy extends BlockFlower {
    BlockPoppy() {

    }

    @Override
    public int getId() {
        return POPPY;
    }

    @Override
    public String getName() {
        return "Poppy";
    }

    @Override
    protected int getUncommonFlowerId() {
        return DANDELION;
    }
}
