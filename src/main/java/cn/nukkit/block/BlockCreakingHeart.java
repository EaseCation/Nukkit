package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockCreakingHeart extends BlockRotatedPillar {
    public static final int ACTIVE_BIT = 0b100;
    public static final int NATURAL_BIT = 0b1000;

    public BlockCreakingHeart() {
        this(0);
    }

    public BlockCreakingHeart(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CREAKING_HEART;
    }

    @Override
    public String getName() {
        return "Creaking Heart";
    }

    @Override
    public float getHardness() {
        return 10;
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        return new Item[]{
                Item.get(getItemId(RESIN_CLUMP), 0, ThreadLocalRandom.current().nextInt(1, 4)),
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
