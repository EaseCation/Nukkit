package cn.nukkit.block;

public class BlockPistonHeadSticky extends BlockPistonHead {

    BlockPistonHeadSticky() {

    }

    @Override
    public int getId() {
        return STICKY_PISTON_ARM_COLLISION;
    }

    @Override
    public String getName() {
        return "Sticky Piston Head";
    }
}
