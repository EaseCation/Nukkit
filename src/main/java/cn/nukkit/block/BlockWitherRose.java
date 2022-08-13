package cn.nukkit.block;

public class BlockWitherRose extends BlockFlower {

    public BlockWitherRose() {
        this(0);
    }

    public BlockWitherRose(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return WITHER_ROSE;
    }

    @Override
    public String getName() {
        return "Wither Rose";
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public int getFullId() {
        return this.getId() << BLOCK_META_BITS;
    }

    @Override
    public void setDamage(int meta) {
    }

    @Override
    protected Block getUncommonFlower() {
        return this;
    }
}
