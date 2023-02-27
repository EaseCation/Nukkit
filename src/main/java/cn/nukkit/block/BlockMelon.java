package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.BlockColor;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 2015/12/11 by Pub4Game.
 * Package cn.nukkit.block in project Nukkit .
 */

public class BlockMelon extends BlockSolid {

    public BlockMelon() {
    }

    @Override
    public int getId() {
        return MELON_BLOCK;
    }

    public String getName() {
        return "Melon Block";
    }

    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public Item[] getDrops(Item item) {
        Random random = ThreadLocalRandom.current();
        int count = 3 + random.nextInt(5);

        Enchantment fortune = item.getEnchantment(Enchantment.FORTUNE);
        if (fortune != null && fortune.getLevel() >= 1) {
            count += random.nextInt(fortune.getLevel() + 1);
        }

        return new Item[]{
                Item.get(Item.MELON_SLICE, 0, Math.min(9, count))
        };
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LIME_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }
}
