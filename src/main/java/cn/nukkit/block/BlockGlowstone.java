package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.Mth;
import cn.nukkit.utils.BlockColor;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 2015/12/6 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockGlowstone extends BlockTransparent {
    public BlockGlowstone() {
    }

    @Override
    public String getName() {
        return "Glowstone";
    }

    @Override
    public int getId() {
        return GLOWSTONE;
    }

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public double getHardness() {
        return 0.3;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public Item[] getDrops(Item item) {
        Random random = ThreadLocalRandom.current();
        int count = 2 + random.nextInt(3);

        Enchantment fortune = item.getId() != Item.ENCHANTED_BOOK ? item.getEnchantment(Enchantment.FORTUNE) : null;
        if (fortune != null && fortune.getLevel() >= 1) {
            count += random.nextInt(fortune.getLevel() + 1);
        }

        return new Item[]{
                Item.get(Item.GLOWSTONE_DUST, 0, Mth.clamp(count, 1, 4))
        };
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
