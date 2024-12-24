package cn.nukkit.block;

import cn.nukkit.item.Item;

public class BlockPitcherPlant extends BlockDoublePlant {
    public static final int UPPER_BLOCK_BIT = 0b1;

    public BlockPitcherPlant() {
        this(0);
    }

    public BlockPitcherPlant(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return PITCHER_PLANT;
    }

    @Override
    public String getName() {
        return "Pitcher";
    }

    @Override
    public Item toItem(boolean addUserData) {
        return Item.get(getItemId());
    }

    @Override
    public int getCompostableChance() {
        return 85;
    }

    @Override
    public int getPlantType() {
        return -1;
    }

    @Override
    protected int getPlantTypeMask() {
        return 0;
    }

    @Override
    protected int getUpperBlockBitMask() {
        return UPPER_BLOCK_BIT;
    }
}
