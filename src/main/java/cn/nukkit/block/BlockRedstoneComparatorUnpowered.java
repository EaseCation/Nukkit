package cn.nukkit.block;

/**
 * @author CreeperFace
 */
public class BlockRedstoneComparatorUnpowered extends BlockRedstoneComparator {

    BlockRedstoneComparatorUnpowered() {

    }

    @Override
    public int getId() {
        return UNPOWERED_COMPARATOR;
    }

    @Override
    public String getName() {
        return "Comparator Block Unpowered";
    }

    @Override
    protected BlockRedstoneComparator getUnpowered() {
        return this;
    }
}