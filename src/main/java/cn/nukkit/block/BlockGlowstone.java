package cn.nukkit.block;

import cn.nukkit.Player;
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
    public float getResistance() {
        return 1.5f;
    }

    @Override
    public float getHardness() {
        return 0.3f;
    }

    @Override
    public int getLightLevel() {
        return 15;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        Random random = ThreadLocalRandom.current();
        int count = 2 + random.nextInt(3);

        int fortune = item.getId() != Item.ENCHANTED_BOOK ? item.getValidEnchantmentLevel(Enchantment.FORTUNE) : 0;
        if (fortune > 0) {
            count += random.nextInt(fortune + 1);
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
