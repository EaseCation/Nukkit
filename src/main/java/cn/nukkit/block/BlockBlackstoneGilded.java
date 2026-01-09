package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockBlackstoneGilded extends BlockSolid {
    BlockBlackstoneGilded() {

    }

    @Override
    public int getId() {
        return GILDED_BLACKSTONE;
    }

    @Override
    public String getName() {
        return "Gilded Blackstone";
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            if (random.nextInt(10) == 0) {
                return new Item[]{
                        Item.get(Item.GOLD_NUGGET, 0, random.nextInt(2, 6)),
                };
            }
            return new Item[]{
                    toItem(true),
            };
        }
        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLACK_BLOCK_COLOR;
    }
}
