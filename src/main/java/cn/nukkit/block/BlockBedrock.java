package cn.nukkit.block;

import cn.nukkit.item.Item;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockBedrock extends BlockSolidMeta {

    public BlockBedrock() {
        this(0);
    }

    public BlockBedrock(int meta) {
        // 0b1 infiniburn_bit
        super(meta & 0b1);
    }

    @Override
    public int getId() {
        return BEDROCK;
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public String getName() {
        return "Bedrock";
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBePulled() {
        return false;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
