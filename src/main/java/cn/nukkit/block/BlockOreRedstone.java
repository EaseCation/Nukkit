package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockOreRedstone extends BlockSolid {

    public BlockOreRedstone() {
    }

    @Override
    public int getId() {
        return REDSTONE_ORE;
    }

    @Override
    public float getHardness() {
        return 3;
    }

    @Override
    public float getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public String getName() {
        return "Redstone Ore";
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_IRON) {
            Random random = ThreadLocalRandom.current();
            int count = random.nextInt(2) + 4;

            Enchantment fortune = item.getEnchantment(Enchantment.FORTUNE);
            if (fortune != null && fortune.getLevel() >= 1) {
                count += random.nextInt(fortune.getLevel() + 1);
            }

            return new Item[]{
                    Item.get(Item.REDSTONE, 0, count)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_TOUCH) { //type == Level.BLOCK_UPDATE_NORMAL ||
            this.getLevel().setBlock(this, Block.get(getLitBlockId()), true, false);

            return Level.BLOCK_UPDATE_WEAK;
        }

        return 0;
    }

    @Override
    public int getDropExp() {
        return ThreadLocalRandom.current().nextInt(1, 6);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    protected int getLitBlockId() {
        return LIT_REDSTONE_ORE;
    }
}
