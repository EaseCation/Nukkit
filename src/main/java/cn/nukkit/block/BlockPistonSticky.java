package cn.nukkit.block;

/**
 * @author CreeperFace
 */
public class BlockPistonSticky extends BlockPistonBase {

    BlockPistonSticky() {

    }

    @Override
    public int getId() {
        return STICKY_PISTON;
    }

    @Override
    public String getName() {
        return "Sticky Piston";
    }

    @Override
    protected boolean isSticky() {
        return true;
    }
}
