package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;

public class BlockDeepslateInfested extends BlockDeepslate {
    public BlockDeepslateInfested() {
        this(0);
    }

    public BlockDeepslateInfested(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return INFESTED_DEEPSLATE;
    }

    @Override
    public String getName() {
        return "Infested Deepslate";
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 3.75;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN && item.hasEnchantment(Enchantment.SILK_TOUCH)) {
            return new Item[]{
                    Item.get(getItemId(DEEPSLATE)),
            };
        }
        return new Item[0];
    }
}
