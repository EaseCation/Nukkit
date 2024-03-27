package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/11 by Pub4Game.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockRedstone extends BlockSolidMeta {

    public BlockRedstone() {
        this(0);
    }

    public BlockRedstone(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return REDSTONE_BLOCK;
    }

    @Override
    public float getResistance() {
        return 30;
    }

    @Override
    public float getHardness() {
        return 5;
    }

    @Override
    public int getToolType() {
        return BlockToolType.PICKAXE;
    }

    @Override
    public String getName() {
        return "Block of Redstone";
    }

    @Override
    public Item[] getDrops(Item item, Player player) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    toItem(true)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FIRE_BLOCK_COLOR;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace face) {
        return 15;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
