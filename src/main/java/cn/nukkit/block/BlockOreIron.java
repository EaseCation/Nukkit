package cn.nukkit.block;

import cn.nukkit.GameVersion;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockOreIron extends BlockSolid {

    public BlockOreIron() {
    }

    @Override
    public int getId() {
        return IRON_ORE;
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
        return "Iron Ore";
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_STONE) {
            if (GameVersion.V1_17_0.isAvailable()) {
                return new Item[]{
                        Item.get(Item.RAW_IRON),
                };
            }
            return new Item[]{
                    toItem(true)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
