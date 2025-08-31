package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
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

    public float getHardness() {
        return 1;
    }

    @Override
    public float getResistance() {
        return 5;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        Random random = ThreadLocalRandom.current();
        int count = 3 + random.nextInt(5);

        int fortune = item.getId() != Item.ENCHANTED_BOOK ? item.getValidEnchantmentLevel(Enchantment.FORTUNE) : 0;
        if (fortune > 0) {
            count += random.nextInt(fortune + 1);
        }

        return new Item[]{
                Item.get(Item.MELON_SLICE, 0, Math.min(9, count))
        };
    }

    @Override
    public int getToolType() {
        return BlockToolType.AXE | BlockToolType.SWORD;
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
    public int getCompostableChance() {
        return 65;
    }

    @Override
    public boolean isVegetation() {
        return true;
    }
}
