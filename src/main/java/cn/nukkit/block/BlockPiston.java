package cn.nukkit.block;

/**
 * @author CreeperFace
 */
public class BlockPiston extends BlockPistonBase {

    BlockPiston() {

    }

    @Override
    public int getId() {
        return PISTON;
    }

    @Override
    public String getName() {
        return "Piston";
    }

    @Override
    protected boolean isSticky() {
        return false;
    }
}
