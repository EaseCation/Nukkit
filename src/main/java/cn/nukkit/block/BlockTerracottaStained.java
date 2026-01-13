package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TerracottaColor;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public abstract class BlockTerracottaStained extends BlockSolid {
    public static final int[] STAINED_TERRACOTTAS = {
            WHITE_TERRACOTTA,
            ORANGE_TERRACOTTA,
            MAGENTA_TERRACOTTA,
            LIGHT_BLUE_TERRACOTTA,
            YELLOW_TERRACOTTA,
            LIME_TERRACOTTA,
            PINK_TERRACOTTA,
            GRAY_TERRACOTTA,
            LIGHT_GRAY_TERRACOTTA,
            CYAN_TERRACOTTA,
            PURPLE_TERRACOTTA,
            BLUE_TERRACOTTA,
            BROWN_TERRACOTTA,
            GREEN_TERRACOTTA,
            RED_TERRACOTTA,
            BLACK_TERRACOTTA,
    };

    @Override
    public float getHardness() {
        return 1.25f;
    }

    @Override
    public float getResistance() {
        return 21;
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
            return new Item[]{toItem(true)};
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        return TerracottaColor.getByTerracottaData(getDyeColor().getWoolData()).getColor();
    }

    public abstract DyeColor getDyeColor();

    @Override
    public boolean isTerracotta() {
        return true;
    }

    @Override
    public String getDescriptionId() {
        return "tile.stained_hardened_clay." + getDyeColor().getDescriptionName() + ".name";
    }
}
