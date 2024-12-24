package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

public class BlockWallPolishedTuff extends BlockWall {
    public BlockWallPolishedTuff() {
        this(0);
    }

    public BlockWallPolishedTuff(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_TUFF_WALL;
    }

    @Override
    public String getName() {
        return "Polished Tuff Wall";
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
            return new Item[]{
                    toItem(true),
            };
        }
        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_TERRACOTA_BLOCK_COLOR;
    }
}
